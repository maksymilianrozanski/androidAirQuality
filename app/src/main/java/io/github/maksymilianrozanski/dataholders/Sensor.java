package io.github.maksymilianrozanski.dataholders;

import android.os.Parcel;
import android.os.Parcelable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import io.github.maksymilianrozanski.main.MainActivity;
import io.github.maksymilianrozanski.vieweditors.SensorAdapter;

public class Sensor implements Parcelable {

    public static String DEFAULT_DATE = "2018-01-01 00:00:00";

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
        } catch (NumberFormatException | NullPointerException e) {
            return -1;
        }
        return percentValue;
    }

    public long getTimeInMillis() throws ParseException {
        String pattern = "yyyy-MM-dd HH:mm:ss";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern, Locale.ENGLISH);
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        Date date;
        date = simpleDateFormat.parse(this.getLastDate());
        return date.getTime();
    }

    public boolean isDateDefault() {
        return (lastDate.equals(DEFAULT_DATE));
    }
}
