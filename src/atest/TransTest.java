package atest;

import com.trans.InitTmkTrans;
import com.trans.LogonTrans;
import com.trans.OnlineNotify;
import com.trans.SaleTrans;
import com.trans.ScanTrans;
import com.trans.SybLogonTrans;
import com.trans.SybSaleTrans;
import com.trans.UpdateTmkTrans;

public class TransTest {
	public static void main(String[] args) {
		//银行卡
		//downTmk("96436097"); // 下载主密钥
		//logon(); // 联机签到
		//doSale(); // 磁条卡消费
		//doScan();
		
		//收银宝
		downTmk("67863219"); // 下载主密钥
		//sybLogon();
		//doSybSale(); //收银宝磁卡消费
	}

	public static void downTmk(String initPwd) {
		if (new InitTmkTrans(initPwd).doTrans()) {
			if (new UpdateTmkTrans().doTrans()) {
				new OnlineNotify().doTrans();
			}
		}
	}

	public static void logon() {
		new LogonTrans().doTrans();
	}
	
	public static void sybLogon() {
		new SybLogonTrans().doTrans();
	}

	public static void doSale() {
		int amount = 10;
		String track2 = "111111111111111111111111111111111"; // 请根据各自终端自行组织
		String track3 = ""; // 请根据各自终端自行组织
		String plainPin = ""; // 请根据各自终端自行组织
		String sn = "A2041010020202016000002022L81259605008422503  ";
		// String sn = ""; //请根据各自终端自行组织
		SaleTrans saleTrans = new SaleTrans();

		saleTrans.setAmount(amount);
		saleTrans.setTrack2(track2);
		saleTrans.setTrack3(track3);
		saleTrans.setPlainPin(plainPin);
		saleTrans.setSn(sn);
		saleTrans.doTrans();
	}
	
	public static void doSybSale() {
		int amount = 1;
		String track2 = "4392258321825798=220110111147561"; // 请根据各自终端自行组织
		String track3 = "994392258321825798=15615619999999930019990000000343434220110==000000080058743000000008308999012050010100"; // 请根据各自终端自行组织
		String plainPin = "836217"; // 请根据各自终端自行组织
		String sn = "A2041010020202016000002022L81259605008422503  ";
		// String sn = ""; //请根据各自终端自行组织
		SybSaleTrans saleTrans = new SybSaleTrans();

		saleTrans.setAmount(amount);
		saleTrans.setTrack2(track2);
		saleTrans.setTrack3(track3);
		saleTrans.setPlainPin(plainPin);
		saleTrans.setSn(sn);
		saleTrans.doTrans();
	}

	public static void doScan() {
		int amount = 10;
		String qrCode = "123456789012345678";

		ScanTrans scanTrans = new ScanTrans();
		scanTrans.setAmount(amount);
		scanTrans.setQrCode(qrCode);
		scanTrans.doTrans();
	}
}
