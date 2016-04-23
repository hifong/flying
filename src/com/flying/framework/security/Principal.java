package com.flying.framework.security;

import java.io.Serializable;

/**
 * @author wanghaifeng
 *
 */
public interface Principal extends Serializable{
	public static final String PRINCIPAL = Principal.class.getName();
	
	public String getId();
	
	public String getName();
	
	public boolean isRole(String[] roles);
	
	public boolean hasPermission(String[] permissions);
}
