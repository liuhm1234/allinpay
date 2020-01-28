package com.pack.iso8583;

import com.pack.iso8583.codec.FieldCodecManager;
import com.pack.iso8583.exception.UnpacketException;

public abstract class ISO8583PacketFactory {
	private FieldCodecManager fieldCodecManager;
	private String fileName;

	public abstract void init();

	public void loadConfig(ClassLoader loader, String fileName) {
		if (this.fileName == null || !this.fileName.equals(fileName)) {
			this.fileName = fileName;
			this.fieldCodecManager = ISO8583ConfigParser.load(loader, fileName);
		}
	}

	public IISO8583Packet createPacket() {
		return new ISO8583PacketImpl(fieldCodecManager);
	}

	public IISO8583Packet fromBytes(byte[] packet) throws UnpacketException {
		return new ISO8583PacketImpl(fieldCodecManager, packet);
	}
}
