package com.flying.common.log.appender;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

import org.apache.log4j.FileAppender;
import org.apache.log4j.Layout;
import org.apache.log4j.helpers.CountingQuietWriter;
import org.apache.log4j.helpers.LogLog;
import org.apache.log4j.helpers.OptionConverter;
import org.apache.log4j.spi.ErrorCode;
import org.apache.log4j.spi.LoggingEvent;

import com.flying.common.config.ConfigUtils;

public class TimeSizeRollingFileAppender extends FileAppender implements ErrorCode {

	protected long maxFileSize = 10 * 1024 * 1024;

	protected int maxBackupIndex = 1;

	static final int TOP_OF_TROUBLE = -1;

	static final int TOP_OF_MINUTE = 0;

	static final int TOP_OF_HOUR = 1;

	static final int HALF_DAY = 2;

	static final int TOP_OF_DAY = 3;

	static final int TOP_OF_WEEK = 4;

	static final int TOP_OF_MONTH = 5;

	protected String datePattern = "'.'yyyy-MM-dd";

	protected String scheduledFilename;

	private long nextCheck = System.currentTimeMillis() - 1;

	Date now = new Date();

	SimpleDateFormat sdf;

	RollingCalendar rc = new RollingCalendar();

	int checkPeriod = TOP_OF_TROUBLE;

	static final TimeZone gmtTimeZone = TimeZone.getTimeZone("GMT");

	protected File currFile;

	public TimeSizeRollingFileAppender() {
	}

	public TimeSizeRollingFileAppender(Layout layout, String filename,
			String datePattern) throws IOException {
		super(layout, filename, true);
		this.datePattern = datePattern;
		activateOptions();
	}

	public void setDatePattern(String pattern) {
		datePattern = pattern;
	}

	public String getDatePattern() {
		return datePattern;
	}

	public int getMaxBackupIndex() {
		return maxBackupIndex;
	}

	public long getMaximumFileSize() {
		return maxFileSize;
	}

	/* (non-Javadoc)
	 * @see org.apache.log4j.FileAppender#setFile(java.lang.String)
	 * 完整路径
	 */
	public void setFile(String file) {
		fileName = file;
		LogLog.debug("fileName:" + fileName);

		int index = fileName.lastIndexOf(ConfigUtils.fileSeparator);
		if (index > 0) {
			String sPath = fileName.substring(0, index);
			File path = new File(sPath);
			if (!path.exists()) {
				path.mkdirs();
			}
		}

		LogLog.debug("File set:" + fileName);
	}

	public synchronized void setFile(String pFileName, boolean append,
			boolean bufferedIO, int bufferSize) throws IOException {
		try {
			reset();
			this.fileName = pFileName;
			LogLog.debug("setFile called: " + fileName + ", " + append);
			
			if (bufferedIO) {
				setImmediateFlush(false);
			}

			Writer fw = createWriter(new FileOutputStream(fileName, append));
			if (bufferedIO) {
				fw = new BufferedWriter(fw, bufferSize);
			}
			this.setQWForFiles(fw);
			this.fileAppend = append;
			this.bufferedIO = bufferedIO;
			this.bufferSize = bufferSize;
			writeHeader();

			if (append) {
				currFile = new File(fileName);
				((CountingQuietWriter) qw).setCount(currFile.length());
			}
			LogLog.debug("setFile ended");
		} catch (IOException e) {
			errorHandler.error("Create log File error", e, FILE_OPEN_FAILURE);
		}
	}

	public void activateOptions() {
		super.activateOptions();
		if (datePattern != null && fileName != null) {
			now.setTime(System.currentTimeMillis());
			sdf = new SimpleDateFormat(datePattern);
			int type = computeCheckPeriod();
			printPeriodicity(type);
			rc.setType(type);
			currFile = new File(fileName);
			scheduledFilename = fileName
					+ sdf.format(new Date(currFile.lastModified()));
			LogLog.debug("scheduledFilename generated:" + scheduledFilename);
		} else {
			LogLog
					.error("Either File or DatePattern options are not set for appender ["
							+ name + "].");
		}
	}

	public void setMaxBackupIndex(int maxBackups) {
		this.maxBackupIndex = maxBackups;
	}

	public void setMaxFileSize(String value) {
		maxFileSize = OptionConverter.toFileSize(value, maxFileSize + 1);
	}

	public void setMaximumFileSize(long value) {
		maxFileSize = value;
	}

	protected void setQWForFiles(Writer writer) {
		this.qw = new CountingQuietWriter(writer, errorHandler);
	}

