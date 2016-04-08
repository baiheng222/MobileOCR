package com.hanvon.rc.application;

import android.app.ActivityManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;

import com.baidu.frontia.FrontiaApplication;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.hanvon.rc.utils.LogUtil;
import com.hanvon.rc.wboard.Constants;
import com.hanvon.userinfo.UserInfoMessage;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.tauth.Tencent;

import java.util.List;

import cn.sharesdk.framework.ShareSDK;
/**
 * @Desc:
 * @Auth: chenxzhuang
 * @Time: 2016/3/23 0023.
 */
public class HanvonApplication extends FrontiaApplication {
    public static Bitmap BitHeadImage;
    public LocationClient mLocationClient = null;
    public BDLocationListener myListener;
    public String curAddress = "";
    public String addrDetail="";
    public static String curCity="";

    /************增加统计需要的国家、省份、城市、区县以及经纬度************************/
    public static String curCountry = "";
    public static String curProvince = "";
    public static String curDistrict = "";
    public static String curLongitude = "";//经度
    public static String curLatitude = "";//维度
    /***********************END*********************************/

    public static String strName = "";
    public static String strEmail = "";
    public static String strPhone = "";
    public static boolean isActivity;
    public static int userFlag;/* 0  汉王用户   1 QQ账号   2 微信账号   3 微博账号*/

    public static String hvnName = "";//第三方登陆时对应于汉王用户名称，汉王用户登陆时对应于邮箱

    public static String AppSid = "MobileOCR_Software";
    public static String AppUid = "";
    public static String AppVer = "";
    public static String AppDeviceId = "";

    public static  String mWeather;

    private static Context mContext;
    //三方登录相关
    public static Tencent mTencent = null;
    private String QQ_APPID = "1105311110";
    public static IWXAPI api;

    @Override
    public void onCreate() {
        super.onCreate();
        ShareSDK.initSDK(this);
        UserInfoMessage.setIsOnLine(false);
        mLocationClient = new LocationClient(getApplicationContext());
        myListener = new MyLocationListener();
        mLocationClient.registerLocationListener(myListener);
        InitLocation();
        mLocationClient.start();

        api = WXAPIFactory.createWXAPI(this, "wx021fab5878ea9288", true);
        api.registerApp("wx021fab5878ea9288");
        mTencent = Tencent.createInstance(QQ_APPID, HanvonApplication.this);

//		/**获取uid**/
        ActivityManager am = (ActivityManager) getSystemService(this.getApplicationContext().ACTIVITY_SERVICE);
        ApplicationInfo appinfo = getApplicationInfo();
        List<ActivityManager.RunningAppProcessInfo> run = am.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo runningProcess : run) {
            if ((runningProcess.processName != null) && runningProcess.processName.equals(appinfo.processName)) {
                AppUid = String.valueOf(runningProcess.uid);
                break;
            }
        }
        //获取软件版本
        PackageManager packageManager = this.getApplicationContext().getPackageManager();
        PackageInfo packInfo = null;
        try {
            packInfo = packageManager.getPackageInfo(this.getApplicationContext().getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        AppVer = packInfo.versionName;
        //获取设备sn号
        GetDevicedUniqueId();
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

        mContext = getApplicationContext();

        if (Constants.Config.DEVELOPER_MODE && Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectAll().penaltyDialog().build());
            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectAll().penaltyDeath().build());
        }

     //   super.onCreate();

