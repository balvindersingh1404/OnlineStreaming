package com.headsupseven.corp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.headsupseven.corp.adapter.SliderAdapter;
import com.headsupseven.corp.pageIndicator.CirclePageIndicator;
import com.headsupseven.corp.utils.PersistentUser;

/**
 * Created by tmmac on 1/26/17.
 */

public class SliderActivity extends AppCompatActivity {
    private TextView start_text;
    public SliderAdapter mSliderAdapter;
    private Context mContext;
    private ViewPager pager;
    private CirclePageIndicator viewpager_indicator;
    private int index = 0;
    private TextView title_text;
    private TextView details_text;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slider);
        mContext = this;
        if (android.os.Build.VERSION.SDK_INT >= 21) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        }

        initUi();

    }

    private void initUi() {
        title_text = (TextView) this.findViewById(R.id.title_text);
        details_text = (TextView) this.findViewById(R.id.details_text);

        pager = (ViewPager) this.findViewById(R.id.pager);
        viewpager_indicator = (CirclePageIndicator) this.findViewById(R.id.viewpager_indicator);
        mSliderAdapter = new SliderAdapter(getSupportFragmentManager());
        pager.setAdapter(mSliderAdapter);
        viewpager_indicator.setViewPager(pager);
        start_text = (TextView) this.findViewById(R.id.start_text);
        start_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PersistentUser.setSlider(mContext);
                Intent mm = new Intent(SliderActivity.this, LoginActivity.class);
                startActivity(mm);
                SliderActivity.this.finish();
            }
        });
        viewpager_indicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                Log.w("onPageScrolled", "are" + position);


            }

            @Override
            public void onPageSelected(int position) {
                Log.w("onPageSelected", "are" + position);
                showTitleDetails(pager.getCurrentItem());
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                Log.w("212", "are" + state);

            }
        });
        pager.setCurrentItem(0);
        showTitleDetails(pager.getCurrentItem());

    }

    public void showTitleDetails(int position) {
        if (position == 0) {
            title_text.setText("News");
            details_text.setText("Enjoy engaging news from all over\nthe world in Virtual reality.");

        } else if (position == 1) {
            title_text.setText("Contests");
            details_text.setText("Participate in contests and\nwin mega prizes.");

        } else if (position == 2) {
            title_text.setText("Dating");
            details_text.setText("Meet someone special, date virtually\nbefore dating physically.");

        } else if (position == 3) {
            title_text.setText("And");
            details_text.setText("Many more features.");


        }

    }

}
