package com.pack.iso8583.handler;

import java.util.Arrays;

import jx.com.utils.BytesUtil;
import atest.Test;

import com.pack.iso8583.IHeaderHandler;
import com.trans.Param;

public class HeaderHandler implements IHeaderHandler {

	public byte[] addHeader(byte[] packet) {
		byte[] header = BytesUtil.hexString2ByteArray("6000080000"
				+ "900200000000");
		
		header = BytesUtil.merge(header, Param.get(Param.MID).getBytes(), Param.get(Param.TID).getBytes());
		byte[] dest = new byte[header.length + packet.length];
		int offset = 0;
		System.arraycopy(header, 0, dest, 0, header.length);
		offset += header.length;
		System.arraycopy(packet, 0, dest, offset, packet.length);
		return dest;
	}

	public byte[] removeHeader(byte[] packet) {
		int offset = 11 + 23;
		if (offset > packet.length) {
			return null;
		}
		return Arrays.copyOfRange(packet, offset, packet.length);
	}

	public byte[] getHeader(byte[] packet) {
		int offset = 11;
		if (offset > packet.length) {
			return null;
		}
		return Arrays.copyOfRange(packet, 0, offset);
	}

}
