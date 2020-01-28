package com.trans;

import java.security.MessageDigest;
import java.util.Arrays;

import jx.com.utils.BytesUtil;
import jx.com.utils.Convert;
import jx.com.utils.DesUtils;

import com.pack.iso8583.CUPSPacket;
import com.pack.iso8583.CUPSPacketFactory;
import com.pack.iso8583.IISO8583Packet;
import com.pack.iso8583.exception.PacketException;
import com.pack.iso8583.exception.UnpacketException;
import com.pack.net.TcpIpUtils;

public class OnlineTrans {
	public static String MID = "821530173990268";
	public static String TID = "03180000";
	// public static String INIT_PWD = "12345678"; // 初始化密钥,通联提供
	public static String INIT_PWD = "77837229"; // 初始化密钥,通联提供
	public static String HOST_IP = "116.228.223.216";
	// public static String HOST_IP = "192.168.31.197";
	public static int HOST_PORT = 10021;
	public static int TIMEOUT = 10;

	public static int tranceNo = 500;
	public static int batchNo = 10;

	static CUPSPacket packMaker = CUPSPacket.getInstance();

	static {
		CUPSPacketFactory.getInstance().init();
	}

	public static void onlineDownTmk() {
		byte[] tempTmk = termOnlineInit();
		byte[] realTmk = onlineUpdate(tempTmk);
		onlineNotify();
	}

	public static void termLogon(byte[] tmk) {
		IISO8583Packet sendPacked = CUPSPacketFactory.getInstance()
				.createPacket();
		sendPacked.setCheckMAC(false);
		sendPacked.setMsgId("0800");
		sendPacked.setField(IISO8583Packet._FLD11_,
				String.format("%06d", tranceNo++));
		sendPacked.setField(IISO8583Packet._FLD41_, TID);
		sendPacked.setField(IISO8583Packet._FLD42_, MID);
		sendPacked.setField(IISO8583Packet._FLD60_,
				String.format("50%06d402", batchNo));
		sendPacked.setField(IISO8583Packet._FLD63_, "001");
		try {
			IISO8583Packet responePacket = sendAndRcv(sendPacked);
			String rspCode = responePacket.getField(39);
			System.out.println("rspCode:" + rspCode);
			System.out.println("field62:" + responePacket.getField(62));
			if (rspCode.equals("00")) {
				byte[] f62 = Convert.strToBcdBytes(responePacket.getField(62),
						false);
				int pos = 1;
				byte[] encrptTpk = BytesUtil.subByte(f62, pos, 16);
				pos += 16;
				byte[] checkVal = BytesUtil.subByte(f62, pos, 4);
				pos += 4;
				
				byte[] tpk  = getPlainKey(encrptTpk, tmk, checkVal);
				if(tpk == null){
					System.out.println("pinkey 校验错");
					return;
				}
				
				byte[] encrptTak = BytesUtil.subByte(f62, pos, 8);
				encrptTak = BytesUtil.merge(encrptTak, encrptTak);
				pos += 16;
				checkVal = BytesUtil.subByte(f62, pos, 4);
				pos += 4;
				
				byte[] tak  = getPlainKey(encrptTpk, tmk, checkVal);
				if(tak == null){
					System.out.println("mackey 校验错");
					return;
				}
				
				byte[] encrptTdk = BytesUtil.subByte(f62, pos, 16);
				pos += 16;
				checkVal = BytesUtil.subByte(f62, pos, 4);
				pos += 4;
				
				byte[] tdk  = getPlainKey(encrptTpk, tmk, checkVal);
				if(tdk == null){
					System.out.println("mtdkey 校验错");
					return;
				}
				
				
			} else {
				System.out.println("rspCode:");
			}

		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
		}
	}

