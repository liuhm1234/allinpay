package com.pack.iso8583.codec;

import java.util.HashMap;

public class FieldCodecManager {
	private HashMap<Integer, IFieldCodec> mapCodec = new HashMap<Integer, IFieldCodec>();

	public FieldCodecManager() {
	}

	public void add(int id, IFieldCodec codec) {
		mapCodec.put(id, codec);
	}

	public void remove(int id) {
		mapCodec.remove(id);
	}

	public void clear() {
		mapCodec.clear();
	}

	public IFieldCodec get(int id) {
		return mapCodec.get(id);
	}
}