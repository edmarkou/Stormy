package com.edmarkou.stormy.UI;


import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.edmarkou.stormy.R;
import com.edmarkou.stormy.Weathers.CurrentWeather;
import com.edmarkou.stormy.Weathers.Day;
import com.edmarkou.stormy.Weathers.Forecast;
import com.edmarkou.stormy.Weathers.Hour;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity{

    public static final String TAG = MainActivity.class.getSimpleName();
    public static final String DAILY_FORECAST = "DAILY_FORECAST";
    public static final String HOURLY_FORECAST = "HOURLY_FORECAST";
    public static final String DAILY_TIMEZONE = "DAILY_TIMEZONE";
    private Forecast mForecast;


    private TextView mTemperatureLabel;
    private TextView mTimeLabel;
    private TextView mLocationLabel;
    private ImageView mIconImageView;
    private TextView mHumidityLabel;
    private TextView mPrecipLabel;
    private TextView mSummaryText;
    private ImageView mRefreshImageView;
    private ProgressBar mProgressBar;
    private LocationManager mLocationManager;
    private double latitude;
    private double longitude;
    private static final int REQUEST_LOCATION = 1;
    private String mTimezone;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);

        mTemperatureLabel = (TextView) findViewById(R.id.temperatureLabel);
        mTimeLabel = (TextView) findViewById(R.id.timeLabelView);
        mLocationLabel = (TextView) findViewById(R.id.locationLabelView);
        mIconImageView = (ImageView) findViewById(R.id.iconImageView);
        mHumidityLabel = (TextView) findViewById(R.id.humidityLabelView);
        mPrecipLabel = (TextView) findViewById(R.id.precipLabelView);
        mSummaryText = (TextView) findViewById(R.id.summaryTextView);
        mRefreshImageView = (ImageView) findViewById(R.id.refreshImageView);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);




        mProgressBar.setVisibility(View.INVISIBLE);

        checkLocation();

        mRefreshImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkLocation();
                getForecast(longitude, latitude);
            }
        });

        getForecast(longitude, latitude);
    }

    private void checkLocation() {
        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if(!mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            buildAlertMessageNoGps();
        } else if(mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            setLocation();
        }
    }


    private void getForecast(double longtitude, double latitude) {
        String apiKey = "ce88cec7105169e63a5148e92e14f555";
        String forecastUrl = "https://api.darksky.net/forecast/" + apiKey + "/" + latitude + "," + longtitude;

        if (isNetworkAvailable()) {

            refreshDisplay();

            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder().url(forecastUrl).build();
            Call call = client.newCall(request);

            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            refreshDisplay();
                        }
                    });
                    alertUserAboutError();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            refreshDisplay();
                        }
                    });
                    try {
                        String jsonData = response.body().string();
                        Log.v(TAG, jsonData);
                        if (response.isSuccessful()) {
                            mForecast = getForecast(jsonData);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    updateDisplay();
                                }
                            });
                        } else {
                            alertUserAboutError();
                        }
                    } catch (IOException e) {
                        Log.e(TAG, "Exception found:  ", e);
                    } catch (JSONException e) {
                        Log.e(TAG, "Exception found:  ", e);
                    }

                }
            });
        } else {
            Toast.makeText(this, R.string.networkUnavailableMessage, Toast.LENGTH_LONG).show();
        }
    }

    private void refreshDisplay() {
        if (mProgressBar.getVisibility() == View.VISIBLE) {
            mProgressBar.setVisibility(View.INVISIBLE);
            mRefreshImageView.setVisibility(View.VISIBLE);
        } else {
            mProgressBar.setVisibility(View.VISIBLE);
            mRefreshImageView.setVisibility(View.INVISIBLE);
        }
    }

    private void updateDisplay() {
        CurrentWeather mCurrentWeather = mForecast.getWeather();
        mTemperatureLabel.setText(mCurrentWeather.getTemperature() + "");
        mLocationLabel.setText(mCurrentWeather.getTimezone());
        mTimeLabel.setText("At " + mCurrentWeather.getFormattedTime() + " it will be");
        mSummaryText.setText(mCurrentWeather.getSummary());
        mPrecipLabel.setText(mCurrentWeather.getPercepChance() + "%");
        mHumidityLabel.setText(mCurrentWeather.getHumidity() + "");

        Drawable drawable = getResources().getDrawable(mCurrentWeather.getIconId());
        mIconImageView.setImageDrawable(drawable);
    }

    private Forecast getForecast(String jsonData) throws JSONException {
        Forecast forecast = new Forecast();

        forecast.setWeather(getDefaultData(jsonData));
        forecast.setHours(getHourlyData(jsonData));
        forecast.setDays(getDailyData(jsonData));

        return forecast;
    }

    private Day[] getDailyData(String jsonData) throws JSONException {
        JSONObject forecast = new JSONObject(jsonData);
        String timezone = forecast.getString("timezone");
        JSONObject daily = forecast.getJSONObject("daily");
        JSONArray data = daily.getJSONArray("data");

        Day days[] = new Day[data.length()];

        for (int i = 0; i < data.length(); i++) {
            JSONObject jsonDay = data.getJSONObject(i);
            Day day = new Day();

            day.setTimezone(timezone);
            day.setSummary(jsonDay.getString("summary"));
            day.setTime(jsonDay.getLong("time"));
            day.setIcon(jsonDay.getString("icon"));
            day.setTemperature(jsonDay.getDouble("temperatureMax"));

            days[i] = day;
        }
        return days;
    }

    private Hour[] getHourlyData(String jsonData) throws JSONException {
        JSONObject forecast = new JSONObject(jsonData);
        String timezone = forecast.getString("timezone");
        JSONObject hourly = forecast.getJSONObject("hourly");
        JSONArray data = hourly.getJSONArray("data");
        Hour hours[] = new Hour[data.length()];

        for (int i = 0; i < data.length(); i++) {
            JSONObject jsonHour = data.getJSONObject(i);
            Hour hour = new Hour();
            hour.setTime(jsonHour.getLong("time"));
            hour.setIcon(jsonHour.getString("icon"));
            hour.setSummary(jsonHour.getString("summary"));
            hour.setTemperature(jsonHour.getDouble("temperature"));
            hour.setTimezone(timezone);

            hours[i] = hour;
        }

        return hours;
    }


    private CurrentWeather getDefaultData(String jsonData) throws JSONException {
        JSONObject forecast = new JSONObject(jsonData);
        String timezone = forecast.getString("timezone");
        Log.i(TAG, "From JSON: " + timezone);

        JSONObject currently = forecast.getJSONObject("currently");
        CurrentWeather mCurrentWeather = new CurrentWeather();
        mCurrentWeather.setHumidity(currently.getDouble("humidity"));
        mCurrentWeather.setIcon(currently.getString("icon"));
        mCurrentWeather.setPercepChance(currently.getDouble("precipProbability"));
        mCurrentWeather.setSummary(currently.getString("summary"));
        mCurrentWeather.setTime(currently.getLong("time"));
        mCurrentWeather.setTemperature(currently.getInt("temperature"));
        mCurrentWeather.setTimezone(timezone);
        mTimezone = mCurrentWeather.getTimezone();

        Log.d(TAG, mCurrentWeather.getFormattedTime());

        return mCurrentWeather;
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        boolean isAvailable = false;
        if (networkInfo != null && networkInfo.isConnected()) {
            isAvailable = true;
        }
        return isAvailable;
    }

    private void alertUserAboutError() {
        AlertDialogFragment dialog = new AlertDialogFragment();
        dialog.show(getFragmentManager(), "error found");
    }

    @OnClick(R.id.dailyButton)
    public void startDailyForecast(View view) {
        Intent intent = new Intent(this, DailyForecastActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(DAILY_TIMEZONE, mTimezone);
        bundle.putParcelableArray(DAILY_FORECAST, mForecast.getDays());
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @OnClick(R.id.hourlyButton)
    public void startHourlyForecast(View view) {
        Intent intent = new Intent(this, HourlyForecastActivity.class);
        intent.putExtra(HOURLY_FORECAST, mForecast.getHours());
        startActivity(intent);
    }

    protected void buildAlertMessageNoGps(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Please turn ON your GPS connection").setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    public void setLocation(){
        if(ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){

            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}
            , REQUEST_LOCATION);

        }else{
            Location location = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

            Location location1 = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            Location location2 = mLocationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);

            if(location != null){

                latitude = location.getLatitude();
                longitude = location.getLongitude();

            }else if(location1 != null){

                latitude = location1.getLatitude();
                longitude = location1.getLongitude();

            }else if(location2 != null){

                latitude = location2.getLatitude();
                longitude = location2.getLongitude();

            }else{

                Toast.makeText(this, "Unable to trace your location", Toast.LENGTH_LONG).show();
            }
        }
    }
}
