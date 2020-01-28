package com.trans;

import jx.com.utils.Convert;
import jx.com.utils.TransUtils;

import com.pack.iso8583.CUPSPacketFactory;
import com.pack.iso8583.IISO8583Packet;

public class SaleTrans extends BaseTrans {
	private int amount;
	private String track2;
	private String track3;
	private String plainPin;
	private String sn;

	@Override
	public boolean onNormalRespone(IISO8583Packet responePacket) {
		String encCardNo = responePacket.getField(IISO8583Packet._FLD2_);
		byte[] rcvCardNo = TransUtils.decryptData(
				Convert.strToBcdBytes(encCardNo), getKey(Param.TDK));
		if (!getCardNo().equals(Convert.bcdBytesToStr(rcvCardNo))) {
			System.out.println("cardno different:" + getCardNo() + "-"
					+ Convert.bcdBytesToStr(rcvCardNo));
			return false;
		}

		System.out.println("SaleTrans deal succesful");
		return true;
	}

	@Override
	public IISO8583Packet getSendPacket() {
		IISO8583Packet sendPacked = CUPSPacketFactory.getInstance()
				.createPacket();
		sendPacked.setCheckMAC(true);

		sendPacked.setMsgId("0200");
		sendPacked.setField(IISO8583Packet._FLD3_, "000000");
		sendPacked.setField(IISO8583Packet._FLD4_,
				String.format("%012d", getAmount()));
		sendPacked.setField(IISO8583Packet._FLD11_, getTrance());
		sendPacked.setField(IISO8583Packet._FLD22_, "0210");
		sendPacked.setField(IISO8583Packet._FLD25_, "00");
		sendPacked.setField(IISO8583Packet._FLD26_, "06");

		String enTrack2 = TransUtils.encryptData(getTrack2(),
				Param.get(Param.TDK));
		sendPacked.setField(IISO8583Packet._FLD35_, enTrack2);
		String enTrack3 = TransUtils.encryptData(getTrack3(),
				Param.get(Param.TDK));
		sendPacked.setField(IISO8583Packet._FLD36_, enTrack3);

		sendPacked.setField(IISO8583Packet._FLD41_, getTid());
		sendPacked.setField(IISO8583Packet._FLD42_, getMid());
		sendPacked.setField(IISO8583Packet._FLD49_, "156");

		String cardNo = getCardNo();
		String enPin = TransUtils.encryptPin(cardNo, getPlainPin(),
				Param.get(Param.TPK));
		sendPacked.setField(IISO8583Packet._FLD52_, enPin);

		sendPacked.setField(IISO8583Packet._FLD53_, "2600000000000000");

		String curSn = getSn();
		if (curSn == null || curSn.length() == 0) {
			System.out.println("sn must be not null");
			return null;
		}
		sendPacked.setField(IISO8583Packet._FLD59_, getSn());

		sendPacked.setField(IISO8583Packet._FLD60_, "22" + getBatch()
				+ "00060000000");
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

	public String getTrack2() {
		return track2;
	}

	public void setTrack2(String track2) {
		this.track2 = track2.replace("=", "D");

	}

	public String getTrack3() {
		return track3;
	}

	public void setTrack3(String track3) {
		this.track3 = track3.replace("=", "D");
	}

	public String getPlainPin() {
		return plainPin;
	}

	public void setPlainPin(String plainPin) {
		this.plainPin = plainPin;
	}

	public String getSn() {
		return sn;
	}

	public void setSn(String sn) {
		this.sn = sn;
	}

	public String getCardNo() {
		return TransUtils.getPan(track2);
	}
}
