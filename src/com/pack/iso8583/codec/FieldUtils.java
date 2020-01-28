package com.pack.iso8583.codec;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;

/**
 * Created by zhaojx on 2019/3/1.
 */

public class FieldUtils {
	public static final String STRING_EMPTY = "";
	public static final byte[] ARRAY_EMPTY = new byte[0];

	private FieldUtils() {
	}

	public static byte hex2byte(char hex) {
		return hex <= 102 && hex >= 97 ? (byte) (hex - 97 + 10) : (hex <= 70
				&& hex >= 65 ? (byte) (hex - 65 + 10)
				: (hex <= 57 && hex >= 48 ? (byte) (hex - 48) : 0));
	}

	public static String bytes2HexString(byte[] data) {
		if (isNullEmpty(data)) {
			return "";
		} else {
			StringBuilder buffer = new StringBuilder();
			byte[] var5 = data;
			int var4 = data.length;

			for (int var3 = 0; var3 < var4; ++var3) {
				byte b = var5[var3];
				String hex = Integer.toHexString(b & 255);
				if (hex.length() == 1) {
					buffer.append('0');
				}

				buffer.append(hex);
			}

			return buffer.toString().toUpperCase();
		}
	}

	public static byte[] hexString2Bytes(String data) {
		if (isNullEmpty(data)) {
			return ARRAY_EMPTY;
		} else {
			byte[] result = new byte[(data.length() + 1) / 2];
			if ((data.length() & 1) == 1) {
				data = data + "0";
			}

			for (int i = 0; i < result.length; ++i) {
				result[i] = (byte) (hex2byte(data.charAt(i * 2 + 1)) | hex2byte(data
						.charAt(i * 2)) << 4);
			}

			return result;
		}
	}

	public static void dumpHex(String msg, byte[] bytes) {
		int length = bytes.length;
		msg = msg == null ? "" : msg;
		StringBuilder sb = new StringBuilder();
		sb.append(String.format("\n---------------------- " + msg
				+ "(len:%d) ----------------------\n",
				new Object[] { Integer.valueOf(length) }));

		for (int i = 0; i < bytes.length; ++i) {
			if (i % 16 == 0) {
				if (i != 0) {
					sb.append('\n');
				}

				sb.append(String.format("0x%08X    ",
						new Object[] { Integer.valueOf(i) }));
			}

			sb.append(String.format("%02X ",
					new Object[] { Byte.valueOf(bytes[i]) }));
		}

		sb.append("\n----------------------------------------------------------------------\n");
		System.out.println("FieldUtils" + sb.toString());
	}

	public static String makeN(String str, int max) {
		if (str != null && max > 0) {
			if (str.length() == max) {
				return str;
			} else if (str.length() > max) {
				return str.substring(0, max);
			} else {
				StringBuilder sb = new StringBuilder();

				for (int i = 0; i < max; ++i) {
					sb.append('0');
				}

				sb.replace(0, str.length(), str);
				return sb.toString();
			}
		} else {
			throw new IllegalArgumentException();
		}
	}

	public static String substr(String data, int max) {
		return isNullEmpty(data) ? "" : (data.length() <= max ? data : data
				.substring(0, max));
	}

	public static byte[] _N2Bcd(String str) {
		if (isNullEmpty(str)) {
			return ARRAY_EMPTY;
		} else {
			String n = str.length() % 2 == 0 ? str : "0" + str;
			byte[] bcd = new byte[n.length() / 2];

			for (int i = 0; i < n.length() / 2; ++i) {
				bcd[i] = (byte) (n.charAt(2 * i) - 48 << 4 | n
						.charAt(2 * i + 1) - 48);
			}

			return bcd;
		}
	}

	public static byte[] N2Bcd_(String str) {
		if (isNullEmpty(str)) {
			return ARRAY_EMPTY;
		} else {
			String n = str.length() % 2 == 0 ? str : str + "0";
			byte[] bcd = new byte[n.length() / 2];

			for (int i = 0; i < n.length() / 2; ++i) {
				bcd[i] = (byte) (n.charAt(2 * i) - 48 << 4 | n
						.charAt(2 * i + 1) - 48);
			}

			return bcd;
		}
	}

