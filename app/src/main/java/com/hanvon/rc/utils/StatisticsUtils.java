package com.hanvon.rc.utils;

import android.app.ProgressDialog;
import android.content.SharedPreferences;

import com.hanvon.rc.application.HanvonApplication;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @Desc:
 * @Auth: chenxzhuang
 * @Time: 2016/3/23 0023.
 */
public class StatisticsUtils {
    private static String UploadTime = "UploadTime";
    private static String LastSaveTime = "LastSaveTime";
    private static String RecordTime = "RecordTime";
    /******************主页面 MainPage************************/
    private static String MainPage = "MainPage";
    private static String QuickRcgBtn = "QuickRcgBtn";
    private static String PreciseRcgBtn = "PreciseRcgBtn";
    private static int mMainPage;
    private static int mQuickRcgBtn;
    private static int mPreciseRcgBtn;
    /**********************Left Page*************************/
    private static String LeftPage = "LeftPage";
    private static String OcrBtn = "OcrBtn";
    private static String FileBtn = "FileBtn";
    private static String OrderBtn = "OrderBtn";
    private static String SettingBtn = "SettingBtn";
    private static int mLeftPage;
    private static int mOcrBtn;
    private static int mFileBtn;
    private static int mOrderBtn;
    private static int mSettingBtn;
    /***********************Login Page************************/
    private static String LoginPage = "LoginPage";
    private static String LoginBtn = "LoginBtn";
    private static String RegisterBtn = "RegisterBtn";
    private static String QQLoginBtn = "QQLoginBtn";
    private static String WXLoginBtn = "WXLoginBtn";
    private static String RmbPwdBtn = "RmbPwdBtn";
    private static int mLoginPage;
    private static int mLoginBtn;
    private static int mRegisterBtn;
    private static int mQQLoginBtn;
    private static int mWXLoginBtn;
    private static int mRmbPwdBtn;
    /**************************Register Page******************************/
    private static String RegisterPage = "RegisterPage";
    private static String PhoneRgBtn = "PhoneRegisterBtn";
    private static String EmailRgBtn = "EmailRegisterBtn";
    private static int mRegisterPage;
    private static int mPhoneBtn;
    private static int mEmailBtn;
    /**************************Pay Page********************************/
    private static String PayPage = "PayPage";
    private static String WxPayBtn = "WXPayBtn";
    private static String AliPayBtn = "AliPayBtn";
    private static String EnsurePayBtn = "EnsurePayBtn";
    private static int mPayPage;
    private static int mWxPayBtn;
    private static int mAliPayBtn;
    private static int mEnsurePayBtn;
    /****************************Evalution Page*************************/
    private static String EvalPage = "EvaluationPage";
    private static String ModifyBtn = "ModifyBtn";
    private static String ToPayBtn = "ToPayBtn";
    private static int mEvalPage;
    private static int mModifyBtn;
    private static int mToPayBtn;
    /****************************Camera Page**************************/
    private static String CameraPage = "CameraPage";
    private static String SingleBtn = "SingleBtn";
    private static String SerialBtn = "SerialBtn";
    private static String InsertBtn = "InsertBtn";
    private static int mCameraPage;
    private static int mSingleBtn;
    private static int mSerialBtn;
    private static int mInsertBtn;
    /******************************Upload Page**********************/
    private static String UploadPage = "UploadPage";
    private static String ModifyFileBtn = "ModifyFileBtn";
    private static String TxtBtn = "TxtBtn";
    private static String PdfBtn = "PdfBtn";
    private static String DocBtn = "DocBtn";
    private static int mUploadPage;
    private static int mModifyFileBtn;
    private static int mTxtBtn;
    private static int mPdfBtn;
    private static int mDocBtn;

    private static StatisticsUtils mInstance = null;
    private static int mRefCount = 0;
    public static SharedPreferences mSharedPref = null;
    public static ProgressDialog pd;

    protected StatisticsUtils(SharedPreferences pref) {
        mSharedPref = pref;
        initConfs();
    }

    public static StatisticsUtils getInstance(SharedPreferences pref) {
        if (mInstance == null) {
            mInstance = new StatisticsUtils(pref);
        }
        assert (pref == mSharedPref);
        mRefCount++;
        return mInstance;
    }

