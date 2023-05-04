package minuhy.xiaoxiang.blog.util;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
/**
 * 密码加密工具，cookie加密工具
 * 创建时间:2023-2-14 14:43:59
 */
public class EncryptionUtil {
	/**
	 * MD5 加密
	 * 
	 * @param bytes 原文
	 * @return 摘要文
	 */
	public static String getMD5(byte[] bytes) {
		byte[] digest = null;
		try {
			MessageDigest md5 = MessageDigest.getInstance("md5");
			digest = md5.digest(bytes);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		// 16是表示转换为16进制数
		String md5Str = new BigInteger(1, digest).toString(16);
		return md5Str;
	}

	/**
	 * MD5 加密
	 * 
	 * @param bytes 原文
	 * @return 摘要文
	 */
	public static String getMD5(String str) {
		byte[] bytes = null;
		bytes = str.getBytes(StandardCharsets.UTF_8);
		return getMD10(bytes);
	}

	/**
	 * MD5 加密（32进制）
	 * 
	 * @param bytes 原文
	 * @return 摘要文
	 */
	public static String getMD10(byte[] bytes) {
		byte[] digest = null;
		try {
			MessageDigest md5 = MessageDigest.getInstance("md5");
			digest = md5.digest(bytes);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		// 32是表示转换为32进制数
		String md5Str = new BigInteger(1, digest).toString(32);
		return md5Str;
	}

	/**
	 * MD5 加密（32进制）
	 * 
	 * @param bytes 原文
	 * @return 摘要文
	 */
	public static String getMD10(String str) {
		byte[] bytes = null;
		try {
			bytes = str.getBytes("utf-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return getMD10(bytes);
	}

	/**
	 * 异或操作
	 * 
	 * @param rawData 要异或的字节数组
	 * @param number  运算数
	 * @return 异或运算后的字节数组
	 */
	public static byte[] Xor(byte[] rawData, int number) {
		byte[] encodeData = new byte[rawData.length];
		for (int i = 0; i < rawData.length; i++) // 遍历字符数组
		{
			encodeData[i] = (byte) (rawData[i] ^ number); // 对每个数组元素进行异或运算
		}
		return encodeData;
	}

	/**
	 * 异或加密
	 * 
	 * @param rawStr 原文
	 * @param key    密钥
	 * @return 密文
	 */
	public static String EncodeByXor(String rawStr, String key) {
		// 通过字符串拿到密钥
		int number = 1;
		for (int i = 0; i < key.length(); i++) {
			number *= key.charAt(i);
		}

		// 转成字节数组
		byte[] rawData = rawStr.getBytes(StandardCharsets.UTF_8);
		byte[] encodeData = Xor(rawData, number);

		// 把字节数组转成某种格式的字符串，方便传输（格式可以自定义，好解析就行）
		StringBuilder encodeStr = new StringBuilder();
		for (byte b : encodeData) {
			encodeStr.append(b).append("x");
		}

		return encodeStr.toString();
	}

	/**
	 * 异或解密
	 * 
	 * @param encodeStr 密文
	 * @param key       密钥
	 * @return 原文
	 */
	public static String DecodeByXor(String encodeStr, String key) {
		// 通过字符串拿到密钥
		int number = 1;
		for (int i = 0; i < key.length(); i++) {
			number *= key.charAt(i);
		}

		// 解析EncodeByXor方法中的字节数组格式，找到加密后的字节数组
		String[] strings = encodeStr.substring(0, encodeStr.length() - 1).split("x");
		byte[] rawData = new byte[strings.length];
		for (int i = 0; i < strings.length; i++) {
			rawData[i] = Byte.parseByte(strings[i]);
		}

		// 异或一下
		byte[] encodeData = Xor(rawData, number);

		// 重新编码成原始字符串
		return new String(encodeData, StandardCharsets.UTF_8);
	}

	/**
	 * 随意写的密码加密算法
	 * 
	 * @param str 字符串，可以是账号
	 * @param pwd 密码
	 * @return 加密后的字符串（32位）
	 */
	public static String EncodePasswd(String str, String pwd) {
		String pwdMd5 = getMD5(pwd);
		String strMd5 = getMD10(str);
		byte[] bytes = new byte[92];
		for (int i = 0; i < bytes.length; i++) {
			if (i % 3 == 1) {
				bytes[i] = (byte) (pwdMd5.charAt(i % pwdMd5.length()) % 99);
			} else {
				bytes[i] = (byte) ((strMd5.charAt(i % strMd5.length()) % 99) * (i % 2 == 0 ? 1 : -1));
			}
		}

		String union = getMD10(bytes) + getMD5(bytes);

		return getMD10(union);
	}
}
