package io.github.maksymilianrozanski.layout;

public class WidgetItem {
    private String stationName, nameAndValueOfParam, updateDate;
    private int stationId;

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

    String getNameAndValueOfParam() {
        return nameAndValueOfParam;
    }

    public void setNameAndValueOfParam(String nameAndValueOfParam) {
        this.nameAndValueOfParam = nameAndValueOfParam;
    }

    String getUpdateDate() {
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
}
