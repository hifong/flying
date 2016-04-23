package com.flying.framework.request.impl;

import java.io.File;
import java.lang.reflect.Array;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import com.flying.common.util.ServiceHelper;
import com.flying.common.util.StringUtils;
import com.flying.common.util.Utils;
import com.flying.framework.data.Data;
import com.flying.framework.data.DataUtils;
import com.flying.framework.module.LocalModule;
import com.flying.framework.request.AbstractRequestFilter;
import com.flying.framework.request.RequestFilterChain;

public class UploadFilter extends AbstractRequestFilter {

	@Override
	public void doFilter(LocalModule module, HttpServletRequest req, HttpServletResponse resp, RequestFilterChain chain) throws Exception {
		if ((req.getContentType() != null)
				&& (req.getContentType().toLowerCase().startsWith("multipart"))) {
			Data request = DataUtils.convert(module, req);
			DiskFileItemFactory dfif = new DiskFileItemFactory();
			dfif.setSizeThreshold(this.serviceConfig.getConfigs().getInt("size-threashold", 4096));
			dfif.setRepository(this.serviceConfig.getConfigs().contains("repository")? new File(this.serviceConfig.getConfig("repository")):null);
			ServletFileUpload sfu = new ServletFileUpload(dfif);
			sfu.setHeaderEncoding(this.serviceConfig.getConfigs().getString("header-encoding", "UTF-8"));
			// 设置上传文件大小的上限，-1表示无上限
			sfu.setSizeMax(this.serviceConfig.getConfigs().getLong("size-max", -1));
			
			// 上传文件，并解析出表单所有字段，包括普通字段和文件字段
			@SuppressWarnings("unchecked")
			List<FileItem> fileItemList = sfu.parseRequest(req);
			if (fileItemList != null){
				List<FileItem> fileList = Utils.newArrayList();
				for (FileItem fileItem:fileItemList) {
					if (fileItem == null)
						continue;
					// 如果是普通字段
					if (fileItem.isFormField()) {
						if (fileItem.getFieldName() != null && fileItem.getString() != null){
							String key = fileItem.getFieldName();
							String value = new String(fileItem.getString().getBytes("iso-8859-1"), "UTF-8");
							if(request.contains(key) && request.get(key) != null) {
								Object oldValue = request.get(key);
								String[] newValue = new String[(oldValue instanceof String) ? 2:Array.getLength(oldValue) + 1];
								if(oldValue instanceof String) {
									newValue[0]=(String)oldValue;
									newValue[1] = value;
								} else {
									System.arraycopy(oldValue, 0, newValue, 0, newValue.length - 1);
									newValue[newValue.length - 1] = value;
								}
								request.put(key, newValue);
							} else {
								request.put(fileItem.getFieldName(), value);
							}
						}
						continue;
					}
					// 否则 是文件字段
					String filename = fileItem.getName();
					long size = fileItem.getSize();
					// 如果文件为空直接跳过
					if (StringUtils.isEmpty(filename) || size == 0)
						continue;
	
					fileList.add(fileItem);
				}
				//
				Data data = new Data("$upload.files", fileList).merge(request, false);
				final String uploadService = this.serviceConfig.getConfig("uploadService");
				if(uploadService != null)
					request.merge(ServiceHelper.invoke(module.getId(), uploadService, data), false);
			}
		}
		chain.doFilter(req, resp);
	}

}
