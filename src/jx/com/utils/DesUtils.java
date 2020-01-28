package jx.com.utils;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

import jx.com.utils.BytesUtil;
import jx.com.utils.Convert;

/**
 * DES �ӽ���
 */
public class DesUtils {

	private final static String DES = "DES";
	private final static String CIPHER_ALGORITHM = "DES/ECB/NoPadding";

	/**
	 * ����
	 * 
	 * @param src
	 *            ���Դ
	 * @param key
	 *            ��Կ�����ȱ�����8�ı���
	 * @return ���ؼ��ܺ�����
	 */
	public static byte[] encrypt(byte[] src, byte[] key) {
		SecureRandom sr = new SecureRandom();
		try {
			DESKeySpec dks = new DESKeySpec(key);
			SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(DES);
			SecretKey securekey = keyFactory.generateSecret(dks);
			Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
			cipher.init(Cipher.ENCRYPT_MODE, securekey, sr);

			// Cipher enc = Cipher.getInstance("DES/CBC/PKCS5Padding");
			// SecretKeySpec keySpec = new SecretKeySpec(key, "DES");//key
			// IvParameterSpec ivSpec = new
			// IvParameterSpec("0000000000000000".getBytes());//iv
			// enc.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);

			return cipher.doFinal(src);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * �����Կ
	 * 
	 * @return
	 * @throws NoSuchAlgorithmException
	 */
	public static byte[] initKey() throws NoSuchAlgorithmException {
		KeyGenerator kg = KeyGenerator.getInstance(DES);
		kg.init(16);
		SecretKey secretKey = kg.generateKey();
		return secretKey.getEncoded();
	}

	/**
	 * ����
	 * 
	 * @param src
	 *            ���Դ
	 * @param key
	 *            ��Կ�����ȱ�����8�ı���
	 * @return ���ؽ��ܺ��ԭʼ���
	 */
	public static byte[] decrypt(byte[] src, byte[] key) {
		SecureRandom sr = new SecureRandom();
		try {
			DESKeySpec dks = new DESKeySpec(key);
			SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(DES);
			SecretKey securekey = keyFactory.generateSecret(dks);
			Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
			cipher.init(Cipher.DECRYPT_MODE, securekey, sr);
			return cipher.doFinal(src);
		} catch (Exception e) {
		}
		return null;
	}

	public static byte[] des3Encrypt(byte[] src, byte[] key) {
		if (key.length != 16) {
			return null;
		}
		if (src.length <= 0 || (src.length % 8 != 0)) {
			System.out.println("data must be n*8");
			return null;
		}

		byte[] ekey = BytesUtil.subByte(key, 0, 8);
		byte[] dkey = BytesUtil.subByte(key, 8, 8);
		
		byte[] result = null;
		for(int i =0; i<src.length/8; i++){
			byte[] tmpSrc = BytesUtil.subByte(src, i*8, 8);
			byte[] tmpResult = DesUtils.encrypt(tmpSrc, ekey);
			tmpResult = DesUtils.decrypt(tmpResult, dkey);
			tmpResult = DesUtils.encrypt(tmpResult, ekey);
			result = BytesUtil.mergeBytes(result, tmpResult);
		}
		//System.out.println("3decrypt:" + Convert.bcdBytesToStr(result));
		return result;
	}
	
	public static byte[] des3Decrypt(byte[] src, byte[] key) {
		if (key.length != 16) {
			return null;
		}
		
		if (src.length <= 0 || (src.length % 8 != 0)) {
			System.out.println("data must be n*8");
			return null;
		}

		byte[] ekey = BytesUtil.subByte(key, 0, 8);
		byte[] dkey = BytesUtil.subByte(key, 8, 8);

		byte[] result = null;
		for(int i =0; i<src.length/8; i++){
			byte[] tmpSrc = BytesUtil.subByte(src, i*8, 8);
			byte[] tmpResult = DesUtils.decrypt(tmpSrc, ekey);
			tmpResult = DesUtils.encrypt(tmpResult, dkey);
			tmpResult = DesUtils.decrypt(tmpResult, ekey);
			result = BytesUtil.mergeBytes(result, tmpResult);
		}
		
		//System.out.println("3decrypt:" + Convert.bcdBytesToStr(result));
		return result;
	}
}