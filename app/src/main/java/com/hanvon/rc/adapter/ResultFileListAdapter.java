package com.hanvon.rc.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.hanvon.rc.R;
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
    private List<FileInfo> mDatas;

    public ResultFileListAdapter(Context context, List<FileInfo> info)
    {
        mContext = context;
        mDatas = info;
        mInflater = LayoutInflater.from(context);
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
    public View getView(int position, View convertView, ViewGroup parent)
    {
        ViewHolder viewHolder = null;
        if (convertView == null)
        {

            convertView = mInflater.inflate(R.layout.file_list_item, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.mFileName = (TextView) convertView.findViewById(R.id.tv_filename);
            viewHolder.mFileSize = (TextView) convertView.findViewById(R.id.tv_filesize);
            convertView.setTag(viewHolder);

        }
        else
        {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        String filepath = mDatas.get(position).getResultPath();
        String filename = filepath.substring(filepath.lastIndexOf("/")+1, filepath.length());
        LogUtil.i("filename is " + filename);
        viewHolder.mFileName.setText(filename);
        //int notesNum = getNoteNumInNoteBook(position);
        int size = (mDatas.get(position).getResultSize() + 1023) /1024;
        LogUtil.i("file size is " + String.valueOf(size) + "k");
        viewHolder.mFileSize.setText(String.valueOf(size) + "k");

        return convertView;
    }

    private final class ViewHolder
    {
        TextView mFileName;
        TextView mFileSize;
    }


}
