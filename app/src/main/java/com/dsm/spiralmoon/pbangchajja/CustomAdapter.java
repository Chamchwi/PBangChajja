package com.dsm.spiralmoon.pbangchajja;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class CustomAdapter extends BaseAdapter {

    private Context context;
    private int icon[];
    private String category[];
    private LayoutInflater inflater;

    public CustomAdapter(Context context, int icon[], String[] category) {
        this.context = context;
        this.icon = icon;
        this.category = category;
        inflater = (LayoutInflater.from(context));
    }

    @Override
    public int getCount() {
        return icon.length;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        view = inflater.inflate(R.layout.custom_spinner, null);
        ImageView icon = (ImageView) view.findViewById(R.id.imageView);
        //TextView names = (TextView) view.findViewById(R.id.textView);

        icon.setImageResource(this.icon[i]);
        //names.setText(this.category[i]);

        return view;
    }
}