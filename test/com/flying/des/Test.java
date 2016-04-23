package com.flying.des;

import com.flying.common.des.Coder;


public class Test {   
    
    private String password = "123456";   
    private String alias = "test";   
    private String certificatePath = "C:/PublicKey.cer";   
    private String keyStorePath = "C:/test.keystore";   
  
    /**  
     * 公钥加密—私钥解密  
     *   
     * @throws Exception  
     */  
    public void test1() throws Exception {   
        System.out.println("公钥加密—私钥解密");   
        String inputStr = "小猪快跑";   
        byte[] data = inputStr.getBytes();   
        // 公钥加密   
        byte[] encrypt = Coder.encryptByPublicKey(data,certificatePath);   
        // 私钥解密   
        byte[] decrypt = Coder.decryptByPrivateKey(encrypt,keyStorePath, alias, password);   
        String outputStr = new String(decrypt);   
        System.out.println("加密前:" + inputStr);   
        System.out.println("解密后:" + outputStr);   
    }   
  
    /**  
     * 私钥加密—公钥解密  
     *   
     * @throws Exception  
     */  
    public void test2() throws Exception {   
        System.out.println("私钥加密—公钥解密");   
        String inputStr = "小猪快跑";   
        byte[] data = inputStr.getBytes();   
        // 私钥加密   
        byte[] encodedData = Coder.encryptByPrivateKey(data,   
                keyStorePath, alias, password);   
        // 公钥加密   
        byte[] decodedData = Coder.decryptByPublicKey(encodedData,   
                certificatePath);   
        String outputStr = new String(decodedData);   
        System.out.println("加密前:" + inputStr);   
        System.out.println("解密后:" + outputStr);   
    }   
  
    /**  
     * 签名验证  
     *   
     * @throws Exception  
     */  
    public void testSign() throws Exception {   
        String inputStr = "签名";   
        byte[] data = inputStr.getBytes();   
        System.out.println("私钥签名—公钥验证");   
        // 产生签名   
        byte[] sign = Coder.sign(data, keyStorePath, alias, password);   
        // 验证签名   
        boolean status = Coder.verify(data, sign, certificatePath);   
        System.out.println("状态:" + status);   
    }
       
    public static void main(String args[]) throws Exception {   
    	Test t = new Test();   
        t.test1();   
        t.test2();   
        t.testSign();
    }
}  