	void printPeriodicity(int type) {
		switch (type) {
		case TOP_OF_MINUTE:
			LogLog.debug("Appender [" + name + "] to be rolled every minute.");
			break;
		case TOP_OF_HOUR:
			LogLog.debug("Appender [" + name
					+ "] to be rolled on top of every hour.");
			break;
		case HALF_DAY:
			LogLog.debug("Appender [" + name
					+ "] to be rolled at midday and midnight.");
			break;
		case TOP_OF_DAY:
			LogLog.debug("Appender [" + name + "] to be rolled at midnight.");
			break;
		case TOP_OF_WEEK:
			LogLog.debug("Appender [" + name
					+ "] to be rolled at start of week.");
			break;
		case TOP_OF_MONTH:
			LogLog.debug("Appender [" + name
					+ "] to be rolled at start of every month.");
			break;
		default:
			LogLog.warn("Unknown periodicity for appender [" + name + "].");
		}
	}

	int computeCheckPeriod() {
		RollingCalendar rollingCalendar = new RollingCalendar(gmtTimeZone,
				Locale.ENGLISH);
		Date epoch = new Date(0);
		if (datePattern != null) {
			for (int i = TOP_OF_MINUTE; i <= TOP_OF_MONTH; i++) {
				SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
						datePattern);
				simpleDateFormat.setTimeZone(gmtTimeZone); // do all date
															// formatting in GMT
				String r0 = simpleDateFormat.format(epoch);
				rollingCalendar.setType(i);
				Date next = new Date(rollingCalendar.getNextCheckMillis(epoch));
				String r1 = simpleDateFormat.format(next);
				LogLog.debug("Type = " + i + ", r0 = " + r0 + ", r1 = " + r1);
				if (r0 != null && r1 != null && !r0.equals(r1)) {
					return i;
				}
			}
		}
		return TOP_OF_TROUBLE; // Deliberately head for trouble...
	}

	/**
	 * Rollover the current file to a new file.
	 */
	public void rollOverForTime() throws IOException {

		/* Compute filename, but only if datePattern is specified */
		if (datePattern == null) {
			errorHandler.error("Missing DatePattern option in rollOver().");
			return;
		}

		String datedFilename = fileName + sdf.format(now);
		LogLog.debug("datedFilename:" + datedFilename);
		// It is too early to roll over because we are still within the
		// bounds of the current interval. Rollover will occur once the
		// next interval is reached.
		if (scheduledFilename.equals(datedFilename)) {
			return;
		}

		// close current file, and rename it to datedFilename
		this.closeFile();

		File target = new File(scheduledFilename + ConfigUtils.LOG_BACKUP_SUFFIX);
		if (target.exists()) {
			target.delete();
		}

		File file = new File(fileName);
		for (int i = 1; i <= maxBackupIndex; i++) {// roll for all size-backup
													// files
			String before = fileName + "." + i;
			File files = new File(before);
			String after = scheduledFilename + "." + i + ConfigUtils.LOG_BACKUP_SUFFIX;
			File targets = new File(after);
			if (targets.exists()) {
				targets.delete();
			}
			if (files.exists()) {// only backup existed one
				boolean result = files.renameTo(targets);
				if (result) {
					LogLog.debug(before + " -> " + after);
				} else {
					LogLog.error("Failed to rename [" + before + "] to ["
							+ after + "].");
				}
			} else {// if xxbiz.1 not exist,must no xxbiz.2
				break;
			}
		}
		// rename current writting file is necessary
		boolean result = file.renameTo(target);
		if (result) {
			LogLog.debug(fileName + " -> " + scheduledFilename);
		} else {
			LogLog.error("Failed to rename [" + fileName + "] to ["
					+ scheduledFilename + "].");
		}

		try {
			// This will also close the file. This is OK since multiple
			// close operations are safe.
			this.setFile(fileName, false, this.bufferedIO, this.bufferSize);
		} catch (IOException e) {
			errorHandler.error("setFile(" + fileName + ", false) call failed.");
		}
		scheduledFilename = datedFilename;
		LogLog.debug("scheduledFilename after roll:" + scheduledFilename);
	}

	/**
	 * Implements the usual roll over behaviour.
	 * <p>
	 * If <code>MaxBackupIndex</code> is positive, then files {
	 * <code>File.1</code>, ..., <code>File.MaxBackupIndex -1</code> are renamed
	 * to {<code>File.2</code>, ..., <code>File.MaxBackupIndex</code> .
	 * Moreover, <code>File</code> is renamed <code>File.1</code> and closed. A
	 * new <code>File</code> is created to receive further log output.
	 * <p>
	 * If <code>MaxBackupIndex</code> is equal to zero, then the
	 * <code>File</code> is truncated with no backup files created.
	 * synchronization not necessary since doAppend is alreasy synched
	 */
	public void rollOverForSize() {
		File target;
		File file;

		LogLog.debug("rolling over count="
				+ ((CountingQuietWriter) qw).getCount());
		LogLog.debug("maxBackupIndex=" + maxBackupIndex);
		// If maxBackups <= 0, then there is no file renaming to be done.
		if (maxBackupIndex > 0) {
			// Delete the oldest file, to keep Windows happy.
			file = new File(fileName + '.' + maxBackupIndex);
			if (file.exists())
				file.delete();

			// Map {(maxBackupIndex - 1), ..., 2, 1} to {maxBackupIndex, ..., 3,
			// 2}
			for (int i = maxBackupIndex - 1; i >= 1; i--) {
				file = new File(fileName + "." + i);
				if (file.exists()) {
					/*
					 * if (countForSize<i+1) {//only get the bigest countForSize
					 * = i + 1; //the max file Seq No }
					 */
					target = new File(fileName + '.' + (i + 1));
					LogLog.debug("Renaming file " + file + " to " + target);
					file.renameTo(target);
				}
			}

			// Rename fileName to fileName.1
			target = new File(fileName + "." + 1);
			// System.out.println("in rollOverForSize countForSize:"+countForSize);
			this.closeFile(); // keep windows happy.

			file = new File(fileName);
			LogLog.debug("Renaming file " + file + " to " + target);
			file.renameTo(target);
		}

		try {
			// This will also close the file. This is OK since multiple
			// close operations are safe.
			this.setFile(fileName, false, bufferedIO, bufferSize);
		} catch (IOException e) {
			LogLog.error("setFile(" + fileName + ", false) call failed.", e);
		}
	}

	/**
	 * This method differentiates TimeSizeRollingFileAppender from its super
	 * class.
	 * 
	 * <p>
	 * Before actually logging, this method will check whether it is time to do
	 * a rollover. If it is, it will schedule the next rollover time and then
	 * rollover.
	 * */
	protected void subAppend(LoggingEvent event) {
		long n = System.currentTimeMillis();
		if (n >= nextCheck) {
			now.setTime(n);
			nextCheck = rc.getNextCheckMillis(now);
			try {
				rollOverForTime();
			} catch (IOException ioe) {
				LogLog.error("rollOver() failed.", ioe);
			}
		}
		// time roll first
		if ((fileName != null)
				&& ((CountingQuietWriter) qw).getCount() >= maxFileSize) {
			rollOverForSize();
		}
		// @CheckItem@ OPT-yanfeng-20030909 remove file exist check while
		// logging for better effencience
		super.subAppend(event);
	}

	protected void directSubAppend(LoggingEvent event) {
		super.subAppend(event);
	}
}

