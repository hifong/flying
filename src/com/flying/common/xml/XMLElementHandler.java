package com.flying.common.xml;

import java.util.Map;

import org.jdom.Element;

public interface XMLElementHandler {
	void handle(Map<String, Object> data, Element ele);
}
