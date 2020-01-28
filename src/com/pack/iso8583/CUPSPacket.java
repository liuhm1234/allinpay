package com.pack.iso8583;

import jx.com.utils.Convert;

import com.pack.iso8583.exception.PacketException;
import com.pack.iso8583.exception.UnpacketException;
import com.pack.iso8583.handler.HeaderHandler;
import com.pack.iso8583.handler.X99MacCalculator;

public class CUPSPacket implements IPack {
	private IISO8583Packet requestPacket;// 发送8583包
	private IISO8583Packet respPacket;// 接收8583包
	private byte[] rspData = null;
	private IMacCalculator macCalculator;
	private IHeaderHandler headerHandler;

	private static CUPSPacket instance;

	public static CUPSPacket getInstance() {
		if (instance == null) {
			instance = new CUPSPacket();
		}

		return instance;
	}

	private CUPSPacket() {
		macCalculator = new X99MacCalculator();
		headerHandler = new HeaderHandler();
	}

	public byte[] pack(IISO8583Packet packet) throws PacketException {
		requestPacket = packet;
		byte[] requsetData = packet.toBytes();
		// 计算MAC
		if (packet.isCheckMAC()) {
			byte[] mac = macCalculator.calcMAC(requsetData);
			if (mac == null) {
				throw new PacketException("计算MAC错误");
			}
			requsetData = appendMac(requsetData, mac);
		}

		// // 报文头
		// requsetData = headerHandler.addHeader(requsetData);
		// // 长度
		// byte[] dataLength = new byte[2];
		// dataLength[0] = (byte) (requsetData.length / 256);
		// dataLength[1] = (byte) (requsetData.length % 256);
		System.out.println("8583PackData:");
		System.out.println(Convert.bcdBytesToStr(requsetData));
		return requsetData;
	}

	public byte[] pack1(IISO8583Packet packet) throws PacketException {
		requestPacket = packet;
		byte[] requsetData = packet.toBytes();
		// 计算MAC
		if (packet.isCheckMAC()) {
			byte[] mac = macCalculator.calcMAC(requsetData);
			if (mac == null) {
				throw new PacketException("计算MAC错误");
			}
			requsetData = appendMac(requsetData, mac);
		}

		// 报文头
		requsetData = headerHandler.addHeader(requsetData);
		// 长度
		byte[] dataLength = new byte[2];
		dataLength[0] = (byte) (requsetData.length / 256);
		dataLength[1] = (byte) (requsetData.length % 256);

		return mergeBytes(dataLength, requsetData);
	}

	public IISO8583Packet unpack(byte[] response) throws UnpacketException {
		// rspData = headerHandler.removeHeader(response);
		// if (rspData == null) {
		// throw new UnpacketException("解包失败，请重新签到");
		// }
		rspData = response;
		System.out.println("8583UnpackData:");
		System.out.println(Convert.bcdBytesToStr(rspData));
		return CUPSPacketFactory.getInstance().fromBytes(rspData);
	}

	public IMacCalculator getMacCalculator() {
		return macCalculator;
	}

	public IHeaderHandler getHeaderHandler() {
		return headerHandler;
	}

	private byte[] appendMac(byte[] packet, byte[] mac) {
		byte[] dest = new byte[packet.length + mac.length];
		System.arraycopy(packet, 0, dest, 0, packet.length);
		System.arraycopy(mac, 0, dest, packet.length, mac.length);
		return dest;
	}

	private byte[] mergeBytes(byte[] bytesA, byte[] bytesB) {
		if (bytesA != null && bytesA.length != 0) {
			if (bytesB != null && bytesB.length != 0) {
				byte[] bytes = new byte[bytesA.length + bytesB.length];
				System.arraycopy(bytesA, 0, bytes, 0, bytesA.length);
				System.arraycopy(bytesB, 0, bytes, bytesA.length, bytesB.length);
				return bytes;
			} else {
				return bytesA;
			}
		} else {
			return bytesB;
		}
	}

}
