package com.flying.common.util;

import java.io.File;
import java.io.FileReader;
import java.io.LineNumberReader;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.flying.common.util.Utils;

/**
 * 计算代码行数
 * @author wanghaifeng
 * @create Sep 10, 2006 8:51:20 PM
 * 
 */
public class LineCounter {
	/**
	 * 根目录路径
	 * @property String:rootPath
	 */
	private String rootPath;
	
	/**
	 * 文件扩展名
	 * @property String[]:exts
	 */
	private String[] exts;
	
	/**
	 * 文件数量
	 * @property int:fileCount
	 */
	private int fileCount;
	
	/**
	 * 字符数量
	 * @property int:charCount
	 */
	private int charCount;
	
	public LineCounter(String rootPath, String[] exts){
		this.rootPath = rootPath;
		this.exts = exts;
	}
	
	public int count() throws Exception{
		return this.count(new File(this.rootPath), this.exts);
	}
	
	/**
	 * @modify wanghaifeng Oct 16, 2006 3:00:16 PM
	 * @param file
	 * @param exts
	 * @return
	 * @throws Exception
	 */
	public int count(File file, String[] exts) throws Exception{
		if(file.isFile()){
			String fn = file.getAbsolutePath();
			if(exts != null){
				boolean match = false;
				for(int  i=0; i< exts.length; i++){
					if(fn.toLowerCase().endsWith("."+exts[i].toLowerCase())){
						match = true;
						break;
					}
				}
				if(!match){
					return 0;
				}
			}
			int lineCount = 0;
			int charCount = 0;
			LineNumberReader reader = new LineNumberReader(new FileReader(file));
			try{
				String line = reader.readLine();
				while(line != null){
					lineCount ++;
					charCount += line.length();
					line = reader.readLine();
				}
			}catch(Exception e){
				lineCount ++;
			}
			fileCount ++;
			this.charCount += charCount;
			fn = fn.substring(this.rootPath.length());
			System.out.println("File<"+this.fileCount+"> \t" + padTrailing(fn, 100, " ")+padLeading("" + lineCount, 10, " ") + padLeading(""+charCount, 10, " "));
			return lineCount;
		} else {
			File[] subFiles = file.listFiles();
			int count = 0;
			if(subFiles != null)
				for(File f: subFiles){
					count += count(f, exts);
				}
			return count;
		}
	}

    public static String padTrailing(String rString, int rLength, String rPad) {
		String lTmpPad = "";
		String lTmpStr = "";
		lTmpStr = rString != null ? rString : "";
		if (lTmpStr.length() >= rLength)
			return lTmpStr.substring(0, lTmpStr.length());
		for (int gCnt = 1; gCnt <= rLength - lTmpStr.length(); gCnt++)
			lTmpPad = rPad + lTmpPad;

		return lTmpStr + lTmpPad;
	}

	public static String padLeading(String rString, int rLength, String rPad) {
		String lTmpPad = "";
		String lTmpStr = rString != null ? rString : "";
		if (lTmpStr.length() >= rLength)
			return lTmpStr.substring(0, lTmpStr.length());
		for (int gCnt = 1; gCnt <= rLength - lTmpStr.length(); gCnt++)
			lTmpPad = lTmpPad + rPad;

		return lTmpPad + lTmpStr;
	}
	
	public static void exec() throws Exception {
		byte[] buff = new byte[1024];
		int rc = 0;
		String rootPath = "";
		String[] exts = null;
		while(true){
			System.out.print("请输入操作类型：default为统计当前项目的代码行数，exit退出统计：");
			rc = System.in.read(buff);
			String op = new String(buff, 0, rc-2);
			if(StringUtils.equals(op, "exit")){
				break;
			} else if(StringUtils.equals("default", op)){
				
				rootPath = "E:\\osp";
				exts = new String[]{"java", "xml", "properties", "jsp"};
			} else{
				System.out.print("请输入根路径：");
				rc = System.in.read(buff);
				rootPath = new String(buff, 0, rc-2);
				System.out.println();
				System.out.print("请输入需要统计的文件的扩展名：");
				buff = new byte[1024];
				rc = System.in.read(buff);
				String extNames = new String(buff, 0, rc-2);
				exts = StringUtils.split(extNames, ",");
			}
			LineCounter lc = new LineCounter(rootPath, exts);
			int totalCount = lc.count();
			System.out.println("Total Line Count\t"+totalCount);
			System.out.println("Total Char Count\t"+lc.charCount);
		}
		System.out.println("End");
	}
    
	static class R implements Comparable<R> {
		String dir;
		String ext;
		int lineCount;
		int charCount;
		
		public int compareTo(R o) {
			if(dir.equals(o.dir)) {
				return ext.compareTo(o.ext);
			} else {
				return dir.compareTo(o.dir);
			}
		}
		public String toString() {
			return dir + "\t" + ext + "\t" + lineCount + "\t" + charCount;
		}
	}
	
	public static void main(String[] args) throws Exception{
		String[] dirs = {
				"E:\\osp\\admin",
				"E:\\osp\\api",
				"E:\\osp\\async", 
				"E:\\osp\\cms",
				"E:\\osp\\common",
				"E:\\osp\\example",
				"E:\\osp\\framework", 
				"E:\\osp\\lbs",
				"E:\\osp\\pas",
				"E:\\osp\\pps",
				"E:\\osp\\pps2",
				"E:\\osp\\product", 
				"E:\\osp\\security",
				"E:\\osp\\test"
				};
		List<R> list = Utils.newArrayList();
		for(String dir: dirs) {
		String[] exts = new String[]{"java","tpl","xml"};
			for(String ext: exts) {
				LineCounter lc = new LineCounter(dir, new String[]{ext});
				int totalCount = lc.count();
				R r = new R();
				r.dir = dir;
				r.ext = ext;
				r.lineCount = totalCount;
				r.charCount = lc.charCount;
				list.add(r);
			}
		}
		for(R r: list) {
			System.out.println(r);
		}
	}
}
