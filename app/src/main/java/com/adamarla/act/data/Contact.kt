package com.adamarla.act.data

import android.os.Parcelable
import io.objectbox.annotation.Convert
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import io.objectbox.converter.PropertyConverter
import kotlinx.android.parcel.Parcelize



/**
 * Created by adamarla on 9/20/17.
 */

@Entity
@Parcelize
class Contact(@Id var id: Long = 0, var firstName: String, var lastName: String, var dob: Int,
              @Transient val contactDetails: MutableList<ContactDetail>): Parcelable {

    constructor(): this(0, "", "", 0, mutableListOf())

    @Transient
    val name = "$firstName $lastName"

    @Transient
    val formattedDob = "$dob"

    fun summary() = ContactDetail(0, id, DetailType.Summary,
            "$firstName, $lastName, $dob, ${contactDetails[0].data}, ${contactDetails[1].data}")

}

@Entity
@Parcelize
data class ContactDetail(@Id var id: Long = 0, var contactId: Long = 0,
                         @Convert(converter = ContactDetail.DetailConverter::class, dbType = Int::class)
                         val detailType: DetailType,
                         var data: String): Parcelable {

    class DetailConverter: PropertyConverter<DetailType, Int> {
        override fun convertToEntityProperty(databaseValue: Int?): DetailType =
                DetailType.values().filter{ it.id == databaseValue }.first()

        override fun convertToDatabaseValue(entityProperty: DetailType?): Int =
            entityProperty?.id ?: 0
    }
}

enum class DetailType(val id: Int) {
    Address(0), Phone(1), Email(2), Summary(3)
}
