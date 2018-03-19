package com.edmarkou.stormy.Weathers;

import com.edmarkou.stormy.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class CurrentWeather {

    private String mIcon;
    private long mTime;
    private String mSummary;
    private double mHumidity;
    private double mPercepChance;
    private double mTemperature;
    private String timezone;

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public String getIcon() {
        return mIcon;
    }

    public void setIcon(String icon) {
        mIcon = icon;
    }

    public long getTime() {
        return mTime;
    }

    public void setTime(long time) {
        mTime = time;
    }

    public String getSummary() {
        return mSummary;
    }

    public void setSummary(String summary) {
        mSummary = summary;
    }

    public double getHumidity() {
        return mHumidity;
    }

    public void setHumidity(double humidity) {
        mHumidity = humidity;
    }

    public int getPercepChance() {
        double percepChance = mPercepChance * 100;
        return (int) Math.round(percepChance);
    }

    public void setPercepChance(double percepChance) {
        mPercepChance = percepChance;
    }

    public int getTemperature() {
        return (int)Math.round((mTemperature - 32) / 1.8);
    }

    public void setTemperature(double temperature) {
        mTemperature = temperature;
    }

    public String getFormattedTime(){
        SimpleDateFormat formatter = new SimpleDateFormat("h:mm a");
        formatter.setTimeZone(TimeZone.getTimeZone(getTimezone()));
        Date dateTime = new Date(getTime() * 1000);
        return formatter.format(dateTime);
    }

    public int getIconId(){
        return Forecast.getIconId(mIcon);
    }
}
