package com.hanvon.rc.md.camera.activity;

import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.util.Log;

import com.hanvon.rc.utils.LogUtil;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by baiheng222 on 16-7-5.
 */
public class CameraPicturSize
{
    private CameraSizeComparator sizeComparator = new CameraSizeComparator();
    private static CameraPicturSize myCamPara = null;
    private CameraPicturSize()
    {

    }

    public static CameraPicturSize getInstance()
    {
        if(myCamPara == null)
        {
            myCamPara = new CameraPicturSize();
            return myCamPara;
        }
        else
        {
            return myCamPara;
        }
    }

    public  Size getPreviewSize(List<Camera.Size> list, int th)
    {
        Collections.sort(list, sizeComparator);

        int i = 0;
        for(Size s:list)
        {
            if((s.width > th) && equalRate(s, 1.33f))
            {
                LogUtil.i( "最终设置预览尺寸:w = " + s.width + "h = " + s.height);
                break;
            }
            i++;
        }

        if (i < list.size())
        {
            return list.get(i);
        }
        else
        {
            return null;
        }
    }

    public Size getPictureSize(List<Camera.Size> list, int th)
    {
        Collections.sort(list, sizeComparator);

        int i = 0;
        for(Size s:list)
        {
            if((s.width > th) && equalRate(s, 1.33f))
            {
                LogUtil.i( "最终设置图片尺寸:w = " + s.width + "h = " + s.height);
                break;
            }
            i++;
        }

        if (i < list.size())
        {
            return list.get(i);
        }
        else
        {
            return null;
        }
    }


    /*通过equalRate(Size s, float rate)保证Size的长宽比率。
      一般而言这个比率为1.333/1.7777即通常说的4:3和16:9比率。
    */
    public boolean equalRate(Size s, float rate)
    {
        float r = (float)(s.width)/(float)(s.height);
        if(Math.abs(r - rate) <= 0.2)
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    public  class CameraSizeComparator implements Comparator<Camera.Size>
    {
        //按升序排列  
        public int compare(Size lhs, Size rhs)
        {
            if(lhs.width == rhs.width)
            {
                return 0;
            }
            else if(lhs.width > rhs.width)
            {
                return 1;
            }
            else
            {
                return -1;
            }
        }

    }
}
