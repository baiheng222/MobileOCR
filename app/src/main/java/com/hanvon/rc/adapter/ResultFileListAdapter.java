package com.hanvon.rc.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.hanvon.rc.R;
import com.hanvon.rc.db.FileInfo;

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
            /*
            convertView = mInflater.inflate(R.layout.notebook_list_item, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.mNoteBookName = (TextView) convertView.findViewById(R.id.tv_notebook_name);
            viewHolder.mNotesNum = (TextView) convertView.findViewById(R.id.tv_notes_in_notebook);
            convertView.setTag(viewHolder);
            */
        }
        else
        {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        /*
        viewHolder.mNoteBookName.setText(mDatas.get(position).getNoteBookName());
        int notesNum = getNoteNumInNoteBook(position);
        viewHolder.mNotesNum.setText(String.valueOf(notesNum));
        */
        return convertView;
    }

    private final class ViewHolder
    {
        TextView mNoteBookName;
        TextView mNotesNum;
    }


}
