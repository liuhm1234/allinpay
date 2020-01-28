package com.pack.iso8583;

public class CUPSPacketFactory extends ISO8583PacketFactory {
	private static CUPSPacketFactory instance;

	private CUPSPacketFactory() {
	}

	public static CUPSPacketFactory getInstance() {
		if (instance == null) {
			instance = new CUPSPacketFactory();
			
		}
		return instance;
	}

	@Override
	public void init() {
		loadConfig(getClass().getClassLoader(), "iso8583.xml");
	}

}
