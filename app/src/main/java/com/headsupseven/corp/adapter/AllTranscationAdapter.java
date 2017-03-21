package com.headsupseven.corp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.headsupseven.corp.R;
import com.headsupseven.corp.api.APIHandler;
import com.headsupseven.corp.model.TransactionList;

import java.util.Vector;

/**
 * Created by tmmac on 1/26/17.
 */


public class AllTranscationAdapter extends BaseAdapter {
    private LayoutInflater l_Inflater;
    Context mContext;
    private Vector<TransactionList> allTransactionLists = new Vector<>();

    public AllTranscationAdapter(Context context, Vector<TransactionList> allTransactionLists) {
        this.mContext = context;
        this.allTransactionLists = allTransactionLists;
        l_Inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void addTransaation(TransactionList mTransactionList) {

        // Please Update the code
        this.allTransactionLists.addElement(mTransactionList);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {

        // Please Update the code
        return allTransactionLists.size();
    }

    @Override
    public Object getItem(int position) {

        // Please Update tha code
        return allTransactionLists.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;

        if (convertView == null) {
            holder = new ViewHolder();
            convertView = l_Inflater.inflate(R.layout.row_transcation_list, parent, false);

            holder.amount = (TextView) convertView.findViewById(R.id.amount);
            holder.title = (TextView) convertView.findViewById(R.id.title);
            holder.date = (TextView) convertView.findViewById(R.id.date);

            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final TransactionList mTransactionList = allTransactionLists.elementAt(position);

        holder.date.setText(APIHandler.getTimeAgo(mTransactionList.getCreatedAt()));
        holder.amount.setText(mTransactionList.getAmount() + "$");
        holder.title.setText(mTransactionList.getTransactionDescription());

        return convertView;

    }

    static class ViewHolder {
        TextView title;
        TextView date;
        TextView amount;

    }


}


