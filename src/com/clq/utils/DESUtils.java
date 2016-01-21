package com.clq.utils;


import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;


public class DESUtils {

    //public static final SysConfig sysConfig = SpringContextHolder.getBean(SysConfig.class);
    
    private static byte[] iv = { 10, 12, 6, 4, 5, 1, 18, 9 };
    
    private static String decryptKey = "clq_keys";
    
    static Base64 base64 = null;
    
    static {
        if(StringUtils.isEmpty(decryptKey)){
            //decryptKey = sysConfig.getConfig("des_key");
        }
        
        if(null == base64) base64 = new Base64();
    }
    
    public static String encryptDES(String encryptString)
            throws Exception {
        IvParameterSpec zeroIv = new IvParameterSpec(iv);
        SecretKeySpec key = new SecretKeySpec(decryptKey.getBytes(), "DES");
        Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, key, zeroIv);
        byte[] encryptedData = cipher.doFinal(encryptString.getBytes());
        
        byte[] encode = base64.encodeBase64(encryptedData);
        return new String(encode);
    }
    
    public static String decryptDES(String decryptString) throws Exception {
        byte[] byteMi = base64.decodeBase64(decryptString);
        IvParameterSpec zeroIv = new IvParameterSpec(iv);
        SecretKeySpec key = new SecretKeySpec(decryptKey.getBytes(), "DES");
        Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, key, zeroIv);
        byte decryptedData[] = cipher.doFinal(byteMi);

        return new String(decryptedData);
    }
    
    
    
    public static void main(String[] args) {
        try {
        	String str = encryptDES("http://cn163.net/archives/tag/sfd/page/7/");
            System.out.println(str);
            System.out.println(decryptDES(str));
            
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
