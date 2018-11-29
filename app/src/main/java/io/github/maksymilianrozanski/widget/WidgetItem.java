package io.github.maksymilianrozanski.widget;

import java.util.concurrent.atomic.AtomicBoolean;

public class WidgetItem {
    private String stationName, nameAndValueOfParam, updateDate;
    private int stationId;
    private boolean isUpToDate = false;

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

    public int getStationId() {
        return stationId;
    }

    public void setStationId(int stationId) {
        this.stationId = stationId;
    }

    public boolean isUpToDate() {
        return isUpToDate;
    }

    public void setUpToDate(AtomicBoolean upToDate) {
        this.isUpToDate = upToDate.get();
    }
}
