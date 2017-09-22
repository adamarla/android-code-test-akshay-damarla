package com.adamarla.act.activity

import android.content.Context
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import com.adamarla.act.R
import data.Contact

class ManageContact : AppCompatActivity() {

    lateinit var contact: Contact

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage_contact)

        contact = this.intent.getParcelableExtra("contact")

        val tvHeadLine = findViewById(R.id.tvHeadline) as TextView
        tvHeadLine.text = "${contact.firstName} ${contact.firstName}"
        val tvBlurb = findViewById(R.id.tvBlurb) as TextView
        tvBlurb.text = "Born: ${contact.dob}"
        val contactDetails = contact.addresses.map { Pair(ContactType.address, "${it.line}, ${it.city}, ${it.state}") } +
                contact.emails.map { Pair(ContactType.email, it)} + contact.phones.map { Pair(ContactType.phone, it)}

        val lv = findViewById(R.id.lvContactInfo) as ListView
        lv.adapter = ContactDetailAdapter(this, contactDetails.toTypedArray())
    }
}

class ContactDetailAdapter(val ctx: Context, val contactDetails: Array<Pair<ContactType, String>>):
        ArrayAdapter<Pair<ContactType, String>>(ctx, R.layout.detail_card, contactDetails) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val detail = contactDetails[position]
        var view = convertView
        if (convertView == null) {
            val inflater = LayoutInflater.from(ctx)
            view = inflater.inflate(R.layout.detail_card, parent, false)
        }
        val tvDetail = view!!.findViewById(R.id.tvDetail) as TextView
        tvDetail.text = detail.second
        return view
    }

    override fun getCount() = contactDetails.size

    override fun getItem(position: Int) = contactDetails[position]
}

enum class ContactType {
    address, phone, email
}