	// initPwd通联提供的初始化密码,返回传输主密钥
	public static byte[] termOnlineInit() {
		String trd = "27150202"; // 终端随机数，这里随便用一个数
		// 获取初始化密钥PWD的明文经过MD5算法生成的摘要
		byte[] md5Pwd = getMD5Result(INIT_PWD.getBytes());
		// 获取xTid
		byte[] xTid = getXData(TID.getBytes());
		// 获取ttek
		byte[] ttek = getTTek(xTid, md5Pwd);
		// 用tek加密终端随机密钥
		byte[] desTrd = DesUtils.des3Encrypt(trd.getBytes(), ttek);
		byte[] desPwd = DesUtils.des3Encrypt(INIT_PWD.getBytes(), ttek);
		// 这里desKek就是联机初始化上送的62域数据
		byte[] desKek = BytesUtil.mergeBytes(desTrd, desPwd);

		IISO8583Packet packet = CUPSPacketFactory.getInstance().createPacket();
		packet.setCheckMAC(false);
		packet.setMsgId("0800");
		packet.setField(IISO8583Packet._FLD11_,
				String.format("%06d", tranceNo++));
		packet.setField(IISO8583Packet._FLD41_, TID);
		packet.setField(IISO8583Packet._FLD42_, MID);
		// packet.setField(IISO8583Packet._FLD60_, "5400001040360000000");
		packet.setField(IISO8583Packet._FLD60_,
				String.format("54%06d40360000000", batchNo));
		packet.setField(IISO8583Packet._FLD62_, Convert.bcdBytesToStr(desKek));
		packet.setField(IISO8583Packet._FLD63_, "099");

		try {
			IISO8583Packet responePacket = sendAndRcv(packet);
			String rspCode = responePacket.getField(39);
			System.out.println("rspCode:" + rspCode);
			System.out.println("field62:" + responePacket.getField(62));
			if (rspCode.equals("00")) {
				byte[] f62 = Convert.strToBcdBytes(responePacket.getField(62),
						false);
				byte[] iTek = getITek(xTid, md5Pwd, trd.getBytes(), ttek,
						BytesUtil.subByte(f62, 16, 8));
				byte[] tmk = getPlainKey(BytesUtil.subByte(f62, 24, 16), iTek,
						BytesUtil.subByte(f62, 40, 4));
				System.out.println("tmk:" + Convert.bcdBytesToStr(tmk));
				return tmk;
			}
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
			return null;
		}
	}

	public static byte[] onlineUpdate(byte[] kek) {
		IISO8583Packet sendPacked = CUPSPacketFactory.getInstance()
				.createPacket();
		sendPacked.setCheckMAC(false);

		sendPacked.setMsgId("0800");
		sendPacked.setField(IISO8583Packet._FLD11_,
				String.format("%06d", tranceNo++));
		sendPacked.setField(IISO8583Packet._FLD41_, TID);
		sendPacked.setField(IISO8583Packet._FLD42_, MID);
		sendPacked.setField(IISO8583Packet._FLD60_,
				String.format("52%06d40460000000", batchNo));
		sendPacked.setField(IISO8583Packet._FLD63_, "099");
		try {
			IISO8583Packet responePacket = sendAndRcv(sendPacked);
			String rspCode = responePacket.getField(39);
			System.out.println("rspCode:" + rspCode);
			System.out.println("field62:" + responePacket.getField(62));
			if (rspCode.equals("00")) {
				byte[] f62 = Convert.strToBcdBytes(responePacket.getField(62),
						false);
				byte[] encrptTmk = BytesUtil.subByte(f62, 24, 16);
				byte[] checkVal = BytesUtil.subByte(f62, 40, 4);

				byte[] realTmk = getPlainKey(encrptTmk, kek, checkVal);
				System.out.println("realTmk:" + Convert.bcdBytesToStr(realTmk));
				return realTmk;
			} else {
				System.out.println("rspCode:");
				return null;
			}

		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
			return null;
		}
	}

	public static void onlineNotify() {
		IISO8583Packet sendPacked = CUPSPacketFactory.getInstance()
				.createPacket();
		sendPacked.setCheckMAC(false);
		sendPacked.setMsgId("0800");
		sendPacked.setField(IISO8583Packet._FLD11_,
				String.format("%06d", tranceNo++));
		sendPacked.setField(IISO8583Packet._FLD41_, TID);
		sendPacked.setField(IISO8583Packet._FLD42_, MID);
		sendPacked.setField(IISO8583Packet._FLD60_,
				String.format("53%06d40460000000", batchNo));
		sendPacked.setField(IISO8583Packet._FLD63_, "099");
		try {
			IISO8583Packet responePacket = sendAndRcv(sendPacked);
			String rspCode = responePacket.getField(39);
			System.out.println("rspCode:" + rspCode);
			System.out.println("field62:" + responePacket.getField(62));
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
		}
	}

	public static byte[] getXData(byte[] bData) {
		String initVector = "CDA8C1AAD0C2D0CB";// 文档中提到的异或向量
		byte[] bInitVector = Convert.strToBcdBytes(initVector, false);

		byte[] tid1 = exchangeStr(bData);
		byte[] tid2 = xor(tid1, bInitVector);
		// System.out.println(Convert.bcdBytesToStr(tid2));

		byte[] xTid = new byte[8];
		for (int i = 0; i < xTid.length; i++) {
			xTid[i] = (byte) (((tid2[i] & 0xff) / 9) ^ ((tid2[i] & 0xff) % 9));
		}

		// System.out.println(Convert.bcdBytesToStr(xTid));
		return xTid;
	}

