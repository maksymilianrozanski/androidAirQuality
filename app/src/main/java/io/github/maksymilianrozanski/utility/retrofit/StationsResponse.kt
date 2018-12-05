package io.github.maksymilianrozanski.utility.retrofit

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import io.github.maksymilianrozanski.dataholders.Station

class StationsResponse(id: String?,
                       stationName: String?,
                       gegrLat: String?,
                       gegrLon: String?,
                       var city: City?) {

    @field:SerializedName("id")
    @field:Expose
    var id = id
        get() = field ?: ""

    @field:SerializedName("stationName")
    @field:Expose
    var stationName = stationName
        get() = field ?: ""

    @field:SerializedName("gegrLat")
    @field:Expose
    var gegrLat = gegrLat
        get() = field ?: ""

    @field:SerializedName("gegrLon")
    @field:Expose
    var gegrLon = gegrLon
        get() = field ?: ""

    class City(id: String?, name: String?) {

        @field:SerializedName("id")
        @field:Expose
        var id = id
            get() = field ?: ""


        @field:SerializedName("name")
        @field:Expose
        var name = name
            get() = field ?: ""
    }

    fun getStationObject(): Station {
        return Station(this.id, this.stationName, this.gegrLat, this.gegrLon, this.city?.id, this.city?.name)
    }
}