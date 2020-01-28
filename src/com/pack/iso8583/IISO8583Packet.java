package com.pack.iso8583;

import com.pack.iso8583.exception.PacketException;

/*****
 * ISO8583PacketImpl
 * FieldCodecManager
 * @author zhaojx
 *
 */
public interface IISO8583Packet {
	int _FLD0_ = 0;
	int _FLD1_ = 1;
	int _FLD2_ = 2;
	int _FLD3_ = 3;
	int _FLD4_ = 4;
	int _FLD5_ = 5;
	int _FLD6_ = 6;
	int _FLD7_ = 7;
	int _FLD8_ = 8;
	int _FLD9_ = 9;
	int _FLD10_ = 10;
	int _FLD11_ = 11;
	int _FLD12_ = 12;
	int _FLD13_ = 13;
	int _FLD14_ = 14;
	int _FLD15_ = 15;
	int _FLD16_ = 16;
	int _FLD17_ = 17;
	int _FLD18_ = 18;
	int _FLD19_ = 19;
	int _FLD20_ = 20;
	int _FLD21_ = 21;
	int _FLD22_ = 22;
	int _FLD23_ = 23;
	int _FLD24_ = 24;
	int _FLD25_ = 25;
	int _FLD26_ = 26;
	int _FLD27_ = 27;
	int _FLD28_ = 28;
	int _FLD29_ = 29;
	int _FLD30_ = 30;
	int _FLD31_ = 31;
	int _FLD32_ = 32;
	int _FLD33_ = 33;
	int _FLD34_ = 34;
	int _FLD35_ = 35;
	int _FLD36_ = 36;
	int _FLD37_ = 37;
	int _FLD38_ = 38;
	int _FLD39_ = 39;
	int _FLD40_ = 40;
	int _FLD41_ = 41;
	int _FLD42_ = 42;
	int _FLD43_ = 43;
	int _FLD44_ = 44;
	int _FLD45_ = 45;
	int _FLD46_ = 46;
	int _FLD47_ = 47;
	int _FLD48_ = 48;
	int _FLD49_ = 49;
	int _FLD50_ = 50;
	int _FLD51_ = 51;
	int _FLD52_ = 52;
	int _FLD53_ = 53;
	int _FLD54_ = 54;
	int _FLD55_ = 55;
	int _FLD56_ = 56;
	int _FLD57_ = 57;
	int _FLD58_ = 58;
	int _FLD59_ = 59;
	int _FLD60_ = 60;
	int _FLD61_ = 61;
	int _FLD62_ = 62;
	int _FLD63_ = 63;
	int _FLD64_ = 64;
	int _FLD65_ = 65;
	int _FLD66_ = 66;
	int _FLD67_ = 67;
	int _FLD68_ = 68;
	int _FLD69_ = 69;
	int _FLD70_ = 70;
	int _FLD71_ = 71;
	int _FLD72_ = 72;
	int _FLD73_ = 73;
	int _FLD74_ = 74;
	int _FLD75_ = 75;
	int _FLD76_ = 76;
	int _FLD77_ = 77;
	int _FLD78_ = 78;
	int _FLD79_ = 79;
	int _FLD80_ = 80;
	int _FLD81_ = 81;
	int _FLD82_ = 82;
	int _FLD83_ = 83;
	int _FLD84_ = 84;
	int _FLD85_ = 85;
	int _FLD86_ = 86;
	int _FLD87_ = 87;
	int _FLD88_ = 88;
	int _FLD89_ = 89;
	int _FLD90_ = 90;
	int _FLD91_ = 91;
	int _FLD92_ = 92;
	int _FLD93_ = 93;
	int _FLD94_ = 94;
	int _FLD95_ = 95;
	int _FLD96_ = 96;
	int _FLD97_ = 97;
	int _FLD98_ = 98;
	int _FLD99_ = 99;
	int _FLD100_ = 100;
	int _FLD101_ = 101;
	int _FLD102_ = 102;
	int _FLD103_ = 103;
	int _FLD104_ = 104;
	int _FLD105_ = 105;
	int _FLD106_ = 106;
	int _FLD107_ = 107;
	int _FLD108_ = 108;
	int _FLD109_ = 109;
	int _FLD110_ = 110;
	int _FLD111_ = 111;
	int _FLD112_ = 112;
	int _FLD113_ = 113;
	int _FLD114_ = 114;
	int _FLD115_ = 115;
	int _FLD116_ = 116;
	int _FLD117_ = 117;
	int _FLD118_ = 118;
	int _FLD119_ = 119;
	int _FLD120_ = 120;
	int _FLD121_ = 121;
	int _FLD122_ = 122;
	int _FLD123_ = 123;
	int _FLD124_ = 124;
	int _FLD125_ = 125;
	int _FLD126_ = 126;
	int _FLD127_ = 127;
	int _FLD128_ = 128;

	void setMsgId(String msgId);

	String getMsgId();

	void setCheckMAC(boolean checkMAC);

	boolean isCheckMAC();

	void setField(int bitNo, String value);

	String getField(int bitNo);

	void deleteField(int bitNo);

	void setDebug(boolean debug);
	
	byte[] toBytes() throws PacketException;
}
