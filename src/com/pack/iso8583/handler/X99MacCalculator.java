package com.pack.iso8583.handler;

import jx.com.utils.Convert;
import jx.com.utils.TransUtils;

import com.pack.iso8583.IMacCalculator;
import com.trans.Param;

public class X99MacCalculator implements IMacCalculator {

	public byte[] calcMAC(byte[] data) {
		String macKey = Param.get(Param.TAK);
		return TransUtils.clacMac(data, Convert.strToBcdBytes(macKey));
	}

	public boolean checkMAC(byte[] data) {
		return true;
	}

}
