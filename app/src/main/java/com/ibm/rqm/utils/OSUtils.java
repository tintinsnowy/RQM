package com.ibm.rqm.utils;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.os.StatFs;
import android.os.Vibrator;
import android.telephony.TelephonyManager;

import java.io.File;

public final class OSUtils {
	public enum NetWorkState {
		NoState, MobileState, WifiState,
	};

	private static int sScreenWidth = 480;
	private static int sScreenHeight = 800;
	// private static int sMaxWidth = 480 * 2;
	// private static int sMaxHeight = 800 * 2;
	private static String sIMEIId = "";
	private static String sMACId = "";
	private static String sIMSIId = "";
	private static String sPhoneNumber = "";
	private static String sSoftwareVersion = "";

	public static int getScreenWidth() {
		return sScreenWidth;
	}

	public static int getScreenHeight() {
		return sScreenHeight;
	}

	public static int getScenceWidth() {
		return sScreenWidth;
	}

	public static int getScenceHeight() {
		return sScreenHeight;
	}

	// <uses-permission
	// android:name="android.permission.WRITE_EXTERNAL_STORAGE"></uses-permission>
	// <uses-permission android:name="android.permission.READ_PHONE_STATE" />
	public static void InitOs(Activity a) {
		if (!StringUtils.isEmpty(sIMEIId))
			return;

		try {
			sScreenWidth = a.getWindowManager().getDefaultDisplay().getWidth();
			sScreenHeight = a.getWindowManager().getDefaultDisplay()
					.getHeight();
			WifiManager wifi = (WifiManager) a
					.getSystemService(Context.WIFI_SERVICE);
			WifiInfo info = wifi.getConnectionInfo();
			TelephonyManager tm = (TelephonyManager) a
					.getSystemService(Activity.TELEPHONY_SERVICE);
			sMACId = info.getMacAddress();
			sIMEIId = tm.getDeviceId();
			// sPhoneNumber = tm.getLine1Number();
			sSoftwareVersion = tm.getDeviceSoftwareVersion();
			sIMSIId = tm.getSubscriberId();
			/*
			 * ��ǰʹ�õ��������ͣ� ���磺 NETWORK_TYPE_UNKNOWN ��������δ֪ 0 NETWORK_TYPE_GPRS
			 * GPRS���� 1 NETWORK_TYPE_EDGE EDGE���� 2 NETWORK_TYPE_UMTS UMTS���� 3
			 * NETWORK_TYPE_HSDPA HSDPA���� 8 NETWORK_TYPE_HSUPA HSUPA���� 9
			 * NETWORK_TYPE_HSPA HSPA���� 10 NETWORK_TYPE_CDMA CDMA����,IS95A ��
			 * IS95B. 4 NETWORK_TYPE_EVDO_0 EVDO����, revision 0. 5
			 * NETWORK_TYPE_EVDO_A EVDO����, revision A. 6 NETWORK_TYPE_1xRTT
			 * 1xRTT���� 7
			 */
			tm.getNetworkType();
			/*
			 * �ֻ����ͣ� ���磺 PHONE_TYPE_NONE ���ź� PHONE_TYPE_GSM GSM�ź�
			 * PHONE_TYPE_CDMA CDMA�ź�
			 */
			tm.getPhoneType();
			/*
			 * Returns the MCC+MNC (mobile country code + mobile network code)
			 * of the provider of the SIM. 5 or 6 decimal digits.
			 * ��ȡSIM���ṩ���ƶ��������ƶ�������.5��6λ��ʮ��������. SIM����״̬������
			 * SIM_STATE_READY(ʹ��getSimState()�ж�).
			 */
			tm.getSimOperator();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public static String getIMEIId() {
		if (sIMEIId == null)
			return "";
		return sIMEIId;
	}

	public static String getMACId() {
		if (sMACId == null)
			return "";
		return sMACId;
	}

	public static String getIMSIId() {
		if (sIMSIId == null)
			return "";
		return sIMSIId;
	}

	public static String getPhoneNumber() {
		if (sPhoneNumber == null)
			return "";
		return sPhoneNumber;
	}

	public static String getSoftwareVersion() {
		return sSoftwareVersion;
	}

	public static boolean IsSdCardMounted() {
		return Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED);
	}

	public static String getSdCardDirectory() {
		File path = Environment.getExternalStorageDirectory();
		return path.getPath();
	}

	public static StatFs getSdCardStatFs() {
		File path = Environment.getExternalStorageDirectory();
		return new StatFs(path.getPath());
	}

	public static boolean ExistSDCard() {
		boolean flag = false;
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			flag = true;
		}
		return flag;
	}

	/**
	 * �ֻ�����ʾ
	 */
	public static void mobileShake(Context context, int ms) {
		Object obj = context.getSystemService(Service.VIBRATOR_SERVICE);
		if (obj instanceof Vibrator) {
			((Vibrator) obj).vibrate(ms);
		}
	}

}
