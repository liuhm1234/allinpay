package com.trans;

import java.security.MessageDigest;
import java.util.Arrays;

import jx.com.utils.BytesUtil;
import jx.com.utils.Convert;
import jx.com.utils.DesUtils;

import com.pack.iso8583.CUPSPacketFactory;
import com.pack.iso8583.IISO8583Packet;
import com.sun.javafx.binding.StringFormatter;

public class InitTmkTrans extends BaseTrans {
	// public static String INIT_PWD = "77837229"; // 初始化密钥,通联提供

	String initPwd = null;
	byte[] md5Pwd; // 初始化密钥的MD5签名
	byte[] xTid; //
	byte[] ttek;
	String trd = "27150202"; // 终端随机数，这里随便用一个数

	public InitTmkTrans(String initPwd) {
		this.initPwd = initPwd;
	}

	@Override
	public boolean onNormalRespone(IISO8583Packet responePacket) {
		byte[] f62 = Convert.strToBcdBytes(responePacket.getField(62), false);
		byte[] iTek = getITek(xTid, md5Pwd, trd.getBytes(), ttek,
				BytesUtil.subByte(f62, 16, 8));
		byte[] tmk = getPlainKey(BytesUtil.subByte(f62, 24, 16), iTek,
				BytesUtil.subByte(f62, 40, 4));
		if (tmk == null) {
			System.out.println("获取initTmk失败!");
			return false;
		}
		System.out.println("initTmk:" + Convert.bcdBytesToStr(tmk));
		saveKey(Param.INIT_KEY, Convert.bcdBytesToStr(tmk));

		System.out.println("InitTmkTrans deal success");
		return true;
	}

	@Override
	public IISO8583Packet getSendPacket() {
		// 获取初始化密钥PWD的明文经过MD5算法生成的摘要
		if (initPwd == null) {
			System.out.println("请设置初始化密码");
			return null;
		}
		md5Pwd = getMD5Result(initPwd.getBytes());
		// 获取xTid
		xTid = getXData(Param.get(Param.TID).getBytes());
		// 获取ttek
		ttek = getTTek(xTid, md5Pwd);
		// 用tek加密终端随机密钥
		byte[] desTrd = DesUtils.des3Encrypt(trd.getBytes(), ttek);
		byte[] desPwd = DesUtils.des3Encrypt(initPwd.getBytes(), ttek);
		// 这里desKek就是联机初始化上送的62域数据
		byte[] desKek = BytesUtil.mergeBytes(desTrd, desPwd);

		IISO8583Packet packet = CUPSPacketFactory.getInstance().createPacket();
		packet.setCheckMAC(false);
		packet.setMsgId("0800");
		packet.setField(IISO8583Packet._FLD11_, getTrance());
		packet.setField(IISO8583Packet._FLD41_, getTid());
		packet.setField(IISO8583Packet._FLD42_, getMid());
		if(SYB_FLAG) {
			packet.setField(IISO8583Packet._FLD60_, "54" + getBatch() + "003");
		}else {
			packet.setField(IISO8583Packet._FLD60_, "54" + getBatch() + "40360000000");
		}
		
		//packet.setField(IISO8583Packet._FLD60_, "54" + getBatch() + "40360000000");
		packet.setField(IISO8583Packet._FLD62_, Convert.bcdBytesToStr(desKek)+String.format("%0168d", 0));
		packet.setField(IISO8583Packet._FLD63_, "099");
		return packet;
	}

	public byte[] getXData(byte[] bData) {
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
	public byte[] exchangeStr(byte[] data) {
		// 终端号应该是8位的
		if (data == null && data.length != 8) {
			return data;
		}

		return BytesUtil.mergeBytes(BytesUtil.subByte(data, 4, 4),
				BytesUtil.subByte(data, 0, 4));
	}

	// 这里没有判断有效参数
	public byte[] xor(byte[] b1, byte[] b2) {
		int i;
		byte[] bXor = new byte[b1.length];
		for (i = 0; i < b1.length; i++) {
			bXor[i] = (byte) (b1[i] ^ b2[i]);
		}

		return bXor;
	}

	public byte[] getMD5Result(byte[] data) {
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
	public byte[] getTTek(byte[] xTid, byte[] md5Pwd) {
		// byte[] md5Pwd = getMD5Result(INIT_PWD.getBytes());
		// byte[] xTid = getXData(TID.getBytes());
		byte[] tTek = xor(
				BytesUtil.merge(xTid,
						Convert.strToBcdBytes("D6A7B8B6CEDED3C7", false)),
				md5Pwd);

		System.out.println("tTek:" + Convert.bcdBytesToStr(tTek));
		return tTek;
	}

	public byte[] getITek(byte[] iKekLeft, byte[] md5Pwd,
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

	public String getInitPwd() {
		return initPwd;
	}

	public void setInitPwd(String initPwd) {
		this.initPwd = initPwd;
	}
}
