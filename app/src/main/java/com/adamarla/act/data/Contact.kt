package com.adamarla.act.data

import android.os.Parcelable
import io.objectbox.annotation.Convert
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import io.objectbox.converter.PropertyConverter
import kotlinx.android.parcel.Parcelize
import java.text.SimpleDateFormat
import java.util.*


/**
 * Created by adamarla on 9/20/17.
 */

@Entity
@Parcelize
data class Contact(@Id var id: Long = 0,
                   var firstName: String = "", var lastName: String = "", var dob: Int = 0,
                   @Transient val contactDetails: MutableList<ContactDetail> =
                   mutableListOf(ContactDetail(), ContactDetail(detailType = DetailType.Phone))): Parcelable {

    val fullName get() = "$firstName $lastName"

    val addresses get() = contactDetails.filter { it.detailType == DetailType.Address }

    val phones get() = contactDetails.filter { it.detailType == DetailType.Phone }

    val emails get() = contactDetails.filter { it.detailType == DetailType.Email }

    val formattedDob: String get() {
        if (dob == 0) return ""
        val calendar = Calendar.getInstance()
        calendar.set((dob / 10000), ((dob % 10000) / 100) - 1, (dob % 10000) % 100)
        val format = SimpleDateFormat("EEEE, MMMM d, yyyy", Locale.US)
        return format.format(calendar.time)
    }

    val age: String get() {
        val calendar = Calendar.getInstance()
        val format = SimpleDateFormat("yyyyMMdd", Locale.US)
        val years = (format.format(calendar.time).toInt() - dob) / 10000
        return "$years"
    }

    val profileDetails get() = ContactDetail(0, id, DetailType.Profile,
            "$firstName, $lastName, $dob")

    val newContactDetails get() = ContactDetail(0, id, DetailType.NewContact,
            "$firstName, $lastName, $dob, ${contactDetails[0].data}, ${contactDetails[1].data}")

}

@Entity
@Parcelize
data class ContactDetail(@Id var id: Long = 0, var contactId: Long = 0,
                         @Convert(converter = ContactDetail.DetailConverter::class, dbType = Int::class)
                         val detailType: DetailType = DetailType.Email,
                         var data: String = ""): Parcelable {

    class DetailConverter: PropertyConverter<DetailType, Int> {
        override fun convertToEntityProperty(databaseValue: Int?): DetailType =
                DetailType.values().filter{ it.id == databaseValue }.first()

        override fun convertToDatabaseValue(entityProperty: DetailType?): Int =
            entityProperty?.id ?: 0
    }
}

enum class DetailType(val id: Int) {
    Address(0), Phone(1), Email(2), Profile(3), NewContact(4)
}
