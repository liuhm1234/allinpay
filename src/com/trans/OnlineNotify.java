package com.trans;

import com.pack.iso8583.CUPSPacketFactory;
import com.pack.iso8583.IISO8583Packet;

public class OnlineNotify extends BaseTrans {

	@Override
	public boolean onNormalRespone(IISO8583Packet responePacket) {
		System.out.println("OnlineNotify deal succesful");
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

		if (SYB_FLAG) {
			sendPacked.setField(IISO8583Packet._FLD60_, "53" + getBatch() + "003");
		} else {
		sendPacked.setField(IISO8583Packet._FLD60_, "53" + getBatch()
				+ "40560000000");
		}
		sendPacked.setField(IISO8583Packet._FLD63_, "099");
		return sendPacked;
	}

}
