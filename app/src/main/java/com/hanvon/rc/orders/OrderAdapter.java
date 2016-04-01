package com.hanvon.rc.orders;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.hanvon.rc.R;

import java.util.List;

/**
 * @Desc:
 * @Auth: chenxzhuang
 * @Time: 2016/3/29 0029.
 */
public class OrderAdapter extends BaseAdapter {

    private LayoutInflater mInflater;
    private Context mContext;
    private List<OrderDetail> mDatas;

    public OrderAdapter(Context Context,List<OrderDetail> List){
        this.mContext = Context;
        this.mDatas = List;
        mInflater = LayoutInflater.from(Context);
    }

    @Override
    public int getCount() {
        return mDatas.size();
    }

    @Override
    public Object getItem(int position) {
        return mDatas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null)
        {
            convertView = mInflater.inflate(R.layout.ordersimpleinfo, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.mOrderTime = (TextView) convertView.findViewById(R.id.ordersimpleinfo_time);
            viewHolder.mOrderStatus = (TextView) convertView.findViewById(R.id.ordersimpleinfo_status);
            viewHolder.mOrderTitle = (TextView) convertView.findViewById(R.id.ordersimpleinfo_title);
            viewHolder.mOrderPrice = (TextView) convertView.findViewById(R.id.ordersimpleinfo_price);
            convertView.setTag(viewHolder);
        }
        else
        {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.mOrderTime.setText(mDatas.get(position).getOrderCreateTime());
        viewHolder.mOrderStatus.setText(mDatas.get(position).getOrderStatus());
        viewHolder.mOrderTitle.setText(mDatas.get(position).getOrderTitle());
        viewHolder.mOrderPrice.setText(String.valueOf(mDatas.get(position).getOrderPrice()));


        return convertView;
    }
    private final class ViewHolder
    {
        TextView mOrderTime;
        TextView mOrderStatus;
        TextView mOrderTitle;
        TextView mOrderPrice;
    }
}
