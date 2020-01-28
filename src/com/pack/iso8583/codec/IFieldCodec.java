package com.pack.iso8583.codec;

public interface IFieldCodec {
	byte[] encode(String data);

	String decode(byte[] data);

	byte[] getBytes();

	int offset();
}
