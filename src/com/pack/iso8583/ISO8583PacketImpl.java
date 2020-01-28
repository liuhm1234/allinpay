package com.pack.iso8583;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import jx.com.utils.Convert;

import com.pack.iso8583.codec.BytesCache;
import com.pack.iso8583.codec.FieldCodecManager;
import com.pack.iso8583.codec.IFieldCodec;
import com.pack.iso8583.exception.PacketException;
import com.pack.iso8583.exception.UnpacketException;

public class ISO8583PacketImpl implements IISO8583Packet {
	private String mMsgId;
	private boolean mCheckMac;
	private byte[] bitmap;
	private HashMap<Integer, String> mFields;
	private FieldCodecManager fieldCodecManager;
	private static boolean isDebug = true;

	public ISO8583PacketImpl(FieldCodecManager fieldCodecManager) {
		this.bitmap = new byte[16];
		this.mFields = new HashMap<Integer, String>();
		this.fieldCodecManager = fieldCodecManager;
	}

	public ISO8583PacketImpl(FieldCodecManager fieldCodec, byte[] packet)
			throws UnpacketException {
		this(fieldCodec);
		unpacket(packet);
	}

	public byte[] toBytes() throws PacketException {
		return packet();
	}

	public void setMsgId(String msgId) {
		mFields.put(1, msgId);
		mMsgId = msgId;
	}

	public String getMsgId() {
		return mMsgId;
	}

	public void setCheckMAC(boolean checkMAC) {
		mCheckMac = checkMAC;
	}

	public boolean isCheckMAC() {
		return mCheckMac;
	}

	private void markFieldBit(int fieldId) {
		byte byteIndex = (byte) (fieldId - 1 >> 3);
		byte bitIndex = (byte) (fieldId % 8);
		bitIndex = bitIndex == 0 ? 8 : bitIndex;
		this.bitmap[byteIndex] = (byte) (this.bitmap[byteIndex] | 1 << 8 - bitIndex);
	}

	public void setField(int bitNo, String value) {
		mFields.put(bitNo, value);
		markFieldBit(bitNo);
	}

	public String getField(int bitNo) {
		return mFields.get(bitNo);
	}

	private void unmarkFieldBit(int fieldId) {
		byte byteIndex = (byte) (fieldId - 1 >> 3);
		byte bitIndex = (byte) (fieldId % 8);
		bitIndex = bitIndex == 0 ? 8 : bitIndex;
		this.bitmap[byteIndex] = (byte) (this.bitmap[byteIndex] & ~(1 << 8 - bitIndex));
	}

	public void deleteField(int bitNo) {
		mFields.remove(bitNo);
		unmarkFieldBit(bitNo);
	}

	private int bitmapLength(byte b) {
		return (b & 128) == 0 ? 8 : 16;
	}

	private int unpacketMsgId(byte[] data) throws UnpacketException {
		IFieldCodec decoder = fieldCodecManager.get(1);
		if (decoder == null) {
			throw new UnpacketException("unpacket msgId error!");
		} else {
			String msgId = decoder.decode(data);
			System.out.println("MSGID = " + msgId);
			setMsgId(msgId);
			return decoder.offset();
		}
	}

	public void setDebug(boolean debug) {
		isDebug = debug;
	}

	private void packetMsgId(BytesCache cache, String mMsgId,
			FieldCodecManager fieldCodecManager) throws PacketException {
		if (cache != null && mMsgId != null) {
			IFieldCodec encoder = fieldCodecManager.get(1);
			if (encoder == null) {
				throw new PacketException("打包信息域属性未配置");
			}

			byte[] msgid = encoder.encode(mMsgId);
			cache.write(msgid);
		}
	}

	private List<Integer> getSortedFieldIds() {
		ArrayList<Integer> ids = new ArrayList<Integer>();
		Iterator<Integer> iter = mFields.keySet().iterator();
		while (iter.hasNext()) {
			ids.add(iter.next());
		}

		Collections.sort(ids);
		return ids;
	}

