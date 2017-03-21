package com.headsupseven.corp.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;

import com.headsupseven.corp.R;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by elanicdroid on 14/09/15.
 */
public class LoaderViewHolder extends RecyclerView.ViewHolder {

    @Bind(R.id.progressbar)
    ProgressBar mProgressBar;

    public LoaderViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }
}
