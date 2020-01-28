package com.trans;

import jx.com.utils.BytesUtil;
import jx.com.utils.Convert;

import com.pack.iso8583.CUPSPacketFactory;
import com.pack.iso8583.IISO8583Packet;

public class UpdateTmkTrans extends BaseTrans {

	@Override
	public boolean onNormalRespone(IISO8583Packet responePacket) {
		byte[] f62 = Convert.strToBcdBytes(responePacket.getField(62), false);
		byte[] encrptTmk = BytesUtil.subByte(f62, 24, 16);
		byte[] checkVal = BytesUtil.subByte(f62, 40, 4);

		byte[] initTmk = getKey(Param.INIT_KEY);
		byte[] tmk = getPlainKey(encrptTmk, initTmk, checkVal);
		if (tmk == null) {
			System.out.println("updateTmkTrans get tmk error!");
			return false;
		}

		saveKey(Param.TMK, Convert.bcdBytesToStr(tmk));
		System.out.println("updateTmkTrans tmk:" + Convert.bcdBytesToStr(tmk));
		System.out.println("UpdateTmkTrans deal succesful");
		return true;
	}

	@Override
	public IISO8583Packet getSendPacket() {
		IISO8583Packet sendPacked = CUPSPacketFactory.getInstance().createPacket();
		sendPacked.setCheckMAC(false);

		sendPacked.setMsgId("0800");
		sendPacked.setField(IISO8583Packet._FLD11_, getTrance());
		sendPacked.setField(IISO8583Packet._FLD41_, getTid());
		sendPacked.setField(IISO8583Packet._FLD42_, getMid());

		if (SYB_FLAG) {
			sendPacked.setField(IISO8583Packet._FLD60_, "52" + getBatch() + "003");
		} else {
			sendPacked.setField(IISO8583Packet._FLD60_, "52" + getBatch() + "40460000000");
		}
		sendPacked.setField(IISO8583Packet._FLD63_, "099");
		return sendPacked;
	}

}
