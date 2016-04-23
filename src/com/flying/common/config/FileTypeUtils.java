package com.flying.common.config;

import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class FileTypeUtils {  
	
	public final static String MIME_FILENAME = "mime.xml";
	public final static String MIME_NAME = "mimename";
	public final static String MIME_TYPE = "mimetype";
	public final static String MIME_IMG = "mimeimg";
	/**
	 * 默认
	 */
	public final static String MIME_DEFAULT = "other";
	 
	public static Map<String,Map<String,String>>  map = new HashMap<String,Map<String,String>>();
	
	/**
	 * 获取所有 MIME 类型
	 * @param getNew  是否重新获取 true，  默认取缓存 false
	 * @return Mime键值对
	 */
	public static Map<String,Map<String,String>> getMimes(boolean getNew) {
		
		String key ="";
		String val ="";
		String img ="";
		
		if(map.size() <= 0){
			try {
				DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
				DocumentBuilder db = dbf.newDocumentBuilder();
				Document document = db.parse(ConfigUtils.getConfigHome()+ConfigUtils.fileSeparator+FileTypeUtils.MIME_FILENAME);
				NodeList root = document.getChildNodes();
				for (int i = 0; i < root.getLength(); i++) {
					Node mimes = root.item(i); 
					NodeList mimesList = mimes.getChildNodes();
					for (int j = 0; j < mimesList.getLength(); j++) {
						Node mime = mimesList.item(j);  
						NodeList mimeList = mime.getChildNodes();
						
						for (int k = 0; k < mimeList.getLength(); k++) {
							Node keyval = mimeList.item(k);  
							
							if(keyval.getNodeName().equals("extension"))
								key = keyval.getNodeValue();
							
							if(keyval.getNodeName().equals("mimetype")) 
								val = keyval.getNodeValue();
							
							if(keyval.getNodeName().equals("imgurl")) 
								img = keyval.getNodeValue();
						}
						if(key.length() > 0 && val.length() > 0){ 
							Map<String,String>  file = new HashMap<String,String>();
							
							file.put(FileTypeUtils.MIME_NAME, key);
							file.put(FileTypeUtils.MIME_TYPE, val);
							file.put(FileTypeUtils.MIME_IMG, img);
							map.put(key, file); 
							
							key = val =img = "";
						}
					}
				}
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
		}
		return map;
	}
	
	/**
	 * 获取所有 MIME 类型
	 * @return Mime键值对
	 */
	public static Map<String,Map<String,String>> getMimes() {
		return getMimes(false);
	}
	 
}