	public static String Bcd2N(byte[] bcd) {
		if (isNullEmpty(bcd)) {
			return "";
		} else {
			StringBuilder sb = new StringBuilder();
			byte[] var5 = bcd;
			int var4 = bcd.length;

			for (int var3 = 0; var3 < var4; ++var3) {
				byte bt = var5[var3];
				sb.append((char) ((byte) (bt >> 4 & 15) + 48));
				sb.append((char) ((byte) (bt & 15) + 48));
			}

			return sb.toString();
		}
	}

	public static String makeANS(String data, int max) {
		if (data == null) {
			data = "";
		}

		if (data.length() == max) {
			return data;
		} else if (data.length() > max) {
			return data.substring(0, max);
		} else {
			StringBuilder sb = new StringBuilder();

			for (int i = 0; i < max; ++i) {
				sb.append(' ');
			}

			sb.replace(0, data.length(), data);
			return sb.toString();
		}
	}

	public static byte[] toBytes(String data) {
		try {
			return data.getBytes("ISO-8859-1");
		} catch (UnsupportedEncodingException var2) {
			var2.printStackTrace();
			return ARRAY_EMPTY;
		}
	}

	public static String fromBytes(byte[] data) {
		try {
			return new String(data, "ISO-8859-1");
		} catch (UnsupportedEncodingException var2) {
			var2.printStackTrace();
			return "";
		}
	}

	public static int bcdLength(int length) {
		return (length >> 1) + (length % 2 == 0 ? 0 : 1);
	}

	public static String Bcd2Ascii(byte[] bcd) {
		if (isNullEmpty(bcd)) {
			return "";
		} else {
			StringBuilder sb = new StringBuilder(bcd.length << 1);
			byte[] var5 = bcd;
			int var4 = bcd.length;

			for (int var3 = 0; var3 < var4; ++var3) {
				byte ch = var5[var3];
				byte half = (byte) (ch >> 4 & 15);
				sb.append((char) (half + (half > 9 ? 55 : 48)));
				half = (byte) (ch & 15);
				sb.append((char) (half + (half > 9 ? 55 : 48)));
			}

			return sb.toString();
		}
	}

	public static byte[] Ascii2Bcd(String ascii) {
		if (ascii == null) {
			return ARRAY_EMPTY;
		} else {
			if ((ascii.length() & 1) == 1) {
				ascii = ascii + "0";
			}

			byte[] asc = ascii.getBytes();
			byte[] bcd = new byte[ascii.length() >> 1];

			for (int i = 0; i < bcd.length; ++i) {
				bcd[i] = (byte) (hex2byte((char) asc[2 * i]) << 4 | hex2byte((char) asc[2 * i + 1]));
			}

			return bcd;
		}
	}

	public static byte[] _Ascii2Bcd(String ascii) {
		if (ascii == null) {
			return ARRAY_EMPTY;
		} else {
			if ((ascii.length() & 1) == 1) {
				ascii = "0" + ascii;
			}

			byte[] asc = ascii.getBytes();
			byte[] bcd = new byte[ascii.length() >> 1];

			for (int i = 0; i < bcd.length; ++i) {
				bcd[i] = (byte) (hex2byte((char) asc[2 * i]) << 4 | hex2byte((char) asc[2 * i + 1]));
			}

			return bcd;
		}
	}

	public static boolean isNullEmpty(byte[] array) {
		return array == null || array.length == 0;
	}

	public static boolean isNullEmpty(String str) {
		return str == null || str.length() == 0;
	}

	public static int parseInt(String string) {
		int result = 0;

		try {
			result = Integer.parseInt(string);
		} catch (NumberFormatException var3) {
			var3.printStackTrace();
		}

		return result;
	}

	public static byte[] copyOf(byte[] original, int newLength) {
		try {
			return Arrays.copyOf(original, newLength);
		} catch (NegativeArraySizeException var3) {
			return null;
		} catch (NullPointerException var4) {
			return null;
		}
	}

	public static byte[] copyOfRange(byte[] original, int start, int end) {
		try {
			return Arrays.copyOfRange(original, start, end);
		} catch (ArrayIndexOutOfBoundsException var4) {
			return null;
		} catch (IllegalArgumentException var5) {
			return null;
		} catch (NullPointerException var6) {
			return null;
		}
	}
}