@SuppressWarnings("serial")
class RollingCalendar extends GregorianCalendar {

	int type = TimeSizeRollingFileAppender.TOP_OF_TROUBLE;

	RollingCalendar() {
		super();
	}

	RollingCalendar(TimeZone tz, Locale locale) {
		super(tz, locale);
	}

	void setType(int type) {
		this.type = type;
	}

	public long getNextCheckMillis(Date now) {
		return getNextCheckDate(now).getTime();
	}

	public Date getNextCheckDate(Date now) {
		this.setTime(now);

		switch (type) {
		case TimeSizeRollingFileAppender.TOP_OF_MINUTE:
			this.set(Calendar.SECOND, 0);
			this.set(Calendar.MILLISECOND, 0);
			this.add(Calendar.MINUTE, 1);
			break;
		case TimeSizeRollingFileAppender.TOP_OF_HOUR:
			this.set(Calendar.MINUTE, 0);
			this.set(Calendar.SECOND, 0);
			this.set(Calendar.MILLISECOND, 0);
			this.add(Calendar.HOUR_OF_DAY, 1);
			break;
		case TimeSizeRollingFileAppender.HALF_DAY:
			this.set(Calendar.MINUTE, 0);
			this.set(Calendar.SECOND, 0);
			this.set(Calendar.MILLISECOND, 0);
			int hour = get(Calendar.HOUR_OF_DAY);
			if (hour < 12) {
				this.set(Calendar.HOUR_OF_DAY, 12);
			} else {
				this.set(Calendar.HOUR_OF_DAY, 0);
				this.add(Calendar.DAY_OF_MONTH, 1);
			}
			break;
		case TimeSizeRollingFileAppender.TOP_OF_DAY:
			this.set(Calendar.HOUR_OF_DAY, 0);
			this.set(Calendar.MINUTE, 0);
			this.set(Calendar.SECOND, 0);
			this.set(Calendar.MILLISECOND, 0);
			this.add(Calendar.DATE, 1);
			break;
		case TimeSizeRollingFileAppender.TOP_OF_WEEK:
			this.set(Calendar.DAY_OF_WEEK, getFirstDayOfWeek());
			this.set(Calendar.HOUR_OF_DAY, 0);
			this.set(Calendar.SECOND, 0);
			this.set(Calendar.MILLISECOND, 0);
			this.add(Calendar.WEEK_OF_YEAR, 1);
			break;
		case TimeSizeRollingFileAppender.TOP_OF_MONTH:
			this.set(Calendar.DATE, 1);
			this.set(Calendar.HOUR_OF_DAY, 0);
			this.set(Calendar.SECOND, 0);
			this.set(Calendar.MILLISECOND, 0);
			this.add(Calendar.MONTH, 1);
			break;
		default:
			throw new IllegalStateException("Unknown periodicity type.");
		}
		return getTime();
	}
}
