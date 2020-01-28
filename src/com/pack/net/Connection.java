package com.pack.net;

import java.io.InputStream;

import javax.net.ssl.HostnameVerifier;

public abstract class Connection {
    private int connectTimeout = 30000;
    private int receiveTimeout = 30000;
    private String clientAgreement = "TLS";
    private String clientKeyManager = "X509";
    private String clientTrustManager = "X509";
    private String keyStoreType = "JKS";
    private InputStream keystoreStream;
    private String password;
    private HostnameVerifier hostnameVerifier;

    public String getClientAgreement() {
        return this.clientAgreement;
    }

    public void setClientAgreement(String clientAgreement) {
        this.clientAgreement = clientAgreement;
    }

    public String getClientKeyManager() {
        return this.clientKeyManager;
    }

    public void setClientKeyManager(String clientKeyManager) {
        this.clientKeyManager = clientKeyManager;
    }

    public String getClientTrustManager() {
        return this.clientTrustManager;
    }

    public void setClientTrustManager(String clientTrustManager) {
        this.clientTrustManager = clientTrustManager;
    }

    public String getKeyStoreType() {
        return this.keyStoreType;
    }

    public void setKeyStoreType(String keyStoreType) {
        this.keyStoreType = keyStoreType;
    }

    public void setKeyStoreStream(InputStream inputStream, String password) {
        this.keystoreStream = inputStream;
        this.password = password;
    }

    public InputStream getKeyStoreStream() {
        return this.keystoreStream;
    }

    public char[] getPassword() {
        return this.password != null && this.password.length() > 0 ? this.password.toCharArray() : null;
    }

    public Connection() {
    }

    public int getConnectTimeout() {
        return this.connectTimeout;
    }

    public void setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public int getReceiveTimeout() {
        return this.receiveTimeout;
    }

    public void setReceiveTimeout(int receiveTimeout) {
        this.receiveTimeout = receiveTimeout;
    }

    public HostnameVerifier getHostnameVerifier() {
        return this.hostnameVerifier;
    }

    public void setHostnameVerifier(HostnameVerifier hostnameVerifier) {
        this.hostnameVerifier = hostnameVerifier;
    }

}