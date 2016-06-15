package com.hanvon.rc.activity;

import java.io.Serializable;

/**
 * Created by baiheng222 on 16-6-14.
 */
public class ResultFileInfo implements Serializable
{
    String fid;
    String fileNanme;
    String fileType;
    String fileAmount;
    String fileSize;
    String downloadFlag;
    String createTime;

    public String toString()
    {
        return "fid:" + fid + " fileName:" + fileNanme + " fileType:" + fileType +
                " fileAmount:" + fileAmount + " fileSize:" + fileSize + " downloadFlag:" +
                downloadFlag + " createTime:" + createTime;
    }

    public String getFid()
    {
        return fid;
    }

    public void setFid(String fid)
    {
        this.fid = fid;
    }

    public String getFileNanme()
    {
        return fileNanme;
    }

    public void setFileNanme(String fileNanme)
    {
        this.fileNanme = fileNanme;
    }

    public String getFileType()
    {
        return fileType;
    }

    public void setFileType(String fileType)
    {
        this.fileType = fileType;
    }

    public String getFileAmount()
    {
        return fileAmount;
    }

    public void setFileAmount(String fileAmount)
    {
        this.fileAmount = fileAmount;
    }

    public String getFileSize()
    {
        return fileSize;
    }

    public void setFileSize(String fileSize)
    {
        this.fileSize = fileSize;
    }

    public String getDownloadFlag()
    {
        return downloadFlag;
    }

    public void setDownloadFlag(String downloadFlag)
    {
        this.downloadFlag = downloadFlag;
    }

    public String getCreateTime()
    {
        return createTime;
    }

    public void setCreateTime(String createTime)
    {
        this.createTime = createTime;
    }


}