    public static void releaseInstance() {
        mRefCount--;
        if (mRefCount == 0) {
            mInstance = null;
        }
    }

    private void initConfs(){
        mMainPage = mSharedPref.getInt(MainPage, 0);
        mQuickRcgBtn = mSharedPref.getInt(QuickRcgBtn, 0);
        mPreciseRcgBtn = mSharedPref.getInt(PreciseRcgBtn, 0);
        mLeftPage = mSharedPref.getInt(LeftPage, 0);
        mOcrBtn = mSharedPref.getInt(OcrBtn, 0);
        mFileBtn = mSharedPref.getInt(FileBtn, 0);
        mOrderBtn = mSharedPref.getInt(OrderBtn, 0);
        mSettingBtn = mSharedPref.getInt(SettingBtn, 0);
        mLoginPage = mSharedPref.getInt(LoginPage, 0);
        mLoginBtn = mSharedPref.getInt(LoginBtn, 0);
        mRegisterBtn = mSharedPref.getInt(RegisterBtn, 0);
        mQQLoginBtn = mSharedPref.getInt(QQLoginBtn, 0);
        mWXLoginBtn = mSharedPref.getInt(WXLoginBtn, 0);
        mRmbPwdBtn = mSharedPref.getInt(RmbPwdBtn, 0);
        mRegisterPage = mSharedPref.getInt(RegisterPage, 0);
        mPhoneBtn = mSharedPref.getInt(PhoneRgBtn, 0);
        mEmailBtn = mSharedPref.getInt(EmailRgBtn, 0);
        mPayPage = mSharedPref.getInt(PayPage, 0);
        mWxPayBtn = mSharedPref.getInt(WxPayBtn, 0);
        mAliPayBtn = mSharedPref.getInt(AliPayBtn, 0);
        mEnsurePayBtn = mSharedPref.getInt(EnsurePayBtn, 0);
        mEvalPage = mSharedPref.getInt(EvalPage, 0);
        mModifyBtn = mSharedPref.getInt(ModifyBtn, 0);
        mToPayBtn = mSharedPref.getInt(ToPayBtn, 0);
        mCameraPage = mSharedPref.getInt(CameraPage, 0);
        mSingleBtn = mSharedPref.getInt(SingleBtn, 0);
        mSerialBtn = mSharedPref.getInt(SerialBtn, 0);
        mInsertBtn = mSharedPref.getInt(InsertBtn, 0);
        mUploadPage = mSharedPref.getInt(UploadPage, 0);
        mModifyFileBtn = mSharedPref.getInt(ModifyFileBtn, 0);
        mTxtBtn = mSharedPref.getInt(TxtBtn, 0);
        mPdfBtn = mSharedPref.getInt(PdfBtn, 0);
        mDocBtn = mSharedPref.getInt(DocBtn, 0);
    }

