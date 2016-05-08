package com.hanvon.rc.adapter;

import android.content.Context;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.hanvon.rc.R;

import java.util.List;
import java.util.zip.Inflater;

/**
 * Created by baiheng222 on 16-5-5.
 */
public class FileFormatAdapter extends BaseAdapter
{
    private Context mContext;
    private LayoutInflater mInflater;
    private List<String> mDatas;

    public FileFormatAdapter(Context context, List<String> data)
    {
        mContext = context;
        mInflater = LayoutInflater.from(context);
        mDatas = data;
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
        if (null == convertView)
        {
            convertView = mInflater.inflate(R.layout.file_format_item, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.mFileFormat = (TextView)convertView.findViewById(R.id.tv_format);
            viewHolder.mImage = (ImageView)convertView.findViewById(R.id.iv_img);
            convertView.setTag(viewHolder);
        }
        else
        {
            viewHolder = (ViewHolder)convertView.getTag();
        }

        viewHolder.mFileFormat.setText(mDatas.get(position));
        //viewHolder.mImage.setImageResource(R.mipmap.);

        return convertView;
    }

    private final class ViewHolder
    {
        TextView mFileFormat;
        ImageView mImage;
    }

}
