package com.flying.common.util;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

import com.flying.framework.exception.AppException;

/**
 * XML读取代码
 * @author lenovo
 *
 */
public class XMLCursor {
	private Document doc;
	private Element current;
	
	public XMLCursor(String filename) {
		this(new File(filename));
	}
	
	public XMLCursor(File file) {
		try {
			SAXBuilder builder = new SAXBuilder();
			this.doc = builder.build(file);
			this.current = this.doc.getRootElement();
		} catch (Exception e) {
			throw new AppException(e,"");
		}
	}
	
	public String getAttribute(String name) {
		if(current != null) {
			String tmp = current.getAttributeValue(name);
			return tmp == null?null:tmp.trim();
		} else {
			return null;
		}
	}
	
	public String getText() {
		String tmp = current == null? null: current.getText();
		return tmp == null?null:tmp.trim();
	}
	
	@SuppressWarnings("unchecked")
	public String getSubText(String name) {
		if(current == null) return null;
		Element child = current.getChild(name);
		if(child != null) {
			String tmp = child.getText();
			return tmp == null?null: tmp.trim();
		} else {
			List<Element> children = current.getChildren();
			for(Element e: children) {
				if(StringUtils.equalsIgnoreCase(name, e.getAttributeValue("name"))) {
					String tmp = e.getText();
					return tmp == null?null: tmp.trim();
				}
			}
			return null;
		}
	}
	
	@SuppressWarnings("unchecked")
	public String[] getSubTexts(String name) {
		if(current == null) return null;
		List<Element> children = StringUtils.isEmpty(name)?current.getChildren(): current.getChildren(name);
		String[] res = new String[children.size()];
		for(int i=0; i< children.size(); i++) {
			String tmp = children.get(i).getText();
			res[i] = tmp == null?null:tmp.trim();
		}
		return res;
	}
	
	/**
	 * 提取所有子节点类似<nodename name="">xxx</nodename>的数据；
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Map<String, String> getConfigs() {
		Map<String, String> res = Utils.newHashMap();
		if(current == null) return res;
		List<Element> children = this.current.getChildren();
		for(Element e: children) {
			String name = e.getAttributeValue("name");
			if(StringUtils.isEmpty(name)) name = e.getName();
			String tmp = e.getText();
			tmp = tmp == null?null:tmp.trim();
			res.put(e.getAttributeValue("name"), tmp);
		}
		return res;
	}
	
	/**
	 * 移动到子节点
	 * @param nodes 节点路径
	 */
	public boolean moveIn(String[] nodes) {
		Element cursor = this.current;
		for(String n: nodes) {
			if(".".equals(n)) {
				cursor = doc.getRootElement();
			} else if("..".equals(n)) {
				cursor = (Element)current.getParent();
			} else {
				Element child = cursor.getChild(n);
				cursor = child;
				if(child == null) {
					return false;
				}
			}
		}
		this.current = cursor;
		return true;
	}
	
	public boolean moveIn(String node) {
		return this.moveIn(new String[]{node});
	}
	
	@SuppressWarnings("unchecked")
	public int size() {
		if(current != null && current.getParent() != null && current.getParent() instanceof Element) {
			List<Element> children = ((Element)current.getParent()).getChildren();
			return children.size();
		} else {
			return -1;
		}
	}
	
	/**
	 * 在同级节点中移动；
	 * @param position 绝对位置
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public boolean moveTo(int position) {
		if(position >= 0 && position < size()& current != null && current.getParent() != null && current.getParent() instanceof Element) {
			List<Element> children = ((Element)current.getParent()).getChildren();
			this.current = children.get(position);
			return true;
		} else {
			return false;
		}
	}
	
	public Element getCurrent() {
		return current;
	}
	
	public String getValue(String name) {
		String val = this.getAttribute(name);
		if(StringUtils.isEmpty(val)) 
			val = this.getSubText(name);
		return val;
	}
}
