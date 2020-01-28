package com.trans;

import java.util.Arrays;

import jx.com.utils.BytesUtil;
import jx.com.utils.Convert;
import jx.com.utils.DesUtils;
import jx.com.utils.TransUtils;

import com.pack.iso8583.CUPSPacket;
import com.pack.iso8583.CUPSPacketFactory;
import com.pack.iso8583.IISO8583Packet;
import com.pack.iso8583.exception.PacketException;
import com.pack.iso8583.exception.UnpacketException;
import com.pack.net.TcpIpUtils;
import com.sun.javafx.binding.StringFormatter;

public abstract class BaseTrans {
	public static String TPDU = "6000080000";
	// public static String HOST_IP = "116.228.223.216";
	// public static int HOST_PORT = 10021;
	public static String HOST_IP = "116.228.223.216";
	public static int HOST_PORT = 10021;
	public static int TIMEOUT = 10;
	public String TAG = this.getClass().getSimpleName();
	public static boolean SYB_FLAG = true;

	static CUPSPacket packMaker = CUPSPacket.getInstance();
	static {
		CUPSPacketFactory.getInstance().init();
	}

	public boolean doTrans() {
		System.out.println("*************** " + TAG + " start ****************");
		try {
			IISO8583Packet requestPacket = getSendPacket();
			if (requestPacket == null) {
				System.out.println("get requestPacket not be null");
				return false;
			}
			IISO8583Packet responePacket = sendAndRcv(requestPacket);
			String rspCode = responePacket.getField(39);
			System.out.println(this.getClass().getSimpleName() + " rspCode:" + rspCode);
			if (rspCode.equals("00")) {
				return onNormalRespone(responePacket);
			} else {
				System.out.println(this.getClass().getSimpleName() + " deal fail");
				return onErrorRespone(responePacket);
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(this.getClass().getSimpleName() + " doTrans ecxeption:" + e.getMessage());
			return false;
		} finally {
			System.out.println("*************** " + TAG + " end ****************");
			System.out.println();
		}
	}

	protected boolean onErrorRespone(IISO8583Packet responePacket) {
		return false;
	}

	public abstract boolean onNormalRespone(IISO8583Packet responePacket);

	public abstract IISO8583Packet getSendPacket();

	public IISO8583Packet sendAndRcv(IISO8583Packet sendPacked) throws UnpacketException, PacketException {

		// 报文体
		byte[] sendData8583 = packMaker.pack(sendPacked);
		System.out.println("befor encrypt:");
		System.out.println(Convert.bytesToHexStr(sendData8583));
		byte[] key = Convert.strToBcdBytes(Param.get(Param.TDK));
		if (isNeedEncrypt() && !SYB_FLAG) {
			sendData8583 = TransUtils.encryptData(sendData8583, key);
			System.out.println("encrypt 8583 data");
			System.out.println("enSendData8583:");
			System.out.println(Convert.bytesToHexStr(sendData8583));
		}

		// 报文头
		byte[] header = getSendHeader(sendData8583.length);
//		if (header.length != 34) {
//			throw new RuntimeException("报文头必须为34字节");
//		}

		// 请求体 = 报文头 + 报文体
		byte[] requsetData = BytesUtil.merge(header, sendData8583);

		// 报文长度
		byte[] dataLength = new byte[2];
		dataLength[0] = (byte) (requsetData.length / 256);
		dataLength[1] = (byte) (requsetData.length % 256);

		// 发送数据 = 报文长度 + 请求体
		byte[] sendDatas = BytesUtil.merge(dataLength, requsetData);
		System.out.println("sendDatas:");
		System.out.println(Convert.bcdBytesToStr(sendDatas));

		// 接收数据
		byte[] rcvDatas = TcpIpUtils.sendAndReceive(HOST_IP, HOST_PORT, sendDatas, TIMEOUT);
		// byte[] rcvDatas=null;
		if (rcvDatas == null || rcvDatas.length == 0) {
			throw new RuntimeException("数据接收为空");
		}
		System.out.println("rcvData:");
		System.out.println(Convert.bytesToHexStr(rcvDatas));

		// 8583报文体,这里不考虑报文头的内容，实际是要处理的
		byte[] rcvData8583 = null;
		if (SYB_FLAG) {
			rcvData8583 = BytesUtil.subByte(rcvDatas, 80, rcvDatas.length - 80);
		} else {
			rcvData8583 = BytesUtil.subByte(rcvDatas, 34, rcvDatas.length - 34);
		}
		if (isNeedEncrypt() && !SYB_FLAG) {
			System.out.println("decrypt 8583 data");
			if (rcvData8583.length % 8 != 0) {
				throw new RuntimeException("接收数据加密格式错");
			}

			rcvData8583 = DesUtils.des3Encrypt(rcvData8583, key);
			System.out.println("deRcvData8583:");
			System.out.println(Convert.bcdBytesToStr(rcvData8583));
		}
		return packMaker.unpack(rcvData8583);
	}

	public byte[] getSendHeader(int packLen) {
		if (SYB_FLAG) {
			return getSYBHeader(packLen);
		}

		String header = TPDU + getHeader();
		byte[] bHeader = BytesUtil.merge(Convert.strToBcdBytes(header), Param.get(Param.MID).getBytes(),
				Param.get(Param.TID).getBytes());

		System.out.println("header:" + Convert.bcdBytesToStr(bHeader));
		return bHeader;
	}

	private byte[] getSYBHeader(int packLen) {
		String header = "6006030000" + getHeader();

		StringBuilder builder = new StringBuilder();
		// TPDU

		// 识别串,15位商户号+8位终端号
		builder.append(Param.get(Param.MID));
		builder.append(Param.get(Param.TID));
		// 机构号
		builder.append("300000000000003");
		// 业务类型,默认为8个0;
		builder.append("00000000");
		// 被代理商户号
		builder.append(Param.get(Param.MID));
		String len = String.format("%08d", packLen);
		builder.append(len);
		return BytesUtil.mergeBytes(Convert.strToBcdBytes(header), builder.toString().getBytes());
	}

	// 这里每个交易不一样，具体交易重载
	protected String getHeader() {
		if (SYB_FLAG) {
			return "902000000000";
		}
		return "900200000000";
	}

	public String getMid() {
		return Param.get(Param.MID);
	}

	public String getTid() {
		return Param.get(Param.TID);
	}

	public String getTrance() {
		String strTrance = Param.get(Param.TRANCE);
		int trance;
		try {
			trance = Integer.parseInt(strTrance);
			if (trance > 999999 || trance <= 0) {
				trance = 1;
			}
		} catch (Exception e) {
			trance = 1;
		}

		Param.set(Param.TRANCE, (trance + 1) + "");
		return String.format("%06d", trance);
	}

	public String getBatch() {
		String strBatch = Param.get(Param.BATCH);
		int batch;
		try {
			batch = Integer.parseInt(strBatch);
			if (batch > 999999 || batch <= 0) {
				batch = 1;
			}
		} catch (Exception e) {
			batch = 1;
		}

		return String.format("%06d", batch);
	}

	public byte[] getKey(String keyName) {
		String strKey = Param.get(keyName);
		if (strKey == null || strKey.length() == 0) {
			return new byte[0];
		}

		return Convert.strToBcdBytes(strKey, false);
	}

	public void saveKey(String keyName, String keyVal) {
		Param.set(keyName, keyVal);
	}

	public byte[] getPlainKey(byte[] encrptKey, byte[] desKey, byte[] checkVal) {
		byte[] plainKey = DesUtils.des3Decrypt(encrptKey, desKey);
		byte[] data = new byte[] { 0, 0, 0, 0, 0, 0, 0, 0 };
		byte[] myCheckVal = DesUtils.des3Encrypt(data, plainKey);
		System.out.println(
				"checkVal:" + Convert.bcdBytesToStr(checkVal) + ",myCheckVal:" + Convert.bcdBytesToStr(myCheckVal));
		if (Arrays.equals(BytesUtil.subByte(myCheckVal, 0, 4), checkVal)) {
			System.out.println("获取明文key校验OK");
		} else {
			System.out.println("获取明文key校验Error");
			return null;
		}
		return plainKey;
	}

	public boolean isNeedEncrypt() {
		return false;
	}

}
