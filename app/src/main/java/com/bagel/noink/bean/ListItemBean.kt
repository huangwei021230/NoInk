package com.bagel.noink.bean

import android.net.Uri
import android.os.Parcel
import android.os.Parcelable
import java.util.Date

data class ListItemBean(
    val id: Int,
    val title: String,
    val text: String?,
    val coverUri: Uri,
    val moodTags: List<String>,
    val eventTag: String,
    val imagesUri: List<Uri>,
    val createDate: Date
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString() ?: "",
        parcel.readString(),
        parcel.readParcelable(Uri::class.java.classLoader) ?: Uri.EMPTY,
        parcel.createStringArrayList() ?: emptyList(), // Read moodTags as String List
        parcel.readString() ?:"",
        parcel.createTypedArrayList(Uri.CREATOR) ?: emptyList(),
        Date(parcel.readLong())
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(title)
        parcel.writeString(text)
        parcel.writeParcelable(coverUri, flags)
        parcel.writeStringList(moodTags) // Write moodTags as String List
        parcel.writeString(eventTag) // Write eventTags as String List
        parcel.writeTypedList(imagesUri)
        parcel.writeLong(createDate.time)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ListItemBean> {
        override fun createFromParcel(parcel: Parcel): ListItemBean {
            return ListItemBean(parcel)
        }

        override fun newArray(size: Int): Array<ListItemBean?> {
            return arrayOfNulls(size)
        }
    }
}
