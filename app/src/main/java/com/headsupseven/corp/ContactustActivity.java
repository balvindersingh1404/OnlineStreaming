package com.headsupseven.corp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;

public class ContactustActivity extends AppCompatActivity {

    public Context mContext;
    private LinearLayout ll_silding;
    private LinearLayout new_message;
    private EditText subject,message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contactus);
        mContext = this;

        if (android.os.Build.VERSION.SDK_INT >= 21) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        }

        initUI();

    }

    private void initUI() {

        ll_silding = (LinearLayout) this.findViewById(R.id.ll_silding);
        ll_silding.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ContactustActivity.this.finish();
            }
        });
        subject=(EditText)this.findViewById(R.id.edt_subject);
        message=(EditText)this.findViewById(R.id.edt_message);
        new_message=(LinearLayout)this.findViewById(R.id.new_message);
        new_message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent emailIntent = new Intent(Intent.ACTION_SEND);
                emailIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                emailIntent.setType("vnd.android.cursor.item/email");
                emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[] {"info@headsup7.com"});
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject.getText().toString().trim());
                emailIntent.putExtra(Intent.EXTRA_TEXT, message.getText().toString().trim());
                startActivity(Intent.createChooser(emailIntent, "Send mail using..."));
            }
        });


    }


}
