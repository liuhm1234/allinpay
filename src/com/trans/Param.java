package com.trans;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Param {
	public final static String MID = "mid";
	public final static String TID = "tid";
	public final static String TRANCE = "trance";
	public final static String BATCH = "batch";
	public final static String TMK = "tmk";
	public final static String TPK = "tpk";
	public final static String TAK = "tak";
	public final static String TDK = "tdk";
	public final static String INIT_KEY = "initTmk";

	public final static String FILE_NAME = "param.properties";

	static Param instance;
	static Properties props = new Properties();

	private Param() {
	}

	static {
		load();
	}

	public static void load() {
		props = new Properties();
		InputStream in = null;
		try {
			in = new FileInputStream(FILE_NAME);
			props.load(in);
		} catch (FileNotFoundException e) {
			System.out.println("FileNotFoundException " + e.getMessage());
		} catch (IOException e) {
			System.out.println("IOException " + e.getMessage());
		} finally {
			try {
				if (null != in) {
					in.close();
				}
			} catch (IOException e) {
			}
		}
	}

	public static String get(String key) {
		return get(key, "");
	}

	public static String get(String key, String defaultValue) {
		if (null == props) {
			load();
		}
		System.out.println("查询参数" + key + "的值:" + props.getProperty(key));
		return props.getProperty(key, defaultValue);
	}

	public static void set(String key, String val) {
		if (null == props) {
			load();
		}

		props.setProperty(key, val);
		System.out.println("设置参数" + key + "的值:" + props.getProperty(key));
		save();
	}

	public static void save() {
		if (null == props) {
			return;
		}

		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(FILE_NAME);
			props.store(fos, "modify on ");

			fos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
