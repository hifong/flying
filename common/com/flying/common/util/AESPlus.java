package com.flying.common.util;  

import java.util.Date;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;
	/**
	 *
	 * @author Administrator
	 *
	 */
	public class AESPlus {
	 
	    // 加密
	    public static String Encrypt(String sSrc, String sKey) throws Exception {
	        if (sKey == null) {
	            System.out.print("Key为空null");
	            return null;
	        }
	        // 判断Key是否为16位
	        if (sKey.length() != 16) {
	            System.out.print("Key长度不是16位");
	            return null;
	        }
	        byte[] raw = sKey.getBytes("utf-8");
	        SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
	        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");//"算法/模式/补码方式"
	        cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
	        byte[] encrypted = cipher.doFinal(sSrc.getBytes("utf-8"));
	        return (new BASE64Encoder()).encode( encrypted); 
             //此处使用BASE64做转码功能，同时能起到2次加密的作用。
	    }
	 
	    // 解密
	    public static String Decrypt(String sSrc, String sKey) throws Exception {
	        try {
	            // 判断Key是否正确
	            if (sKey == null) {
	                System.out.print("Key为空null");
	                return null;
	            }
	            // 判断Key是否为16位
	            if (sKey.length() != 16) {
	                System.out.print("Key长度不是16位");
	                return null;
	            }
	            byte[] raw = sKey.getBytes("utf-8");
	            SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
	            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
	            cipher.init(Cipher.DECRYPT_MODE, skeySpec);
	            byte[] encrypted1 = (new BASE64Decoder()).decodeBuffer(sSrc);//先用base64解密
	            try {
	                byte[] original = cipher.doFinal(encrypted1);
	                String originalString = new String(original,"utf-8");
	                return originalString;
            } catch (Exception e) {
                System.out.println(e.toString());
	                return null;
	            }
	        } catch (Exception ex) {
	            System.out.println(ex.toString());
	            return null;
	        }
	    }
	 
	    public static void main(String[] args) throws Exception {
	        /*
	         * 此处使用AES-128-ECB加密模式，key需要为16位。
	         */
	        String cKey = "1234567890123456";
	        // 需要加密的字串
             String cSrc = "www.gowhere.so";
	        System.out.println(cSrc);
	        // 加密
	        String enString = AESPlus.Encrypt("12345678", "dev@#20121111111");
	        System.out.println("加密后的字串是：" + enString);
	 
	        // 解密
	        String DeString = AESPlus.Decrypt("ss", "dev@#$2012_10086");
	        System.out.println("解密后的字串是：" + DeString);
	        Date date = new Date();
	        long time = date.getTime();

	      //mysq 时间戳只有10位 要做处理
	        String dateline = time + "";
	        dateline = dateline.substring(0, 10);
	        System.out.println(dateline);
	        
	       String  keyValue=AESPlus.Decrypt("", "dev@#$2012_10086");
		
           String strarray[]=keyValue.split("_");
           String uid=null;
           String ut=null;
           for (int i = 0; i < strarray.length; i++) {
   			if(i==1)
   			{
   				uid=strarray[i];
   				System.out.println("uid:"+uid);
   			}
   			if(i==2)
   			{
   				ut=strarray[i];
   				System.out.println("ut:"+ut);
   			}
   		}
	    }
	}
  
	