        initImageLoader(getApplicationContext());
    }

    public static void initImageLoader(Context context) {
        // This configuration tuning is custom. You can tune every option, you may tune some of them,
        // or you can create default configuration by
        //  ImageLoaderConfiguration.createDefault(this);
        // method.
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
                .threadPriority(Thread.NORM_PRIORITY - 1) //加载图片的线程数
                .denyCacheImageMultipleSizesInMemory() //解码图像的大尺寸将在内存中缓存先前解码图像的小尺寸
                .discCacheFileNameGenerator(new Md5FileNameGenerator()) //设置磁盘缓存文件名称
                .tasksProcessingOrder(QueueProcessingType.LIFO) //设置加载显示图片队列进程
                .memoryCache(new LruMemoryCache(2 * 1024 * 1024))
                .memoryCacheSize(2 * 1024 * 1024)//缓存到内存的最大数据
                .discCacheSize(50 * 1024 * 1024) //缓存到文件的最大数据
                .discCacheFileCount(100) //文件数量
                .writeDebugLogs() // Remove for release app
                .build();
        // Initialize ImageLoader with configuration.
        ImageLoader.getInstance().init(config);
    }


    public static Context getcontext(){
        return mContext;
    }
    private void InitLocation() {
        LogUtil.i("tong-----InitLocation");
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);// 设置定位模式
        option.setCoorType("bd09ll");// 返回的定位结果是百度经纬度,默认值gcj02
        // option.setScanSpan(5000);//设置发起定位请求的间隔时间为5000ms
        option.setIsNeedAddress(true);// 返回的定位结果包含地址信息
        // option.setNeedDeviceDirect(true);//返回的定位结果包含手机机头的方向
        mLocationClient.setLocOption(option);
    }

    /**
     * 实现实位回调监听
     */
    public class MyLocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            LogUtil.i("tong-------MyLocationListener");
            // Receive Location
            StringBuffer sb = new StringBuffer(256);
            sb.append("time : ");
            sb.append(location.getTime());
            sb.append("\nerror code : ");
            sb.append(location.getLocType());
            sb.append("\nlatitude : ");
            sb.append(location.getLatitude());
            sb.append("\nlontitude : ");
            sb.append(location.getLongitude());
            sb.append("\nradius : ");
            sb.append(location.getRadius());
            if (location.getLocType() == BDLocation.TypeGpsLocation) {
                LogUtil.i("tong-------TypeGpsLocation");
                sb.append("\nspeed : ");
                sb.append(location.getSpeed());
                sb.append("\nsatellite : ");
                sb.append(location.getSatelliteNumber());
                sb.append("\ndirection : ");
                sb.append("\naddr : ");
                sb.append(location.getAddrStr());
                sb.append(location.getDirection());
            } else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {
                LogUtil.i("tong-------TypeNetWorkLocation");
                sb.append("\naddr : ");
                sb.append(location.getAddrStr());
                // 运营商信息
                sb.append("\noperationers : ");
                sb.append(location.getOperators());
            }
            // setAddress(location.getAddrStr());
            if(location.getCity()!=null||location.getDistrict()!=null){
                LogUtil.i("tong--------location.getCity()!=nulllocation.getDistrict()!=null");
                setAddress(location.getCity() + location.getDistrict());
            }
            if(location.getAddrStr()!=null){
                setAddrDetail(location.getAddrStr());
            }

            /*********add by cxz*****************/
            setLongitude(location.getLongitude()+"");
            setLatitude(location.getLatitude()+"");
            setCountry(location.getCountry());
            setProvince(location.getProvince());
            setDistrict(location.getDistrict());
            /***********add End*************/
            setCity(location.getCity());
            if(location.getCity()!=null){
                //GetFirstWeather(location.getCity());
            }else{
                return;
            }

        }

    }

    public String getAddress() {
        return curAddress;
    }

    public void setAddress(String addr) {
        curAddress = addr;
    }
    public String getCity() {
        return curCity;
    }

    public void setCity(String City) {
        this.curCity = City;
    }
    public String getWeather() {
        return mWeather;
    }

    public void setWeather(String curWeather) {
        this.mWeather = curWeather;
    }

    public String getAddrDetail() {
        LogUtil.i("tong-----addrDetail:"+addrDetail);
        return addrDetail;
    }
    public void setAddrDetail(String addrDetail) {
        this.addrDetail = addrDetail;
    }

    /**********统计部分，增加国家**********************/
    public String getCountry() {
        LogUtil.i("tong-----addrDetail:"+curCountry);
        return curCountry;
    }
    public void setCountry(String country) {
        this.curCountry = country;
    }
    public String getProvice() {
        LogUtil.i("tong-----getProvice:"+curProvince);
        return curProvince;
    }
    public void setProvince(String province) {
        this.curProvince = province;
    }
    public String getDistrict() {
        LogUtil.i("tong-----District:"+curDistrict);
        return curDistrict;
    }
    public void setDistrict(String District) {
        this.curDistrict = District;
    }
    public String getLongitude() {
        LogUtil.i("tong-----Longitude:"+curLongitude);
        return curLongitude;
    }
    public void setLongitude(String Longitude) {
        this.curLongitude = Longitude;
    }
    public String getLatitude() {
        LogUtil.i("tong-----Latitude:"+curLatitude);
        return curLatitude;
    }
    public void setLatitude(String Latitude) {
        this.curLatitude = Latitude;
    }

    public void GetDevicedUniqueId(){
        TelephonyManager telephonyManager = (TelephonyManager)this.getSystemService( this.getApplicationContext().TELEPHONY_SERVICE);
        AppDeviceId =  telephonyManager.getSimSerialNumber();
        if (null == AppDeviceId){
            AppDeviceId = telephonyManager.getDeviceId();
        }else{
            return;
        }
        if(null == AppDeviceId){
            AppDeviceId = android.os.Build.SERIAL;
        }else{
            return;
        }

        if (null == AppDeviceId){
            AppDeviceId = android.provider.Settings.System.getString(getContentResolver(), android.provider.Settings.System.ANDROID_ID);
        }else{
            return;
        }

        if(null == AppDeviceId){
            AppDeviceId = "9774d56d682e549MobileOCR";
        }else{
            return;
        }

    }

}

