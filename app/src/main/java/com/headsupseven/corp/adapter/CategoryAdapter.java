package com.headsupseven.corp.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.headsupseven.corp.R;
import com.headsupseven.corp.model.Categoryaddvideo;

import java.util.List;

/**
 * Created by Prosanto on 3/23/17.
 */


public class CategoryAdapter extends ArrayAdapter<Categoryaddvideo> {
    LayoutInflater flater;

    public CategoryAdapter(Activity context, int resouceId, int textviewId, List<Categoryaddvideo> list) {
        super(context, resouceId, textviewId, list);
        flater = context.getLayoutInflater();
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        return rowview(convertView, position);
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return rowview(convertView, position);
    }

    private View rowview(View convertView, int position) {
        Categoryaddvideo rowItem = getItem(position);
        viewHolder holder;
        View rowview = convertView;
        if (rowview == null) {
            holder = new viewHolder();
            flater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowview = flater.inflate(R.layout.spinner_item, null, false);
            holder.txtTitle = (TextView) rowview.findViewById(R.id.item_name);
            rowview.setTag(holder);
        } else {
            holder = (viewHolder) rowview.getTag();
        }
        holder.txtTitle.setText(rowItem.getName());
        return rowview;
    }

    private class viewHolder {
        TextView txtTitle;
    }
}
