package com.pack.iso8583;

public interface IMacCalculator {
	/**
	 * 计算数据的MAC值
	 * 
	 * @param data
	 *            待计算MAC的数据
	 * @return mac值
	 */
	byte[] calcMAC(byte[] data);

	/**
	 * 校验数据的MAC值是否与数据中的MAC值一致
	 * 
	 * @param data
	 *            待校验的数据
	 * @return 是否一致
	 */
	boolean checkMAC(byte[] data);
}
