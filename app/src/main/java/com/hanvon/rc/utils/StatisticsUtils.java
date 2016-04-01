package com.hanvon.rc.utils;

import com.hanvon.rc.application.HanvonApplication;

import org.json.JSONObject;

/**
 * @Desc:
 * @Auth: chenxzhuang
 * @Time: 2016/3/23 0023.
 */
public class StatisticsUtils {
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
}
