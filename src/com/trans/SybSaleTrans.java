package com.trans;

import com.pack.iso8583.CUPSPacketFactory;
import com.pack.iso8583.IISO8583Packet;

import jx.com.utils.BytesUtil;
import jx.com.utils.Convert;
import jx.com.utils.DateUtils;
import jx.com.utils.TransUtils;

public class SybSaleTrans extends BaseTrans {
	private int amount;
	private String track2;
	private String track3;
	private String plainPin;
	private String sn;

	@Override
	public boolean onNormalRespone(IISO8583Packet responePacket) {
		System.out.println("SybSaleTrans deal succesful");
		return true;
	}

	@Override
	public IISO8583Packet getSendPacket() {
		IISO8583Packet sendPacked = CUPSPacketFactory.getInstance().createPacket();
		sendPacked.setCheckMAC(true);

		sendPacked.setMsgId("0200");
		sendPacked.setField(IISO8583Packet._FLD3_, "009000");
		sendPacked.setField(IISO8583Packet._FLD4_, String.format("%012d", getAmount()));
		sendPacked.setField(IISO8583Packet._FLD11_, getTrance());
		String datatime = DateUtils.getCurrentDateTime(DateUtils.DATE_FORMAT_YMDHMS);
		sendPacked.setField(IISO8583Packet._FLD12_, datatime.substring(8));
		sendPacked.setField(IISO8583Packet._FLD13_, datatime.substring(4, 8));
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
		sendPacked.setField(IISO8583Packet._FLD46_, packF46(3, ""));
		sendPacked.setField(IISO8583Packet._FLD49_, "156");

		String cardNo = getCardNo();
		String enPin = TransUtils.encryptPin(cardNo, getPlainPin(), Param.get(Param.TPK));
		sendPacked.setField(IISO8583Packet._FLD52_, enPin);

		sendPacked.setField(IISO8583Packet._FLD53_, "2600000000000000");

		String curSn = getSn();
		if (curSn == null || curSn.length() == 0) {
			System.out.println("sn must be not null");
			return null;
		}
		//sendPacked.setField(IISO8583Packet._FLD59_, getSn());

		// 消息类型+批次号+网络管理码+输入类型+行业卡编码+免密面签标识
		sendPacked.setField(IISO8583Packet._FLD60_, "22" + getBatch() + "000" + "20" + "0000" + "00");
		return sendPacked;
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

	String packF46(int scanMode, String orderNum) {
		StringBuilder builder = new StringBuilder();
		if (scanMode == 0) { // 微信
			builder.append("303502");
		} else if (scanMode == 1) { // 支付宝
			builder.append("313302");
		} else if (scanMode == 2) { // 通联钱包
			builder.append("323002");
		} else {
			builder.append("303302");
		}

		if (scanMode != 3) {
			builder.append("3002");
		} else {
			builder.append("2002");
		}
		builder.append(orderNum);
		builder.append("02");

		byte[] b46 = Convert.strToBcdBytes(builder.toString());
		byte[] result = new byte[3];
		result[0] = 0x5f;
		result[1] = 0x52;
		result[2] = (byte) b46.length;

		return Convert.bcdBytesToStr(BytesUtil.merge(result, b46));
	}

}