	private byte[] getBitmap() {
		List<Integer> ids = getSortedFieldIds();
		int topId = ids.get(ids.size() - 1);
		if (mCheckMac) {
			markFieldBit(topId <= 64 ? 64 : 128);
		}

		if (topId > 64) {
			this.bitmap[0] = (byte) (this.bitmap[0] | 128);
		}

		return Arrays.copyOfRange(bitmap, 0, bitmapLength(bitmap[0]));
	}

	private byte[] packet() throws PacketException {
		BytesCache cache = new BytesCache();
		// 打包消息码
		packetMsgId(cache, mMsgId, fieldCodecManager);
		// 打包bitmap
		byte[] bitmap = getBitmap();
		cache.write(bitmap);

		// 循环打包域
		List<Integer> ids = getSortedFieldIds();
		System.out.println(String.format(
				"----------------FLD [num:%d]------------------", ids.size()));
		Iterator<Integer> idsIterator = ids.iterator();
		while (idsIterator.hasNext()) {
			int id = (Integer) idsIterator.next();
			if (id == 1) {
				System.out.println("MSGID = " + this.mMsgId);
			} else {
				IFieldCodec encoder = fieldCodecManager.get(id);
				if (encoder == null) {
					System.out.println(String.format(
							"ISO8583Field[%d] encoder is NULL.", id));
					throw new PacketException("打包域[" + id + "]属性未配置");
				}
				String fieldValue = mFields.get(id);
				if (fieldValue == null) {
					System.out.println(String.format("FieldValue[%d] is null.",
							id));
					fieldValue = "";
				} else {
					System.out.println(String.format("FLD[%d] = %s", id,
							fieldValue));
				}
				byte[] bytes = encoder.encode(fieldValue);
				cache.write(bytes);
			}
		}

		System.out
				.println("--------------------------end---------------------------");
		return cache.getBytes();
	}

	private void unpacket(byte[] data) throws UnpacketException {
		boolean offset = false;
		if (data == null || data.length == 0) {
			throw new UnpacketException("解包数据不存在");
		}

		System.out.println("----------------unpack start------------------");
		// 解包信息码
		int curPos = unpacketMsgId(data);
		if (curPos >= data.length) {
			throw new UnpacketException("消息码长度过长");
		}

		// 解包位图
		int bitLength = bitmapLength(data[curPos]);
		if (curPos + bitLength >= data.length) {
			throw new UnpacketException("解包位图长度过长");
		}
		
		byte[] bitmap = Arrays.copyOfRange(data, curPos, curPos + bitLength);
		curPos += bitLength;
		
		System.out.println("BITMAP = "+Convert.bcdBytesToStr(bitmap));

		// 解包各域
		for (int i = 0; i < bitLength; ++i) {
			for (int j = 7; j >= 0; --j) {
				if ((bitmap[i] & 1 << j) != 0) {
					int id = (i + 1) * 8 - j;
					if (id != 1) {
						IFieldCodec decoder = fieldCodecManager.get(id);
						if (decoder == null) {
							System.out.println(String.format(
									"ISO8583Field[%d] decoder is NULL.", id));
							throw new UnpacketException("解包域[" + id + "]属性未配置");
						}
						if (curPos >= data.length) {
							System.out
									.println(String
											.format("decoder[%d] error: offset = %d, data.length = %d.",
													new Object[] {
															Integer.valueOf(id),
															Integer.valueOf(curPos),
															Integer.valueOf(data.length) }));
							throw new UnpacketException("解包域[" + id + "]偏移"
									+ curPos + "超出预期:" + data.length);
						}
						byte[] field = Arrays.copyOfRange(data, curPos,
								data.length);
						String value = decoder.decode(field);
						if (value == null || value.length() == 0) {
							System.out
									.println(String
											.format("decoder[%d] fail: offset = %d, data.length = %d.",
													new Object[] {
															Integer.valueOf(id),
															Integer.valueOf(curPos),
															Integer.valueOf(data.length) }));
							value = "";
						}
						setField(id, value);
						System.out.println("FLD[" + id + "] = " + value);
						curPos += decoder.offset();
					}
				}
			}
		}
	}

}
