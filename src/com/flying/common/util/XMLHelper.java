package com.flying.common.util;

import java.io.StringReader;
import java.io.StringWriter;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;
import org.jdom.input.SAXBuilder;
import org.jdom.xpath.XPath;
import org.lilystudio.smarty4j.Context;
import org.lilystudio.smarty4j.Engine;
import org.lilystudio.smarty4j.Template;
import org.xml.sax.InputSource;

import com.flying.framework.module.LocalModule;
import com.flying.framework.util.SmartyUtils;

public class XMLHelper {
	
	private static Logger logger = Logger.getLogger(XMLHelper.class);
	
	public static Document getDocument(String xml){
		if(StringUtils.isBlank(xml))
			return null;
		
		StringReader sr = new StringReader(xml);
		InputSource is = new InputSource(sr);
		try {
			 Document doc = (new SAXBuilder()).build(is);
	       return doc;
		} catch (Exception e) {
			logger.error("XMLHelper:getDocument",e);
		}
		return null;
	}
	
	public static String getNodeValue(Element parent,String nodeName){
		Element element = parent.getChild(nodeName);
		if(element==null){
			return "";
		}
		return element.getValue();
	}
	
	public static String getNodeValue(Element parent,String nodeName,Namespace namespace){
		Element element = parent.getChild(nodeName,namespace);
		if(element==null){
			return "";
		}
		return element.getValue();
	}
	
	public static String getNodeValue(String xml,String xPath) throws Exception{
		Document document = getDocument(xml);
		if(document==null){
			return "";
		}
		Element root = document.getRootElement();
		Element element = (Element)XPath.selectSingleNode(root, xPath);
		if(element==null){
			return "";
		}
		return element.getValue();
	}
	
	public static String getNodeValueByPath(Element root,String xPath) throws Exception{
		Element element = (Element)XPath.selectSingleNode(root, xPath);
		if(element==null){
			return "";
		}
		return element.getValue();
	}
	
	public static Element getSingleNode(String xml,String xPath) throws Exception{
		Document document = getDocument(xml);
		if(document==null){
			return null;
		}
		Element root = document.getRootElement();
		Element element = (Element)XPath.selectSingleNode(root, xPath);
		return element;
	}
	
	public static Element getSingleNode(Element root,String xPath) throws Exception{
		Element element = (Element)XPath.selectSingleNode(root, xPath);
		return element;
	}
	
	public static List<Element> getNodeList(String xml,String xPath) throws Exception{
		Document document = getDocument(xml);
		if(document==null){
			return null;
		}
		Element root = document.getRootElement();
		List<Element> elements = (List<Element>)XPath.selectNodes(root, xPath);
		return elements;
	}
	
	public static List<Element> getNodeList(Element root,String xPath) throws Exception{
		List<Element> elements = (List<Element>)XPath.selectNodes(root, xPath);
		return elements;
	}
	
	public static void copytobean(Element parent,Object bean) throws Exception{
		List<Element> list = parent.getChildren();
		Map<String,String> eMap = Utils.newHashMap();
		for (Element element : list) {
			String elementName = element.getName();
			String value = element.getValue();
			eMap.put(elementName.toLowerCase(), value);
		}
		
		
		Class clasz = bean.getClass();
		AccessibleObject.setAccessible(clasz.getDeclaredFields(), true);
		try {
			Field[] fields = clasz.getDeclaredFields();
			for (Field field : fields) {
				String fieldName = field.getName();
				String value = eMap.get(fieldName.toLowerCase());
				if(value!=null){
					BeanUtils.setProperty(bean, field.getType(),fieldName, value);
				}
			}
		} catch (Exception e) {
			logger.debug(e.toString());
		}
	}
	
	public static String generateXml(LocalModule module,String name,Map<String,Object> map){
		try{
			Engine engine = SmartyUtils.getEngine(module);
			Context context = new Context();
			context.putAll(map);
			Template template = engine.getTemplate("xml/" + name + ".tpl");
			StringWriter sw = new StringWriter();
			template.merge(context, sw);
			return sw.toString().trim();
		}catch(Exception e){
			logger.error("XMLHelper:generateXml",e);
			return "";
		}
	}
}
