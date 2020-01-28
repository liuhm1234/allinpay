package com.pack.net;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

import jx.com.utils.BytesUtil;

public class TcpIpUtils {
	public static byte[] sendAndReceive(String hostIp, int hostPort,
			byte[] sendData, int timeout) {
		Socket socket = new Socket();
		try {
			// 连接
			try {
				socket.setTcpNoDelay(true);
				socket.setReuseAddress(true);
				socket.setSoTimeout(timeout * 1000); // readtimeout
				// socket.setSoLinger(true, 5);
				// socket.setKeepAlive(true);
				System.out.println("TcpIpConnection connect hostIp:" + hostIp);
				System.out.println("TcpIpConnection connect hostPort:"
						+ hostPort);
				System.out
						.println("TcpIpConnection connect timeout:" + timeout);
				socket.connect(new InetSocketAddress(hostIp, hostPort), 30000);
			} catch (Exception e) {
				System.out.println("系统连接服务器失败");
				e.printStackTrace();
				throw new NetConnectException("系统连接服务器失败:" + e.getMessage());
			}

			// 发送
			OutputStream mOutputStream;
			try {
				mOutputStream = socket.getOutputStream();
				mOutputStream.write(sendData);
				mOutputStream.flush();
			} catch (Exception e) {
				System.out.println("数据发送失败");
				e.printStackTrace();
				throw new NetSendException("发送数据失败:" + e.getMessage());
			}

			// 接收数据
			InputStream mInputStream;
			mInputStream = socket.getInputStream();
			byte[] lengthBytes;
			byte[] buffer;
			try {
				lengthBytes = new byte[2];
				mInputStream.read(lengthBytes, 0, 2);
				int dataLen = BytesUtil.byte2Int(lengthBytes);
				if (dataLen > 0) {
					buffer = new byte[dataLen];
					mInputStream.read(buffer, 0, dataLen);
				} else {
					throw new NetReceiveException("数据接收长度错:" + dataLen);
				}
			} catch (Exception e) {
				e.printStackTrace();
				throw new NetReceiveException("数据接收失败:" + e.getMessage());
			}
			try {
				mInputStream.close();
				mOutputStream.close();
			} catch (Exception e) {
				e.printStackTrace();
			}

			return buffer;
		} catch (NetConnectException | NetSendException | NetReceiveException e) {
			throw e;
		} catch (Exception e) {
			System.out.println("TcpIp通讯发生异常,堆栈信息如下");
			e.printStackTrace();
			throw new RuntimeException("服务器通讯故障:" + e.getMessage());
		} finally {
			try {
				socket.close();
			} catch (IOException e1) {
				System.out.println("关闭socket时发生异常:{}" + e1.getMessage());
			}
		}
	}
}
