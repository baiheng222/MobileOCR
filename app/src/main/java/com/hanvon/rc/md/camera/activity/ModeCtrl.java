package com.hanvon.rc.md.camera.activity;

public class ModeCtrl
{

	public static final int BARCODE_ON = 0x01;
	public static final int QRCODE_ON = 0x02;
	public static final int SCANNING_ON=0x03;
	public static final int BCARD_ON = 0x04;
	public static final int BCARDADD_ON = 0x07;
	public static final int SHOPPING_ON = 0x08;

	private static ModeCtrl modeCtrl;
	private static UserMode userMode;
	private static int recMode;
	private static boolean isUserModeChanged;
	private static boolean isScanningStop = false;
	private static boolean isBCardScanningStop = false;

	public enum UserMode {
		SCANNING,BCARD, SHOPPING
	}

	public ModeCtrl() {
		setModeCtrl(this);
		setRecMode(BCARD_ON);
		setUserMode(UserMode.BCARD);
	}

	public static void release() {
		setModeCtrl(null);
	}

	public static UserMode getUserMode() {
		return userMode;
	}

	public static void setUserMode(UserMode userMode) {
		ModeCtrl.userMode = userMode;
	}

	public static int getRecMode() {
		return recMode;
	}

	public static void setRecMode(int recMode) {
		ModeCtrl.recMode = recMode;
	}

	/*public static boolean isUserModeChanged() {
		return isUserModeChanged;
	}

	public static void setUserModeChanged(boolean isUserModeChanged) {
		ModeCtrl.isUserModeChanged = isUserModeChanged;
	}*/

	public static ModeCtrl getModeCtrl() {
		return modeCtrl;
	}

	public static void setModeCtrl(ModeCtrl modeCtrl) {
		ModeCtrl.modeCtrl = modeCtrl;
	}

	public static boolean isUserModeChanged() {
		return isUserModeChanged;
	}

	public static void setUserModeChanged(boolean isUserModeChanged) {
		ModeCtrl.isUserModeChanged = isUserModeChanged;
	}

	public static boolean isScanningStop() {
		return isScanningStop;
	}

	public static void setScanningStop(boolean isScanningStop) {
		ModeCtrl.isScanningStop = isScanningStop;
	}

	public static boolean isBCardScanningStop() {
		return isBCardScanningStop;
	}

	public static void setBCardScanningStop(boolean isBCardScanningStop) {
		ModeCtrl.isBCardScanningStop = isBCardScanningStop;
	}
	public static void initScanningFlag(){
		ModeCtrl.setScanningStop(false);
		ModeCtrl.setBCardScanningStop(false);
	}
}
