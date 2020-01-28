package com.trans;

import jx.com.utils.Convert;
import jx.com.utils.TransUtils;

import com.pack.iso8583.CUPSPacketFactory;
import com.pack.iso8583.IISO8583Packet;

public class ScanTrans extends BaseTrans {
	private int amount;
	private String qrCode;

	@Override
	public boolean onNormalRespone(IISO8583Packet responePacket) {

		System.out.println("ScanTrans deal succesful");
		return true;
	}

	@Override
	public IISO8583Packet getSendPacket() {

		IISO8583Packet sendPacked = CUPSPacketFactory.getInstance()
				.createPacket();
		sendPacked.setCheckMAC(true);

		sendPacked.setMsgId("0200");
		//如果交易报错，可以试试将下面这段打开 (jx126)
		String cardNo = "0000000000000000";
		String enCardNo = TransUtils.encryptData(cardNo, Param.get(Param.TDK));
		sendPacked.setField(IISO8583Packet._FLD2_, enCardNo);
		sendPacked.setField(IISO8583Packet._FLD3_, "000000");
		sendPacked.setField(IISO8583Packet._FLD4_,
				String.format("%012d", getAmount()));
		sendPacked.setField(IISO8583Packet._FLD11_, getTrance());
		sendPacked.setField(IISO8583Packet._FLD22_, "0120");
		sendPacked.setField(IISO8583Packet._FLD25_, "36");
		sendPacked.setField(IISO8583Packet._FLD41_, getTid());
		sendPacked.setField(IISO8583Packet._FLD42_, getMid());
		sendPacked.setField(IISO8583Packet._FLD49_, "156");
		sendPacked.setField(IISO8583Packet._FLD60_, "22" + getBatch()
				+ "00060000000");
		sendPacked.setField(IISO8583Packet._FLD62_, Convert.bcdBytesToStr(("SQ" +getQrCode()).getBytes()));
		return sendPacked;
	}

	@Override
	protected String getHeader() {
		return "910200000000";
	}

	@Override
	public boolean isNeedEncrypt() {
		return true;
	}

	public int getAmount() {
		return amount;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}

	public String getQrCode() {
		return qrCode;
	}

	public void setQrCode(String qrCode) {
		this.qrCode = qrCode;
	}
}