    private static void Init(){
        mMainPage = 0;
        mQuickRcgBtn = 0;
        mPreciseRcgBtn = 0;
        mLeftPage = 0;
        mOcrBtn = 0;
        mFileBtn = 0;
        mOrderBtn = 0;
        mSettingBtn = 0;
        mLoginPage = 0;
        mLoginBtn = 0;
        mRegisterBtn = 0;
        mQQLoginBtn = 0;
        mWXLoginBtn = 0;
        mRmbPwdBtn = 0;
        mRegisterPage = 0;
        mPhoneBtn = 0;
        mEmailBtn = 0;
        mPayPage = 0;
        mWxPayBtn = 0;
        mAliPayBtn = 0;
        mEnsurePayBtn = 0;
        mEvalPage = 0;
        mModifyBtn = 0;
        mToPayBtn = 0;
        mCameraPage = 0;
        mSingleBtn = 0;
        mSerialBtn = 0;
        mInsertBtn = 0;
        mUploadPage = 0;
        mModifyFileBtn = 0;
        mTxtBtn = 0;
        mPdfBtn = 0;
        mDocBtn = 0;

        SharedPreferences.Editor editor = mSharedPref.edit();
        for(int i = 0;i < 24;i++){
            editor.putInt(i+"", 0);
        }
        editor.putString(LastSaveTime, getCurDate());
        editor.commit();
    }
    public static String getCurDate() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date currentTime = new Date();
        String dateString = formatter.format(currentTime);
        return dateString;
    }

    public static void WriteBack(){
        SharedPreferences.Editor editor = mSharedPref.edit();
        editor.putInt(MainPage, mMainPage);
        editor.putInt(QuickRcgBtn, mQuickRcgBtn);
        editor.putInt(PreciseRcgBtn, mPreciseRcgBtn);
        editor.putInt(LeftPage, mLeftPage);
        editor.putInt(OcrBtn, mOcrBtn);
        editor.putInt(FileBtn, mFileBtn);
        editor.putInt(OrderBtn, mOrderBtn);
        editor.putInt(SettingBtn, mSettingBtn);
        editor.putInt(LoginPage, mLoginPage);
        editor.putInt(LoginBtn, mLoginBtn);
        editor.putInt(RegisterBtn, mRegisterBtn);
        editor.putInt(QQLoginBtn, mQQLoginBtn);
        editor.putInt(WXLoginBtn, mWXLoginBtn);
        editor.putInt(RmbPwdBtn, mRmbPwdBtn);
        editor.putInt(RegisterPage, mRegisterPage);
        editor.putInt(PhoneRgBtn, mPhoneBtn);
        editor.putInt(EmailRgBtn, mEmailBtn);
        editor.putInt(PayPage, mPayPage);
        editor.putInt(WxPayBtn, mWxPayBtn);
        editor.putInt(AliPayBtn, mAliPayBtn);
        editor.putInt(EnsurePayBtn, mEnsurePayBtn);
        editor.putInt(EvalPage, mEvalPage);

        editor.putInt(ModifyBtn, mModifyBtn);
        editor.putInt(ToPayBtn, mToPayBtn);
        editor.putInt(CameraPage, mCameraPage);
        editor.putInt(SingleBtn, mSingleBtn);
        editor.putInt(SerialBtn, mSerialBtn);
        editor.putInt(InsertBtn, mInsertBtn);
        editor.putInt(UploadPage, mUploadPage);
        editor.putInt(ModifyFileBtn, mModifyFileBtn);
        editor.putInt(TxtBtn, mTxtBtn);
        editor.putInt(PdfBtn, mPdfBtn);
        editor.putInt(DocBtn, mDocBtn);
        editor.putString(RecordTime, getCurDate());
        editor.commit();
    }

    public static void IncreaseDocBtn() {mDocBtn++;}
    public static void IncreaseMainPage() {mMainPage++;}
    public static void IncreaseQuickRcgBtn() {mQuickRcgBtn++;}
    public static void IncreasePreciseRcgBtn() {mPreciseRcgBtn++;}
    public static void IncreaseLeftPage() {mLeftPage++;}
    public static void IncreaseOcrBtn() {mOcrBtn++;}
    public static void IncreaseFileBtn() {mFileBtn++;}
    public static void IncreaseOrderBtn() {mOrderBtn++;}
    public static void IncreaseSettingBtn() {mSettingBtn++;}
    public static void IncreaseLoginPage() {mLoginPage++;}
    public static void IncreaseLoginBtn() {mLoginBtn++;}
    public static void IncreaseRegisterBtn() {mRegisterBtn++;}
    public static void IncreaseQQLoginBtn() {mQQLoginBtn++;}
    public static void IncreaseWXLoginBtn() {mWXLoginBtn++;}
    public static void IncreaseRmbPwdBtn() {mRmbPwdBtn++;}
    public static void IncreaseRegisterPage() {mRegisterPage++;}
    public static void IncreasePhoneBtn() {mPhoneBtn++;}
    public static void IncreaseEmailBtn() {mEmailBtn++;}
    public static void IncreasePayPage() {mPayPage++;}
    public static void IncreaseWxPayBtn() {mWxPayBtn++;}
    public static void IncreaseAlipayBtn() {mAliPayBtn++;}
    public static void IncreaseEnsurePayBtn() {mEnsurePayBtn++;}
    public static void IncreaseEvalPage() {mEvalPage++;}
    public static void IncreaseModifyBtn() {mModifyBtn++;}
    public static void IncreaseToPayBtn() {mToPayBtn++;}
    public static void IncreaseSingleBtn() {mSingleBtn++;}
    public static void IncreaseSerialBtn() {mSerialBtn++;}
    public static void IncreaseInsertBtn() {mInsertBtn++;}
    public static void IncreaseUploadPage() {mUploadPage++;}
    public static void IncreasemModifyFileBtn() {mModifyFileBtn++;}
    public static void IncreaseTxtBtn() {mTxtBtn++;}
    public static void IncreasePdfBtn() {mPdfBtn++;}
    public static void IncreaseCameraPage() {mCameraPage++;}

    public static JSONObject StatisticsJson(JSONObject paramJson){
        try {
            paramJson.put("ver", HanvonApplication.AppVer);
            paramJson.put("longitude", HanvonApplication.curLongitude);
            paramJson.put("latitude", HanvonApplication.curLatitude);
            paramJson.put("locationCountry", HanvonApplication.curCountry);
            paramJson.put("locationProvince", HanvonApplication.curProvince);
            paramJson.put("locationCity", HanvonApplication.curCity);
            paramJson.put("locationArea", HanvonApplication.curDistrict);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return paramJson;
    }

/*
    public static void UpLoadFunctionStatus1(Context mcontext) throws Exception{
        String uploadTime = mSharedPref.getString(UploadTime, "");
        String lastSaveTime = mSharedPref.getString(LastSaveTime, "");
        String curTime = getCurDate();

        if (uploadTime.equals("")){
            SharedPreferences.Editor editor = mSharedPref.edit();
            editor.putString(UploadTime, curTime);
            editor.putString(LastSaveTime, curTime);
            editor.commit();
            return;
        }
        if (uploadTime.equals(curTime)){
            return;
        }

     //   mFunctionDao = new StatisticsFunctionDao(mcontext);

        if (new ConnectionDetector(HanvonApplication.getcontext()).isConnectingTOInternet()) {
            pd = ProgressDialog.show(mcontext, "", "");
            new Thread(UpLoadFunctionThread).start();
        }else{
            if (lastSaveTime.equals(curTime)){
                return;
            }else{
                SaveLastToDb();
            }
        }
    }

    private static void SaveLastToDb() throws Exception{
        JSONArray functionJson = GetActionJson();
        StatisticsFunction mFunction = new StatisticsFunction();
        Object obj;
        int count = 0;
        if (mLoginPage == 0){
            mFunction.setLoginpage("");
        }else{
            obj = functionJson.get(count);
            mFunction.setLoginpage(Base64Utils.encode(obj.toString().getBytes()));
            count++;
        }
        if (mMainPage == 0){
            mFunction.setMainpage("");
        }else{
            obj = functionJson.get(count);
            count++;
            mFunction.setMainpage(Base64Utils.encode(obj.toString().getBytes()));
        }
        if (mEditNoteBookPage == 0){
            mFunction.setEditNoteBookPage("");
        }else{
            obj = functionJson.get(count);
            count++;
            mFunction.setEditNoteBookPage(Base64Utils.encode(obj.toString().getBytes()));
        }
        if (mEditNoteRecodePage == 0){
            mFunction.setEditNoteRecodePage("");
        }else{
            obj = functionJson.get(count);
            count++;
            mFunction.setEditNoteRecodePage(Base64Utils.encode(obj.toString().getBytes()));
        }
        if (mSettingPage == 0){
            mFunction.setSettingPage("");
        }else{
            obj = functionJson.get(count);
            count++;
            mFunction.setSettingPage(Base64Utils.encode(obj.toString().getBytes()));
        }
        if (mSeleteNoteBookPage == 0){
            mFunction.setSeleteNoteBookPage("");
        }else{
            obj = functionJson.get(count);
            count++;
            mFunction.setSeleteNoteBookPage(Base64Utils.encode(obj.toString().getBytes()));
        }

       /*******记录活动时间**********/
    /*
        JSONObject actionTimeJson = new JSONObject();
        for(int i = 0;i < 24;i++){
            int value = mSharedPref.getInt(i+"",0);
            if (value == 1){
                actionTimeJson.put(i+"", value+"");
            }
        }
        mFunction.setTimeJson(Base64Utils.encode(actionTimeJson.toString().getBytes()));

        String createTime = mSharedPref.getString(RecordTime, "");
        mFunction.setCreateTime(createTime);

     //   mFunctionDao.add(mFunction);

        Init();
    }
*/
}
