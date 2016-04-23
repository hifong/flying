package com.flying.framework.request.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Date;
import java.util.List;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

import com.flying.common.log.Logger;
import com.flying.common.util.Codes;
import com.flying.common.util.DateUtils;
import com.flying.common.util.ServiceHelper;
import com.flying.common.util.Utils;
import com.flying.framework.data.Data;
import com.flying.framework.event.EventPublisher;
import com.flying.framework.service.AbstractService;

public class UploadService extends AbstractService{
	private final static Logger logger = Logger.getLogger(UploadService.class);
	
	private List<String[]> services; //in config, pattern is: moduleid|serviceName:method;moduleid|serviceName:method
	
	private List<String[]> getServices() {
		if(services != null) return services;
		
		synchronized(this) {
			if(services != null) return services;
			
			services = Utils.newArrayList();
			final String configServices = this.serviceConfig.getConfig("services");
			if(configServices == null) return services;
			
			final String[] tmps1 = StringUtils.split(configServices, ";");
			for(String s: tmps1) {
				if(s == null) continue;
				final String[] tmps2 = StringUtils.split(s, "|");
				if(tmps2.length != 2) continue;
				services.add(new String[]{tmps2[0].trim(), tmps2[1].trim()});
			}
			return services;
		}
	}
	
	public Data upload(Data request) throws Exception {
		List<FileItem> fileList = request.get("$upload.files");
		if(fileList == null || fileList.isEmpty())
			 return new Data(Codes.CODE, Codes.SUCCESS, Codes.MSG, "upload.files is empty!");
		//
		final List<Data> files = Utils.newArrayList();
		for(FileItem fi: fileList) {
			final String fieldName = fi.getFieldName();
			final String contentType = fi.getContentType();
			final String name = fi.getName().indexOf(File.separator) >0 ?fi.getName().substring(fi.getName().lastIndexOf(File.separator) + 1): fi.getName();
			final long size = fi.getSize();

			Date now = new Date();
			final String fileDir = DateUtils.formatDate(now, "yyyyMM") + "/" + DateUtils.formatDate(now, "dd");
			File dir = new File(this.serviceConfig.getConfig("path") + File.separator + fileDir);
			if(!dir.exists()) dir.mkdirs();
			
			final String fileName = DateUtils.formatDate(now, "HHmmssms") + "." + name;
			final String relationPath =  fileDir + "/" + fileName;
			final String diskFilePath = this.serviceConfig.getConfig("path") + File.separator + fileDir + File.separator + fileName;
			
			FileOutputStream fos = new FileOutputStream(diskFilePath);
			IOUtils.copy(fi.getInputStream(), fos);
			fos.close();
			//
			final Data item = new Data("x-fieldName", fieldName, "x-fileName", name, 
					"x-relationPath", relationPath, "x-realPath", diskFilePath, 
					"x-contentType", contentType, "x-size", size, "tag", request.getString("tag")).merge(request, false);
			logger.debug("UploadService info:" + item);
			files.add(item);
			//
			for(String[] sd: this.getServices()) {
				EventPublisher.publish(sd[0], sd[1], item);
			}
			
		}
		return new Data(Codes.CODE, Codes.SUCCESS, "x-files", files);
	}
}
