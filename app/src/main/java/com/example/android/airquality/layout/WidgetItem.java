package com.example.android.airquality.layout;

/**
 * Created by Max on 20.10.2017.
 */

class WidgetItem {
    private String stationName,nameAndValueOfParam,updateDate;

    String getStationName() {
        return stationName;
    }

    void setStationName(String stationName) {
        this.stationName = stationName;
    }

    String getNameAndValueOfParam() {
        return nameAndValueOfParam;
    }

    void setNameAndValueOfParam(String nameAndValueOfParam) {
        this.nameAndValueOfParam = nameAndValueOfParam;
    }

    String getUpdateDate() {
        return updateDate;
    }

    void setUpdateDate(String updateDate) {
        this.updateDate = updateDate;
    }
}
