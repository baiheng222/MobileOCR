package com.hanvon.rc.db;

import java.io.Serializable;

/**
 * Created by baiheng222 on 16-4-27.
 */
public class FileInfo implements Serializable
{
    private String userID;
    private String originPath;
    private String resultPath;
    private String resultType;
    private String resultFUID;
    private int  resultSize;

    public String getUserID()
    {
        return userID;
    }

    public void setUserID(String userID)
    {
        this.userID = userID;
    }

    public String getOriginPath()
    {
        return originPath;
    }

    public void setOriginPath(String originPath)
    {
        this.originPath = originPath;
    }

    public String getResultPath()
    {
        return resultPath;
    }

    public void setResultPath(String resultPath)
    {
        this.resultPath = resultPath;
    }

    public String getResultType()
    {
        return resultType;
    }

    public void setResultType(String resultType)
    {
        this.resultType = resultType;
    }

    public String getResultFUID()
    {
        return resultFUID;
    }

    public void setResultFUID(String resultFUID)
    {
        this.resultFUID = resultFUID;
    }

    public int getResultSize()
    {
        return resultSize;
    }

    public void setResultSize(int resultSize)
    {
        this.resultSize = resultSize;
    }

    public String toSting()
    {
        return "user_id: " + userID + " origin_path: " + originPath + " ret_path: " + resultPath +
            " ret_type: " + resultType + " ret_id: " + resultFUID + " ret_size: " + resultSize;
    }



}
