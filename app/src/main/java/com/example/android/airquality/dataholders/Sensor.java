package com.example.android.airquality.dataholders;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.example.android.airquality.main.MainActivity;
import com.example.android.airquality.vieweditors.SensorAdapter;

/**
 * Created by Max on 18.08.2017.
 */

public class Sensor implements Parcelable {

    private int id;
    private String param;
    private double value;
    private String lastDate;
    private static final String LOG_TAG = MainActivity.class.getName();

    public Sensor(int id, String param) {
        this.id = id;
        this.param = param;
    }

    public Sensor() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getParam() {
        return param;
    }

    public void setParam(String param) {
        this.param = param;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public String getLastDate() {
        return lastDate;
    }

    public void setLastDate(String lastDate) {
        this.lastDate = lastDate;
    }

    private Sensor(Parcel in) {
        String[] data = new String[4];
        in.readStringArray(data);
        this.id = Integer.parseInt(data[0]);
        this.param = data[1];
        this.value = Double.parseDouble(data[2]);
        this.lastDate = data[3];
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeStringArray(new String[]{
                Integer.toString(this.id),
                this.param,
                Double.toString(this.value),
                this.lastDate});
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        @Override
        public Object createFromParcel(Parcel parcel) {
            return new Sensor(parcel);
        }

        @Override
        public Object[] newArray(int size) {
            return new Sensor[size];
        }
    };

    public double percentOfMaxValue() {
        double percentValue;
        try {
            percentValue = (this.getValue() / SensorAdapter.getMaxConcentrations().get(this.getParam()) * 100);
        } catch (NumberFormatException e) {
            Log.e(LOG_TAG, "NumberFormatException " + e);
            throw e;
        }
        return percentValue;
    }
}
