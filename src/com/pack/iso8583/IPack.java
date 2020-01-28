package com.pack.iso8583;

import com.pack.iso8583.exception.PacketException;
import com.pack.iso8583.exception.UnpacketException;

public interface IPack {
	/**
     * 打包，包括报文长度、报文头、TPDU
     * @return
     * @throws PacketException
     */
    byte[] pack(IISO8583Packet packet) throws PacketException;
    
    /**
     * 解包，获取8583报文体
     * @param response
     * @return
     * @throws UnpacketException
     */
    IISO8583Packet unpack(byte[] response) throws UnpacketException;
    
    /**
     * 获取MAC计算器
     * @return
     */
    IMacCalculator getMacCalculator();
}
