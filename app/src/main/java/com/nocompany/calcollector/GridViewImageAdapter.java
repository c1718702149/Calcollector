package com.nocompany.calcollector;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import java.util.ArrayList;

public class GridViewImageAdapter extends BaseAdapter {

    private Context context;
    private Integer[] gridview_images = {
            R.drawable.dashboard_3_daily,
            R.drawable.dashboard_4_weekly,
            R.drawable.dashboard_5_monthly,
            R.drawable.dashboard_6_spend,
            R.drawable.dashboard_7_baddebt,
            R.drawable.dashboard_8_blacklist};

    public GridViewImageAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return gridview_images.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {
            imageView = new ImageView(context);
            imageView.setLayoutParams(new GridView.LayoutParams(250, 250));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(8, 8, 8, 8);
            imageView.setBackgroundResource(R.color.button_background);
        } else {
            imageView = (ImageView) convertView;
        }

        imageView.setImageResource(gridview_images[position]);
        return imageView;
    }
}
