package jx.com.utils;

import java.math.BigInteger;
import java.text.DecimalFormat;

public class FormatUtil {
	public static void main(String[] args) {
		System.out.println(getFromatAmount(new BigInteger("100000")));
	}
	
	public static String getFormatAmount(String amount) {
        int mAmount = (int)(Double.parseDouble(amount) * 1.0D);
        return String.format("%012d", new Object[]{Integer.valueOf(mAmount)});
    }
	
	public static String getFromatAmount(BigInteger amount) {
        String mAmount = String.format("%012d", new Object[]{amount});
        return mAmount.substring(mAmount.length() - 12, mAmount.length());
    }
	
	public static String getReadableAmount(String amount) {
        if(amount != null && !amount.isEmpty()) {
            DecimalFormat df = new DecimalFormat("0.00");
            return df.format(Double.parseDouble(amount) / 100.0D);
        } else {
            return "0.00";
        }
    }
	
    public static String str2Amt(String mount){
        String formatString = formatString(mount);
        System.out.println("formatString:"+formatString);
        return Long.valueOf(formatString)/100 + "."+Long.valueOf(formatString)/100;
    }

    /*
     * 0000000001250转化成12.50
	 */
    public static String formatAmount(String mount) {
        if ("".equals(mount) || mount == null) {
            return "0.00";
        }

        if (mount.contains(".")) {
            int index = mount.lastIndexOf(".");
            if (index == 0) {
                mount = "0" + mount;
                return mount;
            }

            String front = mount.substring(0, index);
            int len = front.length();
            for (int i = len - 3; i > 0; i = i - 3) {
                front = front.substring(0, i) + "," + front.substring(i);
            }

            mount = front + mount.substring(index, mount.length());
            return mount;
        }

        long amount = Long.valueOf(mount);
        if (amount > 0) {
            int len = mount.length();
            for (int i = len - 3; i > 0; i = i - 3) {
                mount = mount.substring(0, i) + "," + mount.substring(i);
            }
            return mount;

        }
        else {
            return "0";
        }
    }

    /**
     * 手机号码格式化
     *
     * @param phone
     * @return
     * @createtor：Administrator
     * @date:2014-3-17 下午4:58:53
     */
    public static String formatPhoneNum(String phone) {
        StringBuffer str = new StringBuffer();
        if (!"".equals(phone) && null != phone) {
            if (phone.length() > 7) {
                str.append(phone.substring(0, 3) + " " + phone.substring(3, 7) + " " + phone.substring(7));
            }
            else if (phone.length() > 3 && phone.length() != 7) {
                str.append(phone.substring(0, 3) + " " + phone.substring(3));
            }
            else if (phone.length() == 7) {
                str.append(phone.substring(0, 3) + " " + phone.substring(3, 7) + " ");
            }
            else if (phone.length() == 3) {
                str.append(phone.substring(0, 3) + " ");
            }
            else if (phone.length() > 0) {
                str.append(phone.substring(0));
            }
        }
        return str.toString();
    }

    /**
     * formatString
     * 去除字符中间的 "空格/-/," 等间隔符
     *
     * @param string 要格式化的字符
     * @return 格式化后的字符
     */
    public static String formatString(String string) {
        if (string == null) return "";
        String newString = string.replaceAll(" ", "")
                            .replaceAll("-", "")
                            .replaceAll(",", "");
        return newString;
    }

}