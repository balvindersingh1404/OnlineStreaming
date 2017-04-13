package com.headsupseven.corp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class RecoverpasswordActivity extends AppCompatActivity {

    private ImageView li_back;
    private EditText edit_validate;
    private EditText editi_newpassword;
    private EditText edit_confirmpassword;
    private LinearLayout Update;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recoverpassword);
        li_back=(ImageView)this.findViewById(R.id.li_back);
        li_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RecoverpasswordActivity.this.finish();
            }
        });
        edit_validate=(EditText)this.findViewById(R.id.edit_validate);
        editi_newpassword=(EditText)this.findViewById(R.id.editi_newpassword);
        edit_confirmpassword=(EditText)this.findViewById(R.id.edit_confirmpassword);
        Update=(LinearLayout) this.findViewById(R.id.Update);
        Update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

}
