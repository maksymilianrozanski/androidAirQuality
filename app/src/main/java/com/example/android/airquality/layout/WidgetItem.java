package com.example.android.airquality.layout;

/**
 * Created by Max on 20.10.2017.
 */

public class WidgetItem {
    private String stationName,nameAndValueOfParam,updateDate;

    public WidgetItem() {
    }

    public WidgetItem(String stationName, String nameAndValueOfParam, String updateDate) {
        this.stationName = stationName;
        this.nameAndValueOfParam = nameAndValueOfParam;
        this.updateDate = updateDate;
    }

    public String getStationName() {
        return stationName;
    }

    public void setStationName(String stationName) {
        this.stationName = stationName;
    }

    public String getNameAndValueOfParam() {
        return nameAndValueOfParam;
    }

    public void setNameAndValueOfParam(String nameAndValueOfParam) {
        this.nameAndValueOfParam = nameAndValueOfParam;
    }

    public String getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(String updateDate) {
        this.updateDate = updateDate;
    }
}
