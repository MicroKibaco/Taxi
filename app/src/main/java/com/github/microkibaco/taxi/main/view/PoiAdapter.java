package com.github.microkibaco.taxi.main.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatTextView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.github.microkibaco.taxi.R;

import java.util.List;

public class PoiAdapter extends ArrayAdapter {
    private final List<String> mData;
    private final LayoutInflater mInflater;
    private OnItemClickListener mOnItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }

    public PoiAdapter(Context context, List<String> data) {
        super(context, R.layout.poi_list_item);
        this.mData = data;
        this.mInflater = (LayoutInflater)
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    interface OnItemClickListener {
        void onItemClick(int id);
    }


    public void setData(List<String> data) {
        this.mData.clear();
        this.mData.addAll(data);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        if (mData == null) {
            return 0;
        }
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @SuppressLint("InflateParams")
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Holder holder = null;

        if (convertView == null) {

            convertView = mInflater.inflate(R.layout.poi_list_item, null);
            holder = new Holder();
            holder.textView = (AppCompatTextView) convertView.findViewById(R.id.name);
            convertView.setTag(holder);
        } else {

            final Object tag = convertView.getTag();

            if (null == tag) {

                holder = new Holder();
                holder.textView = (AppCompatTextView) convertView.findViewById(R.id.name);
                convertView.setTag(holder);

            } else {

                holder = (Holder) tag;

            }

            holder.id = position;
            holder.textView.setText(mData.get(position));

        }
        return convertView;
    }

    private class Holder {
        int id;
        AppCompatTextView textView;
    }
}
