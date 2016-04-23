package com.flying.common.util;

import java.io.Serializable;

public class PageDomain implements Serializable{
	private int index;
	private String name;
	
	public PageDomain(int index, int countPage) {
		this.index = index;
//		if(this.index == 1)
//			this.name = "首页";
//		else if(this.index == countPage)
//			this.name = "末页";
//		else
			this.name = String.valueOf(index);
	}
	
	public int getIndex() {
		return index;
	}
	public void setIndex(int index) {
		this.index = index;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
}
