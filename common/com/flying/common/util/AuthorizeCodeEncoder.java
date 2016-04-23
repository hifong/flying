package com.flying.common.util;

import java.util.UUID;

public class AuthorizeCodeEncoder {
	
	public static String encode() throws Exception{
		UUID uuid = UUID.randomUUID();
		MD5 md5 = new MD5(uuid.toString());
		String md5Str= md5.encode().toUpperCase();
		String rs = null;
		if(md5Str.length()<32){
			rs = md5Str;
			while(rs.length()<32){
				rs += "A";
			}
		}else{
			rs = md5Str.substring(0,32);
		}
		return rs;
	}
}
