package com.edmarkou.stormy.UI;

import android.content.Intent;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.edmarkou.stormy.R;
import com.edmarkou.stormy.Weathers.Day;

import java.util.Arrays;

public class DailyDetailActivity extends AppCompatActivity {

    private Day mDays;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_detail);

        Intent intent = getIntent();
        mDays =  intent.getParcelableExtra(DailyForecastActivity.DAILY_DETAIL);

        TextView dayOfTheWeek = (TextView) findViewById(R.id.dayOfTheWeekView);
        ImageView iconImageView = (ImageView) findViewById(R.id.iconImageView);
        TextView temperatureLabel = (TextView) findViewById(R.id.temperatureLabel);
        TextView summaryTextView = (TextView) findViewById(R.id.summaryTextView);
        TextView locationLabelView = (TextView) findViewById(R.id.LocationLabelView);


        dayOfTheWeek.setText(mDays.getDayOfTheWeek());
        iconImageView.setImageResource(mDays.getIconId());
        temperatureLabel.setText(mDays.getTemperature()+ "");
        summaryTextView.setText(mDays.getSummary());
        locationLabelView.setText(mDays.getTimezone());
    }
}