	// 1) 交换终端号TID的前后4位
	public static byte[] exchangeStr(byte[] data) {
		// 终端号应该是8位的
		if (data == null && data.length != 8) {
			return data;
		}

		return BytesUtil.mergeBytes(BytesUtil.subByte(data, 4, 4),
				BytesUtil.subByte(data, 0, 4));
	}

	// 这里没有判断有效参数
	public static byte[] xor(byte[] b1, byte[] b2) {
		int i;
		byte[] bXor = new byte[b1.length];
		for (i = 0; i < b1.length; i++) {
			bXor[i] = (byte) (b1[i] ^ b2[i]);
		}

		return bXor;
	}

	public static byte[] getMD5Result(byte[] data) {
		byte[] secretBytes = null;
		try {
			// 生成一个MD5加密计算摘要
			MessageDigest md = MessageDigest.getInstance("MD5");
			// 对字符串进行加密
			md.update(data);
			// 获得加密后的数据
			secretBytes = md.digest();
		} catch (Exception e) {
			e.printStackTrace();
		}

		System.out.println("md5pwd:" + Convert.bcdBytesToStr(secretBytes));
		return secretBytes;
	}

	// 获取终端加密TMK的tek
	public static byte[] getTTek(byte[] xTid, byte[] md5Pwd) {
		// byte[] md5Pwd = getMD5Result(INIT_PWD.getBytes());
		// byte[] xTid = getXData(TID.getBytes());
		byte[] tTek = xor(
				BytesUtil.merge(xTid,
						Convert.strToBcdBytes("D6A7B8B6CEDED3C7", false)),
				md5Pwd);

		System.out.println("tTek:" + Convert.bcdBytesToStr(tTek));
		return tTek;
	}

	public static byte[] sendAndRcv(byte[] sendDatas) {
		byte[] rcvDatas = TcpIpUtils.sendAndReceive(HOST_IP, HOST_PORT,
				sendDatas, TIMEOUT);
		if (rcvDatas == null || rcvDatas.length == 0) {
			throw new RuntimeException("数据接收为空");
		}

		return rcvDatas;
	}

	public static IISO8583Packet sendAndRcv(IISO8583Packet sendPacked)
			throws UnpacketException, PacketException {
		byte[] sendDatas = packMaker.pack(sendPacked);
		System.out.println("senddata:");
		System.out.println(Convert.bytesToHexStr(sendDatas));

		byte[] rcvDatas = TcpIpUtils.sendAndReceive(HOST_IP, HOST_PORT,
				sendDatas, TIMEOUT);
		if (rcvDatas == null || rcvDatas.length == 0) {
			throw new RuntimeException("数据接收为空");
		}

		System.out.println(Convert.bytesToHexStr(rcvDatas));
		return packMaker.unpack(rcvDatas);
	}

	public static byte[] getITek(byte[] iKekLeft, byte[] md5Pwd,
			byte[] termRandomData, byte[] tTek, byte[] encrptPRD) {
		// 解密出后台随机数明文PRD，PRD是被TTEK加密过的
		// plainPRD就是后台随机数明文
		byte[] plainPRD = DesUtils.des3Decrypt(encrptPRD, tTek);

		// 计算终端随机数变种
		byte[] xTRD = getXData(termRandomData);
		// 计算后台随机数变种
		byte[] xPRD = getXData(plainPRD);

		// 计算IKEY_RIGHT
		byte[] iKeyRight = xor(xTRD, xPRD);

		byte[] iTek = xor(BytesUtil.mergeBytes(iKekLeft, iKeyRight), md5Pwd);

		System.out.println("iKek:" + Convert.bcdBytesToStr(iTek));
		return iTek;
	}

	public static byte[] getPlainKey(byte[] encrptKey, byte[] desKey,
			byte[] checkVal) {
		byte[] plainKey = DesUtils.des3Decrypt(encrptKey, desKey);
		byte[] data = new byte[] { 0, 0, 0, 0, 0, 0, 0, 0 };
		byte[] myCheckVal = DesUtils.des3Encrypt(data, plainKey);
		System.out.println("checkVal:" + Convert.bcdBytesToStr(checkVal)
				+ ",myCheckVal:" + Convert.bcdBytesToStr(myCheckVal));
		if (Arrays.equals(BytesUtil.subByte(myCheckVal, 0, 4), checkVal)) {
			System.out.println("获取明文key校验OK");
		} else {
			System.out.println("获取明文key校验Error");
			return null;
		}
		return plainKey;
	}

}
