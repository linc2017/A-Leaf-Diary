package com.rainstorm.aleaf.adapter;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.rainstorm.aleaf.R;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @description Main adapter
 * @author liys
 */
public class MainAdapter extends BaseAdapter {
    private LayoutInflater inflater = null;
    private List<Map<String, Object>> diaryData = new ArrayList<>();

    public MainAdapter(Context context, List<Map<String, Object>> diaryData) {
        this.inflater = LayoutInflater.from(context);
        this.diaryData = diaryData;
    }

    @Override
    public int getCount() {
        return diaryData.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.list_item, null);
            holder.title = convertView.findViewById(R.id.title);
            holder.text = convertView.findViewById(R.id.info);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        
        holder.title.setText((String) diaryData.get(position).get("title"));
        holder.text.setText(Html.fromHtml((String) diaryData.get(position).get("color_text")));

        return convertView;
    }

    private static class ViewHolder {
        public TextView title;
        public TextView text;
    }
}
