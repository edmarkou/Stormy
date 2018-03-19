package com.edmarkou.stormy.UI;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.edmarkou.stormy.R;
import com.edmarkou.stormy.Weathers.Day;
import com.edmarkou.stormy.adapters.DayAdapter;

import org.w3c.dom.Text;

import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DailyForecastActivity extends Activity {

    private Day mDays[];
    public static final String DAILY_DETAIL = "DAILY_DETAIL";
    private TextView timezone;

    @BindView(android.R.id.list) ListView mListView;
    @BindView(android.R.id.empty) TextView mEmpty;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_forecast);
        ButterKnife.bind(this);

        timezone = (TextView) findViewById(R.id.timezoneTextView);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        timezone.setText(intent.getStringExtra(MainActivity.DAILY_TIMEZONE));
        Parcelable[] parcelables = bundle.getParcelableArray(MainActivity.DAILY_FORECAST);
        mDays = Arrays.copyOf(parcelables, parcelables.length, Day[].class);

        DayAdapter dayAdapter = new DayAdapter(this, mDays);
        mListView.setAdapter(dayAdapter);
        mListView.setEmptyView(mEmpty);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Intent intent = new Intent(DailyForecastActivity.this, DailyDetailActivity.class);
                intent.putExtra(DAILY_DETAIL, mDays[i]);
                startActivity(intent);
            }
        });
    }


}
