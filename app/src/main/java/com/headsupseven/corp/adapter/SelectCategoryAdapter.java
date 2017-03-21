package com.headsupseven.corp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.headsupseven.corp.model.CategoryList;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import com.headsupseven.corp.R;

/**
 * Created by tmmac on 1/26/17.
 */


public class SelectCategoryAdapter extends BaseAdapter {
    private LayoutInflater l_Inflater;
    Context mContext;
    ArrayList<CategoryList> arrayList;
    ArrayList<CategoryList> selectearrayList = new ArrayList<CategoryList>();

    public SelectCategoryAdapter(Context context, ArrayList<CategoryList> arrayList) {
        this.mContext = context;
        this.arrayList = new ArrayList<CategoryList>();
        this.arrayList = arrayList;
        this.selectearrayList.clear();
        l_Inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return arrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return arrayList.get(position);
    }

    public void addnewObject(CategoryList mCategoryList) {
        arrayList.add(mCategoryList);
        notifyDataSetChanged();
    }
    public ArrayList<CategoryList> getselectedObject() {
       return selectearrayList;

    }
    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView,
                        ViewGroup parent) {
        final ViewHolder holder;

        if (convertView == null) {
            holder = new ViewHolder();
            convertView = l_Inflater.inflate(R.layout.row_select_category,
                    parent, false);

            holder.categoryimage = (ImageView) convertView
                    .findViewById(R.id.categoryImage);
            holder.categorySelected = (ImageView) convertView
                    .findViewById(R.id.icon_selected);
            holder.categoryTitle = (TextView) convertView
                    .findViewById(R.id.category_text);
            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        convertView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (arrayList.get(position).isSelected()) {
                    arrayList.get(position).setSelected(false);
                    selectearrayList.remove(arrayList.get(position));
                    notifyDataSetChanged();

                } else {
                    arrayList.get(position).setSelected(true);
                    selectearrayList.add(arrayList.get(position));
                    notifyDataSetChanged();
                }

            }
        });
        holder.categoryTitle.setText(arrayList.get(position).getName());
        Picasso.with(mContext)
                .load(arrayList.get(position).getThumbnailUrl())
                .into(holder.categoryimage);
        if(arrayList.get(position).isSelected())
            holder.categorySelected.setVisibility(View.VISIBLE);
        else
            holder.categorySelected.setVisibility(View.INVISIBLE);

        return convertView;

    }

    static class ViewHolder {
        ImageView categoryimage, categorySelected;
        TextView categoryTitle;

    }


}


