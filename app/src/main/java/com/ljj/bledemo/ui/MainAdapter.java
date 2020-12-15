package com.ljj.bledemo.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ljj.bledemo.R;
import com.ljj.bledemo.bean.ScanDeviceBean;

import java.util.ArrayList;

public class MainAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<ScanDeviceBean> deviceList;

    public MainAdapter(Context context, ArrayList<ScanDeviceBean> deviceList) {
        this.deviceList = deviceList;
        this.mContext = context;
    }

    @Override
    public int getCount() {
        if (deviceList != null) {
            return deviceList.size();
        } else {
            return 0;
        }

    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    private class ViewHolder {
        TextView tv_name;
        TextView tv_uuid;
        TextView tv_type;
//        TextView tv_all;
        RelativeLayout ll_type;
        LinearLayout ll_all;
        TextView sCheckBox;
    }

    @Override
    public View getView(final int position, View rowView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view;
        ViewHolder viewHolder = null;
        if (rowView == null) {
            rowView = inflater.inflate(R.layout.add_device_item, null);
            viewHolder = new ViewHolder();

            viewHolder.ll_type = rowView.findViewById(R.id.ll_type);
            viewHolder.tv_type = rowView.findViewById(R.id.tv_type);
            viewHolder.ll_all = rowView.findViewById(R.id.ll_all);
            viewHolder.tv_name = rowView.findViewById(R.id.tv_name);
            viewHolder.tv_uuid = rowView.findViewById(R.id.tv_uuid);
            viewHolder.sCheckBox = rowView.findViewById(R.id.tv_choose);


            rowView.setTag(viewHolder);
        } else {
            view = rowView;
            viewHolder = (ViewHolder) view.getTag();
        }
        if (position == 0) {
            viewHolder.ll_type.setVisibility(View.VISIBLE);
        } else {
            if (deviceList.get(position - 1).getType() == deviceList.get(position).getType()) {
                viewHolder.ll_type.setVisibility(View.GONE);
            } else {
                viewHolder.ll_type.setVisibility(View.VISIBLE);
            }
        }
        viewHolder.tv_name.setText(deviceList.get(position).getName());
        viewHolder.tv_uuid.setText(deviceList.get(position).getMac());
        return rowView;
    }


}