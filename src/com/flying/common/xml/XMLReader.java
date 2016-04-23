package com.flying.common.xml;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

public class XMLReader {
	private Map<String, Object> data;
	
	public XMLReader(File file) throws JDOMException, IOException {
		SAXBuilder builder = new SAXBuilder();
		Document doc = builder.build(file);
		Element root = doc.getRootElement();
		
		
	}
}
