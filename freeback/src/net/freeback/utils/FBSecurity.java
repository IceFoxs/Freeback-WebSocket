package net.freeback.utils;

import java.security.Key;

import javax.crypto.*;

public class FBSecurity {

	private static Key key = null;

	static
	{
		try {
			String MobKey = "6de3d56f91bd2e1c053f5917119bbe984c025d4da6448ae0";
			key = getKey(MobKey.getBytes());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static String byteArr2HexStr(byte[] arrB) throws Exception {
		int iLen = arrB.length;
		StringBuffer sb = new StringBuffer(iLen * 2);
		for (int i = 0; i < iLen; i++) {
			int intTmp = arrB[i];
			while (intTmp < 0) {
				intTmp = intTmp + 256;
			}
			if (intTmp < 16) {
				sb.append("0");
				
			}
			sb.append(Integer.toString(intTmp, 16));
		}
		return sb.toString();
	}

	public static byte[] hexStr2ByteArr(String strIn) throws Exception {
		byte[] arrB = strIn.getBytes();
		int iLen = arrB.length;
		byte[] arrOut = new byte[iLen / 2];
		for (int i = 0; i < iLen; i = i + 2) {
			String strTmp = new String(arrB, i, 2);
			arrOut[i / 2] = (byte) Integer.parseInt(strTmp, 16);
		}
		return arrOut;
	}
	
	static public byte[] encrypt(byte[] input) throws Exception
	{
		Cipher cipher = Cipher.getInstance("DES");
		cipher.init(Cipher.ENCRYPT_MODE, key); 
		return cipher.doFinal(input);
	}
	
	static public String encrypt(String input)
	{
		try {
			return byteArr2HexStr(encrypt(input.getBytes()));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	static public byte[] decrypt(byte[] input) throws Exception {
		Cipher cipher = Cipher.getInstance("DES");
		cipher.init(Cipher.DECRYPT_MODE, key); 
		return cipher.doFinal(input);
	}

	static public String decrypt(String input) {
		try {
			return new String(decrypt(hexStr2ByteArr(input)));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	static private Key getKey(byte[] arrBTmp) throws Exception {
		byte[] arrB = new byte[8];
		for (int i = 0; i < arrBTmp.length && i < arrB.length; i++) {
			arrB[i] = arrBTmp[i];
		}
		Key key = new javax.crypto.spec.SecretKeySpec(arrB, "DES");
		return key;
	} 
}
