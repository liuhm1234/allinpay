package jx.com.utils;

import java.io.UnsupportedEncodingException;
import java.util.Locale;

/**
 * 
 * @author JohnnyLiu
 *
 */
public class Convert {

    public static byte[] intToBytes(long num) {
        byte[] tmp = new byte[4];
        for (int i = 0; i < 4; i++) {
            tmp[i] = (byte) (num >>> (24 - i * 8));
        }
        return tmp;
    }

    public static int bytesToInt(byte[] bytes, boolean bytesIsHex, int radix) {
        int result = 0;
        try {
            if (bytesIsHex)
                result = Integer.valueOf(hexBytesToStr(bytes), radix);
            else
                result = Integer.valueOf(bcdBytesToStr(bytes), radix);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static String hexBytesToStr(byte[] bytes) {
        try {
            return new String(bytes, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return "";
        }
    }
    public static String bytesToHexStr(byte[] byteArr) {
        if (byteArr == null || byteArr.length == 0)
            return "";
        StringBuffer strBufTemp = new StringBuffer("");
        for (int i = 0; i < byteArr.length; i++) {
            String stmp = Integer.toHexString(byteArr[i] & 0XFF);
            if (stmp.length() == 1)
                strBufTemp.append("0" + stmp);
            else
                strBufTemp.append(stmp);
        }
        return strBufTemp.toString().toUpperCase(Locale.getDefault());
    }
    public static byte[] strToHexBytes(String hexString) {
        if (hexString == null || hexString.equals(""))
            return new byte[0];

        String hexData = hexString.toUpperCase(Locale.getDefault()).replaceAll(" ", "");
        int length = hexData.length() / 2;
        char[] hexChars = hexData.toCharArray();
        byte[] d = new byte[length];
        for (int i = 0; i < length; i++) {
            int pos = i * 2;
            d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
        }
        return d;
    }

    public static String bcdBytesToStr(byte[] bytes) {
        if (bytes == null)
            return "";

        char c[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (int i = 0; i < bytes.length; i++)
            sb.append(c[(bytes[i] & 0xf0) >>> 4]).append(c[bytes[i] & 0x0f]);

        return sb.toString();
    }
    
    public static byte[] strToBcdBytes(String str){
    	return strToBcdBytes(str, false);
    }

    public static byte[] strToBcdBytes(String str, boolean isPaddingLeft) {
        if (str == null)
            return new byte[0];
        str = str.toUpperCase();
        int mod = str.length() % 2;
        if (mod != 0) {
            if (!isPaddingLeft)
                str = str + "0";
            else
                str = "0" + str;
        }

        int len = str.length() / 2;
        byte[] result = new byte[len];
        char[] achar = str.toCharArray();

        for (int i = 0; i < len; i++) {
            int pos = i * 2;
            result[i] = (byte) (charToByte(achar[pos]) << 4 | charToByte(achar[pos + 1]));
        }
        return result;
    }

    private static byte charToByte(char c) {
        byte b = (byte) "0123456789ABCDEF".indexOf(c);
        return b;
    }
    public static int getLastIndexOf(byte value, byte[] items) { // 查value在items中最后一次出现的索引
        for (int i = items.length; --i >= 0;) {
            if (items[i] == value) {
                return i;
            }
        }
        return -1;
    }
}
