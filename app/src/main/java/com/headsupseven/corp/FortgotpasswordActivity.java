package com.headsupseven.corp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

public class FortgotpasswordActivity extends AppCompatActivity {

    private ImageView li_back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgotpassword);
        li_back=(ImageView)this.findViewById(R.id.li_back);
        li_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FortgotpasswordActivity.this.finish();
            }
        });

    }

}
