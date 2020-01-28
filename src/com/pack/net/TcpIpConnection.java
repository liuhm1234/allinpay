package com.pack.net;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.security.KeyStore;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.TrustManagerFactory;

import jx.com.utils.BytesUtil;
import jx.com.utils.Convert;

public class TcpIpConnection extends Connection {
	private String hostIp;
	private int hostPort;
	private Socket socket;
	private boolean isLive = false;
	private CAParam caParam;

	public TcpIpConnection() {
	}

	public void initCommPara(String hostIp, int hostPort, int connectTimeout,
			int receiveTimeout) {
		this.initCommPara(hostIp, hostPort, connectTimeout, receiveTimeout,
				null);
	}

	public void initCommPara(String hostIp, int hostPort, int connectTimeout,
			int receiveTimeout, CAParam caParam) {
		this.caParam = caParam;
		this.hostIp = hostIp;
		this.hostPort = hostPort;
		this.setReceiveTimeout(receiveTimeout * 1000);
		this.setConnectTimeout(connectTimeout * 1000);
	}

	private Socket getSocket() {
		if (socket == null) {
			if (caParam != null) {
				try {
					socket = getSslSocket(caParam.getCertificate(),
							caParam.getCfPwd(), caParam.getCA(),
							caParam.getCaPwd());
				} catch (Exception e) {
					e.printStackTrace();
					close();
					throw new NetConnectException("SSLSOCKET创建失败");
				}
			} else {
				socket = new Socket();
			}
		}

		return socket;
	}

	private SSLSocket getSslSocket(byte[] certificate, String cfPwd, byte[] CA,
			String caPwd) throws Exception {

		KeyStore kks = KeyStore.getInstance("JKS");
		ByteArrayInputStream keyval = new ByteArrayInputStream(certificate);
		kks.load(keyval, cfPwd.toCharArray());
		KeyManagerFactory keyManager = KeyManagerFactory.getInstance("X509");
		keyManager.init(kks, cfPwd.toCharArray());

		KeyStore tks = KeyStore.getInstance("JKS");
		ByteArrayInputStream tksval = new ByteArrayInputStream(CA);
		tks.load(tksval, caPwd.toCharArray());
		TrustManagerFactory trustManager = TrustManagerFactory
				.getInstance("X509");
		trustManager.init(tks);
		SSLContext sslContext = SSLContext.getInstance("TLS");
		sslContext.init(keyManager.getKeyManagers(),
				trustManager.getTrustManagers(), null);

		SSLSocket sslSocket = (SSLSocket) sslContext.getSocketFactory()
				.createSocket(this.hostIp, this.hostPort);
		sslSocket.setNeedClientAuth(false);
		sslSocket.setSoTimeout(this.getReceiveTimeout());
		return sslSocket;
	}

	public byte[] sendAndReceive(byte[] sendData) {

		socket = getSocket();
		InetSocketAddress remoteAddr = new InetSocketAddress(this.hostIp,
				this.hostPort);
		try {
			socket.connect(remoteAddr, getConnectTimeout());
			socket.setSoTimeout(getReceiveTimeout());
		} catch (IOException e) {
			e.printStackTrace();
			close();
			throw new NetConnectException("系统连接服务器失败");
		}

		return sendBasicData(sendData);
	}

	private byte[] sendBasicData(byte[] sendData) {
		InputStream mInputStream;
		OutputStream mOutputStream;

		try {
			mInputStream = socket.getInputStream();
			mOutputStream = socket.getOutputStream();
			mOutputStream.write(sendData);
			mOutputStream.flush();
		} catch (Exception e) {
			e.printStackTrace();
			close();
			throw new NetSendException("发送数据失败");
		}

		byte[] lengthBytes;
		byte[] buffer;
		try {
			lengthBytes = new byte[2];
			mInputStream.read(lengthBytes, 0, 2);
			int dataLen = BytesUtil.byte2Int(lengthBytes);
			System.out.println("接收数据len:" + dataLen);
			if (dataLen > 0) {
				buffer = new byte[dataLen];
				mInputStream.read(buffer, 0, dataLen);
			} else {
				throw new NetReceiveException("数据接收长度错:" + dataLen);
			}
		} catch (Exception e) {
			e.printStackTrace();
			this.close();
			throw new NetReceiveException("数据接收失败");
		}

		// byte[] rcvData = BytesUtil.mergeBytes(lengthBytes, buffer);

		try {
			mInputStream.close();
			mOutputStream.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (!this.isLive) {
			close();
		}

		System.out.println(Convert.bcdBytesToStr(buffer));
		return buffer;
	}

	public boolean isLive() {
		return this.isLive;
	}

	public void setLive(boolean isLive) {
		this.isLive = isLive;
	}

	public void close() {
		if (socket != null) {
			try {
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			socket = null;
		}
	}

	public static class CAParam {
		private byte[] certificate;
		private byte[] CA;
		private String cfPwd;
		private String caPwd;

		public CAParam(byte[] certificate, String cfPwd, byte[] CA, String caPwd) {
			this.certificate = new byte[certificate.length];
			this.CA = new byte[CA.length];
			System.arraycopy(certificate, 0, this.certificate, 0,
					certificate.length);
			System.arraycopy(CA, 0, this.CA, 0, CA.length);
			this.cfPwd = cfPwd;
			this.caPwd = caPwd;
		}

		public byte[] getCertificate() {
			return this.certificate;
		}

		public void setCertificate(byte[] certificate) {
			this.certificate = certificate;
		}

		public byte[] getCA() {
			return this.CA;
		}

		public void setCA(byte[] cA) {
			this.CA = cA;
		}

		public String getCfPwd() {
			return this.cfPwd;
		}

		public void setCfPwd(String cfPwd) {
			this.cfPwd = cfPwd;
		}

		public String getCaPwd() {
			return this.caPwd;
		}

		public void setCaPwd(String caPwd) {
			this.caPwd = caPwd;
		}
	}
}
