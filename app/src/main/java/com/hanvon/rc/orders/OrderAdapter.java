package com.hanvon.rc.orders;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
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
            convertView = mInflater.inflate(R.layout.orderlistinfo, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.mOrderTime = (TextView) convertView.findViewById(R.id.orderlist_createtime);
            viewHolder.mOrderStatus = (TextView) convertView.findViewById(R.id.orderlist_status);
            viewHolder.IvOrderStatusMap = (ImageView) convertView.findViewById(R.id.orderlist_statutsmap);
            viewHolder.mOrderNumber = (TextView) convertView.findViewById(R.id.orderlist_ordernumber);
            viewHolder.mOrderPrice = (TextView) convertView.findViewById(R.id.orderlist_price);
            convertView.setTag(viewHolder);
        }
        else
        {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.mOrderTime.setText(mDatas.get(position).getOrderCreateTime());
        if("1".equals(mDatas.get(position).getOrderStatus())){
            viewHolder.IvOrderStatusMap.setImageResource(R.mipmap.wait_pay);
            viewHolder.mOrderStatus.setText("待支付");
        }else if("2".equals(mDatas.get(position).getOrderStatus())){
            viewHolder.IvOrderStatusMap.setImageResource(R.mipmap.working);
            viewHolder.mOrderStatus.setText("处理中");
        }else if("3".equals(mDatas.get(position).getOrderStatus())){
            viewHolder.IvOrderStatusMap.setImageResource(R.mipmap.working);
            viewHolder.mOrderStatus.setText("处理中");
        }else if("4".equals(mDatas.get(position).getOrderStatus())){
            viewHolder.IvOrderStatusMap.setImageResource(R.mipmap.canceling);
            viewHolder.mOrderStatus.setText("取消");
        }else if("5".equals(mDatas.get(position).getOrderStatus())){
            viewHolder.IvOrderStatusMap.setImageResource(R.mipmap.wait_downing);
            viewHolder.mOrderStatus.setText("待收取");
        }
        viewHolder.mOrderNumber.setText(mDatas.get(position).getOrderNumber());
        viewHolder.mOrderPrice.setText("¥"+String.valueOf(mDatas.get(position).getOrderPrice()));


        return convertView;
    }
    private final class ViewHolder
    {
        TextView mOrderTime;
        TextView mOrderStatus;
        TextView mOrderNumber;
        TextView mOrderPrice;
        ImageView IvOrderStatusMap;
    }


    /**
     * 添加列表项
     * @param item
     */
    public void addItem(OrderDetail item) {
        mDatas.add(item);
    }

    public void clearItem(){
        mDatas.clear();
    }
}
