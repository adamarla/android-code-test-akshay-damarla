package data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * Created by adamarla on 9/20/17.
 */

@Parcelize
data class Contact(val firstName: String, val lastName: String, val dob: Int,
                   val addresses: List<Address>, val phones: List<String>,
                   val emails: List<String>): Parcelable

@Parcelize
data class Address(val line: String, val city: String, val state: String, val zip: String): Parcelable