package com.headsupseven.corp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class FortgotpasswordActivity extends AppCompatActivity {

    private ImageView li_back;
    private LinearLayout Already;
    private Context mContext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgotpassword);
        mContext=this;
        li_back=(ImageView)this.findViewById(R.id.li_back);
        li_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FortgotpasswordActivity.this.finish();
            }
        });

        Already=(LinearLayout)this.findViewById(R.id.Already);
        Already.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mm = new Intent(mContext, RecoverpasswordActivity.class);
                startActivity(mm);
            }
        });
    }

}
