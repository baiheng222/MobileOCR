package com.hanvon.rc.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hanvon.rc.R;
import com.hanvon.rc.activity.FileListActivity;
import com.hanvon.rc.activity.ResultFileInfo;
import com.hanvon.rc.db.FileInfo;
import com.hanvon.rc.utils.LogUtil;

import java.util.List;

/**
 * Created by baiheng222 on 16-4-27.
 */
public class ResultFileListAdapter extends BaseAdapter
{
    private LayoutInflater mInflater;
    private Context mContext;
    private List<ResultFileInfo> mDatas;

    private static int EDIT_MODE = 2;
    private static int VIEW_MODE = 1;

    private int mShowMode;

    public ResultFileListAdapter(Context context, List<ResultFileInfo> info, int showmode)
    {
        mContext = context;
        mDatas = info;
        mInflater = LayoutInflater.from(context);
        mShowMode = showmode;
    }

    public void setmShowMode(int mode)
    {
        mShowMode = mode;
    }

    @Override
    public int getCount()
    {
        return mDatas.size();
    }

    @Override
    public Object getItem(int position)
    {
        return mDatas.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent)
    {
        ViewHolder viewHolder = null;
        if (convertView == null)
        {
            convertView = mInflater.inflate(R.layout.file_list_item2, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.mImageSelection = (CheckBox) convertView.findViewById(R.id.iv_select);
            viewHolder.mImageSelection.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
            {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
                {
                    LogUtil.i("itme " + position + " state is " + isChecked);
                }
            });
            viewHolder.mRlFileItem = (RelativeLayout) convertView.findViewById(R.id.rl_fileitem);
            viewHolder.mRlFileItem.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    FileListActivity act = (FileListActivity)mContext;
                    //act.startRecsultActivity(position);
                }
            });
            viewHolder.mImageFormat = (ImageView) convertView.findViewById(R.id.iv_format_img);
            viewHolder.mTvFileName = (TextView) convertView.findViewById(R.id.tv_file_name);
            viewHolder.mTvFileType = (TextView) convertView.findViewById(R.id.tv_filetype);
            viewHolder.mTvFileSize = (TextView) convertView.findViewById(R.id.tv_filesize);
            viewHolder.mTvFileCreateTime = (TextView) convertView.findViewById(R.id.tv_creattime);
            viewHolder.mIvDownLoad = (ImageView) convertView.findViewById(R.id.iv_download);
            viewHolder.mIvDownLoad.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    LogUtil.i("!!! download img click, position is " + position);
                    String downloadFlag = mDatas.get(position).getDownloadFlag();
                    LogUtil.i("item " + position + " download flag is  " + downloadFlag);

                }

            }
            );
            convertView.setTag(viewHolder);

        }
        else
        {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if (mShowMode == VIEW_MODE)
        {
            viewHolder.mImageSelection.setVisibility(View.GONE);
        }
        else
        {
            viewHolder.mImageSelection.setVisibility(View.VISIBLE);
        }

        viewHolder.mImageFormat.setImageResource(getFileTypeIcon(mDatas.get(position).getFileType()));

        /*
        String filepath = mDatas.get(position).getResultPath();
        String filename = filepath.substring(filepath.lastIndexOf("/")+1, filepath.length());
        */
        LogUtil.i("filename is " + mDatas.get(position).getFileNanme());
        viewHolder.mTvFileName.setText(mDatas.get(position).getFileNanme());

        viewHolder.mTvFileType.setText(mDatas.get(position).getFileType().toUpperCase());

        //int notesNum = getNoteNumInNoteBook(position);
        int size = (Integer.parseInt(mDatas.get(position).getFileSize()) + 1023) /1024;
        LogUtil.i("file size is " + String.valueOf(size) + "k");
        viewHolder.mTvFileSize.setText(String.valueOf(size) + "k");

        //viewHolder.mTvFileCreateTime.setText(mDatas.get(position).getResultFileCreateTime());
        viewHolder.mTvFileCreateTime.setText(mDatas.get(position).getCreateTime());

        String downloadFlag = mDatas.get(position).getDownloadFlag();
        if (downloadFlag.equals("1"))
        {
            viewHolder.mIvDownLoad.setVisibility(View.GONE);
        }
        else
        {
            viewHolder.mIvDownLoad.setVisibility(View.VISIBLE);
        }

        return convertView;
    }

    private int getFileTypeIcon(String type)
    {
        if (null == type)
        {
            return R.mipmap.format_txt;
        }

        if (type.toLowerCase().equals("doc"))
        {
            return R.mipmap.format_doc;
        }
        else if (type.toLowerCase().equals("pdf"))
        {
            return R.mipmap.format_pdf;
        }
        else
        {
            return R.mipmap.format_txt;
        }

    }

    private final class ViewHolder
    {
        CheckBox mImageSelection;
        ImageView mImageFormat;
        TextView  mTvFileName;
        TextView  mTvFileType;
        TextView  mTvFileSize;
        TextView  mTvFileCreateTime;
        ImageView mIvDownLoad;
        RelativeLayout mRlFileItem;
    }


}
