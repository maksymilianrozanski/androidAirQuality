package io.github.maksymilianrozanski.widget

import android.os.Parcel
import android.os.Parcelable
import java.util.concurrent.atomic.AtomicBoolean

class WidgetItem() : Parcelable {
    var stationName: String? = null
    var nameAndValueOfParam: String? = null
    var updateDate: String? = null
    var stationId: Int = 0
    var isUpToDate: Boolean = false

    constructor(parcel: Parcel) : this() {
        stationName = parcel.readString()
        nameAndValueOfParam = parcel.readString()
        updateDate = parcel.readString()
        stationId = parcel.readInt()
        isUpToDate = parcel.readInt() == 1
    }

    fun setUpToDate(upToDate: AtomicBoolean) {
        this.isUpToDate = upToDate.get()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(stationName)
        parcel.writeString(nameAndValueOfParam)
        parcel.writeString(updateDate)
        parcel.writeInt(stationId)
        parcel.writeInt((if (isUpToDate) 1 else 0))
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

    override fun equals(other: Any?): Boolean {
        return other is WidgetItem && other.stationName == this.stationName
                && other.nameAndValueOfParam == this.nameAndValueOfParam
                && other.updateDate == this.updateDate
                && other.stationId == this.stationId
                && other.isUpToDate == this.isUpToDate

    }

    override fun hashCode(): Int {
        return super.hashCode()
    }
}
