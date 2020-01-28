package com.trans;

import jx.com.utils.BytesUtil;
import jx.com.utils.Convert;

import com.pack.iso8583.CUPSPacketFactory;
import com.pack.iso8583.IISO8583Packet;

public class LogonTrans extends BaseTrans {

	@Override
	public boolean onNormalRespone(IISO8583Packet responePacket) {
		byte[] tmk = getKey(Param.TMK);
		byte[] f62 = Convert.strToBcdBytes(responePacket.getField(62));

		byte[] encrptTpk = BytesUtil.subByte(f62, 44, 16);
		byte[] checkVal = BytesUtil.subByte(f62, 60, 4);
		byte[] tpk = getPlainKey(encrptTpk, tmk, checkVal);
		if (tpk == null) {
			System.out.println("LogonTrans tpk check error!");
			return false;
		}

		byte[] encrptTak = BytesUtil.subByte(f62, 64, 8);
		encrptTak = BytesUtil.merge(encrptTak, encrptTak);
		checkVal = BytesUtil.subByte(f62, 80, 4);
		byte[] tak = getPlainKey(encrptTak, tmk, checkVal);
		if (tak == null) {
			System.out.println("LogonTrans tak check error!");
			return false;
		}

		byte[] encrptTdk = BytesUtil.subByte(f62, 84, 16);
		checkVal = BytesUtil.subByte(f62, 100, 4);
		byte[] tdk = getPlainKey(encrptTdk, tmk, checkVal);
		if (tdk == null) {
			System.out.println("LogonTrans tdk check error!");
			return false;
		}

		saveKey(Param.TPK, Convert.bcdBytesToStr(tpk));
		saveKey(Param.TAK, Convert.bcdBytesToStr(tak));
		saveKey(Param.TDK, Convert.bcdBytesToStr(tdk));

		String batchNo = responePacket.getField(60).substring(2, 8);
		saveKey(Param.BATCH, batchNo);
		
		System.out.println("LogonTrans deal succesful");
		return true;
	}

	@Override
	public IISO8583Packet getSendPacket() {
		IISO8583Packet sendPacked = CUPSPacketFactory.getInstance()
				.createPacket();
		sendPacked.setCheckMAC(false);

		sendPacked.setMsgId("0800");
		sendPacked.setField(IISO8583Packet._FLD11_, getTrance());
		sendPacked.setField(IISO8583Packet._FLD41_, getTid());
		sendPacked.setField(IISO8583Packet._FLD42_, getMid());
		sendPacked.setField(IISO8583Packet._FLD60_, "50" + getBatch() + "402");
		sendPacked.setField(IISO8583Packet._FLD63_, "001");
		return sendPacked;
	}

}
