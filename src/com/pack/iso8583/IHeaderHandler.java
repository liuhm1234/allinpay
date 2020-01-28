package com.pack.iso8583;

public interface IHeaderHandler {
	/**
	 * 生成报文头，发送数据前调用。所返回的报文头数据会被填充到数据包前面
	 * 
	 * @return
	 */
	byte[] addHeader(byte[] packet);

	/**
	 * 移除报文头
	 * 
	 * @param packet
	 * @return
	 */
	byte[] removeHeader(byte[] packet);

	/**
	 * 获取报文头，数据接收后调用。原始应答数据会被砍掉报文头
	 * 
	 * @param packet
	 * @return
	 */
	byte[] getHeader(byte[] packet);
}
