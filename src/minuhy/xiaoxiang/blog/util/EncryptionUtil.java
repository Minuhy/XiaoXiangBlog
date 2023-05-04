package minuhy.xiaoxiang.blog.util;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
/**
 * ������ܹ��ߣ�cookie���ܹ���
 * ����ʱ��:2023-2-14 14:43:59
 */
public class EncryptionUtil {
	/**
	 * MD5 ����
	 * 
	 * @param bytes ԭ��
	 * @return ժҪ��
	 */
	public static String getMD5(byte[] bytes) {
		byte[] digest = null;
		try {
			MessageDigest md5 = MessageDigest.getInstance("md5");
			digest = md5.digest(bytes);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		// 16�Ǳ�ʾת��Ϊ16������
		String md5Str = new BigInteger(1, digest).toString(16);
		return md5Str;
	}

	/**
	 * MD5 ����
	 * 
	 * @param bytes ԭ��
	 * @return ժҪ��
	 */
	public static String getMD5(String str) {
		byte[] bytes = null;
		bytes = str.getBytes(StandardCharsets.UTF_8);
		return getMD10(bytes);
	}

	/**
	 * MD5 ���ܣ�32���ƣ�
	 * 
	 * @param bytes ԭ��
	 * @return ժҪ��
	 */
	public static String getMD10(byte[] bytes) {
		byte[] digest = null;
		try {
			MessageDigest md5 = MessageDigest.getInstance("md5");
			digest = md5.digest(bytes);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		// 32�Ǳ�ʾת��Ϊ32������
		String md5Str = new BigInteger(1, digest).toString(32);
		return md5Str;
	}

	/**
	 * MD5 ���ܣ�32���ƣ�
	 * 
	 * @param bytes ԭ��
	 * @return ժҪ��
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
	 * ������
	 * 
	 * @param rawData Ҫ�����ֽ�����
	 * @param number  ������
	 * @return ����������ֽ�����
	 */
	public static byte[] Xor(byte[] rawData, int number) {
		byte[] encodeData = new byte[rawData.length];
		for (int i = 0; i < rawData.length; i++) // �����ַ�����
		{
			encodeData[i] = (byte) (rawData[i] ^ number); // ��ÿ������Ԫ�ؽ����������
		}
		return encodeData;
	}

	/**
	 * ������
	 * 
	 * @param rawStr ԭ��
	 * @param key    ��Կ
	 * @return ����
	 */
	public static String EncodeByXor(String rawStr, String key) {
		// ͨ���ַ����õ���Կ
		int number = 1;
		for (int i = 0; i < key.length(); i++) {
			number *= key.charAt(i);
		}

		// ת���ֽ�����
		byte[] rawData = rawStr.getBytes(StandardCharsets.UTF_8);
		byte[] encodeData = Xor(rawData, number);

		// ���ֽ�����ת��ĳ�ָ�ʽ���ַ��������㴫�䣨��ʽ�����Զ��壬�ý������У�
		StringBuilder encodeStr = new StringBuilder();
		for (byte b : encodeData) {
			encodeStr.append(b).append("x");
		}

		return encodeStr.toString();
	}

	/**
	 * ������
	 * 
	 * @param encodeStr ����
	 * @param key       ��Կ
	 * @return ԭ��
	 */
	public static String DecodeByXor(String encodeStr, String key) {
		// ͨ���ַ����õ���Կ
		int number = 1;
		for (int i = 0; i < key.length(); i++) {
			number *= key.charAt(i);
		}

		// ����EncodeByXor�����е��ֽ������ʽ���ҵ����ܺ���ֽ�����
		String[] strings = encodeStr.substring(0, encodeStr.length() - 1).split("x");
		byte[] rawData = new byte[strings.length];
		for (int i = 0; i < strings.length; i++) {
			rawData[i] = Byte.parseByte(strings[i]);
		}

		// ���һ��
		byte[] encodeData = Xor(rawData, number);

		// ���±����ԭʼ�ַ���
		return new String(encodeData, StandardCharsets.UTF_8);
	}

	/**
	 * ����д����������㷨
	 * 
	 * @param str �ַ������������˺�
	 * @param pwd ����
	 * @return ���ܺ���ַ�����32λ��
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
