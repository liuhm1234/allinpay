package jx.com.utils;

import com.trans.Param;

public class TransUtils {

	public static void main(String[] args) {
		String key = Param.get(Param.TDK);
		String data = "123456789012";
		// data += "123456789012345678901234567890";
		// data += "123456789012345678901234567890";
		// data += "123456789012345678901234567890";
		encryptData(data, key);
	}

	public static String getPan(String track) {
		if (track == null)
			return "";

		int len = track.indexOf('=');
		if (len < 0) {
			len = track.indexOf('D');
			if (len < 0) {
				return "";
			}
		}

		if ((len < 13) || (len > 19)) {
			return "";
		}
		return track.substring(0, len);
	}

	public static String getPanBlock(String pan) {
		String panBlock = null;
		if (pan == null || pan.length() < 13 || pan.length() > 19) {
			return null;
		}

		panBlock = "0000" + pan.substring(pan.length() - 13, pan.length() - 1);
		return panBlock;
	}

	public static String getExpDate(String track) {
		if (track == null)
			return null;

		int index = track.indexOf('=');
		if (index < 0) {
			index = track.indexOf('D');
			if (index < 0) {
				return null;
			}
		}

		if (index + 5 > track.length()) {
			return null;
		}
		return track.substring(index + 1, index + 5);
	}

	public static String encryptPin(String pan, String plainPin, String tpk) {
		String strPanBlk = getPanBlock(pan);
		String strPinBlk = String.format("%02d%sFFFFFFFFFFFFFFFF",
				plainPin.length(), plainPin).substring(0, 16);

		byte[] panBlk = Convert.strToBcdBytes(strPanBlk, false);
		byte[] pinBlk = Convert.strToBcdBytes(strPinBlk, false);

		byte[] dataIn = BytesUtil.xor(panBlk, pinBlk);

		byte[] pin = DesUtils.des3Encrypt(dataIn, Convert.strToBcdBytes(tpk));
		return Convert.bcdBytesToStr(pin);
	}

	public static String encryptData(String data, String key) {

		byte[] lenBcd = getencryptDataBcdLen(data);
		byte[] dataIn = BytesUtil.merge(lenBcd, Convert.strToBcdBytes(data));
		byte[] enData = encryptData(dataIn, Convert.strToBcdBytes(key));

		System.out.println("enData:" + Convert.bcdBytesToStr(enData));
		return Convert.bcdBytesToStr(enData);
	}

	public static byte[] getencryptDataBcdLen(String data) {
		String dataLen = String.format("%d", data.length());
		if (dataLen.length() % 2 != 0) {
			dataLen = "0" + dataLen;
		}

		System.out.println(dataLen);
		return Convert.strToBcdBytes(dataLen);
	}

	public static byte[] encryptData(byte[] data, byte[] key) {
		int dataLen = data.length;
		int x = dataLen % 8;
		int addLen = 0;
		if (x != 0) {
			addLen = 8 - dataLen % 8;
		}

		// System.out.println("realData len:" + (dataLen + addLen));
		// System.out.println("dataLen len:" + dataLen);
		// System.out.println("addLen len:" + addLen);
		byte[] realData = new byte[dataLen + addLen];
		System.arraycopy(data, 0, realData, 0, dataLen);

		// System.out.println("realData:" + Convert.bcdBytesToStr(realData));

		byte[] enData = DesUtils.des3Encrypt(realData, key);
		return enData;
	}

	public static byte[] decryptData(byte[] data, byte[] key) {
		if (data.length % 8 != 0) {
			return null;
		}
		byte[] enData = DesUtils.des3Decrypt(data, key);
		return enData;
	}

	public static byte[] clacMac(byte[] input, byte[] key) {
		int length = input.length;
		int x = length % 8;
		int addLen = 0;
		if (x != 0) {
			addLen = 8 - length % 8;
		}
		int pos = 0;
		byte[] data = new byte[length + addLen];
		System.arraycopy(input, 0, data, 0, length);
		byte[] oper1 = new byte[8];
		System.arraycopy(data, pos, oper1, 0, 8);
		pos += 8;
		for (int i = 1; i < data.length / 8; i++) {
			byte[] oper2 = new byte[8];
			System.arraycopy(data, pos, oper2, 0, 8);
			byte[] t = BytesUtil.xor(oper1, oper2);
			oper1 = t;
			pos += 8;
		}
		// 将异或运算后的最后8个字节（RESULT BLOCK）转换成16个HEXDECIMAL：
		byte[] resultBlock = Convert.bcdBytesToStr(oper1).getBytes();
		// 取前8个字节用mkey1，DES加密
		byte[] front8 = new byte[8];
		System.arraycopy(resultBlock, 0, front8, 0, 8);
		byte[] behind8 = new byte[8];
		System.arraycopy(resultBlock, 8, behind8, 0, 8);

		byte[] desfront8 = DesUtils.encrypt(front8, key);

		// 将加密后的结果与后8 个字节异或：
		byte[] resultXOR = BytesUtil.xor(desfront8, behind8);

		// 用异或的结果TEMP BLOCK 再进行一次单倍长密钥算法运算
		byte[] buff = DesUtils.encrypt(resultXOR, key);

		// 将运算后的结果（ENC BLOCK2）转换成16 个HEXDECIMAL asc
		// 取8个长度字节
		byte[] retBuf = BytesUtil.subByte(Convert.bcdBytesToStr(buff)
				.getBytes(), 0, 8);
		return retBuf;
	}
}
