package io.github.maksymilianrozanski.widget

import android.os.Parcel
import android.os.Parcelable
import java.util.concurrent.atomic.AtomicBoolean

class WidgetItem() : Parcelable {
    var stationName: String? = null
    var nameAndValueOfParam: String? = null
    var updateDate: String? = null
    var stationId: Int = 0
    var isUpToDate = false
        private set

    constructor(parcel: Parcel) : this() {
        stationName = parcel.readString()
        nameAndValueOfParam = parcel.readString()
        updateDate = parcel.readString()
        stationId = parcel.readInt()
    }

    fun setUpToDate(upToDate: AtomicBoolean) {
        this.isUpToDate = upToDate.get()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(stationName)
        parcel.writeString(nameAndValueOfParam)
        parcel.writeString(updateDate)
        parcel.writeInt(stationId)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<WidgetItem> {
        override fun createFromParcel(parcel: Parcel): WidgetItem {
            return WidgetItem(parcel)
        }

        override fun newArray(size: Int): Array<WidgetItem?> {
            return arrayOfNulls(size)
        }
    }
}
