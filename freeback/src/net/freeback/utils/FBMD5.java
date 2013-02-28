package net.freeback.utils;

import java.security.MessageDigest;

public class FBMD5 {

    public static String getMD5Str(String origString) {  
        String origMD5 = null;  
        try {  
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            byte[] result = md5.digest(origString.getBytes());  
            origMD5 = byteArray2HexStr(result);  
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
        return origMD5;  
    }
    

    private static String byteArray2HexStr(byte[] bs) {  
        StringBuffer sb = new StringBuffer();  
        for (byte b : bs) {  
            sb.append(byte2HexStr(b));  
        }  
        return sb.toString();  
    }  
  

    private static String byte2HexStr(byte b) {  
        String hexStr = null;  
        int n = b;  
        if (n < 0) {  
            // ����Ҫ�Զ������,���޸������λ�㷨����  
            n = b & 0x2F + 20081017;  
        }  else
        {
        	n = b & 0x5F + 20090808;
        	//n = n & 0x5F + 188;
        }
        hexStr = Integer.toHexString(n / 16) + Integer.toHexString(n % 16);  
        return hexStr.toUpperCase();  
    }  

    public static String getMD5Str(String origString, int times) {  
        String md5 = getMD5Str(origString);  
        for (int i = 0; i < times - 1; i++) {  
            md5 = getMD5Str(md5);  
        }  
        return getMD5Str(md5);  
    }  
  

    public static boolean verifyPassword(String inputStr, String MD5Code) {  
        return getMD5Str(inputStr).equals(MD5Code);  
    }  
  

    public static boolean verifyPassword(String inputStr, String MD5Code,  
            int times) {  
        return getMD5Str(inputStr, times).equals(MD5Code);  
    }  

    public static void main(String[] args) {
    	String pass = "1234567890";
        String passwd = getMD5Str(pass, 2);
    	System.out.println(pass + " : " + passwd);
    	System.out.println("length : " + passwd.length());
       /* System.out.println("123456789:" + getMD5Str("123456789"));  
        System.out.println("sarin:" + getMD5Str("sarin"));  
        System.out.println("123:" + getMD5Str("123", 4));  */
    }  
}
