package com.adamarla.act.activity

import android.content.Context
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import com.adamarla.act.ACTAD
import com.adamarla.act.R
import com.adamarla.act.data.Contact
import com.adamarla.act.data.ContactDetail
import com.adamarla.act.gui.EditItemDialog
import io.objectbox.Box

class ManageContact : AppCompatActivity(), EditItemDialog.EditDialogHost {

    private var itemPosition = NEW_CONTACT_POSN
    lateinit var contact: Contact

    lateinit var contactsBox: Box<Contact>
    lateinit var detailsBox: Box<ContactDetail>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage_contact)

        contactsBox = (application as ACTAD).boxStore.boxFor(Contact::class.java)
        detailsBox = (application as ACTAD).boxStore.boxFor(ContactDetail::class.java)

        contact = intent.getParcelableExtra("contact")

        val rlSummary = findViewById(R.id.rlSummary)
        rlSummary!!.setOnLongClickListener { _ ->
            itemPosition = NEW_CONTACT_POSN
            val summary = contact.summary()
            EditItemDialog(summary, this).show(fragmentManager, "dialog")
            true
        }

        val tvHeadLine = rlSummary.findViewById(R.id.tvHeadline) as TextView
        tvHeadLine.text = contact.name
        val tvBlurb = findViewById(R.id.tvBlurb) as TextView
        tvBlurb.text = contact.formattedDob

        val lv = findViewById(R.id.lvContactInfo) as ListView
        lv.adapter = ContactDetailAdapter(this, contact.contactDetails.toTypedArray())
        lv.onItemLongClickListener = AdapterView.OnItemLongClickListener { _, _, position, _ ->
            itemPosition = position
            EditItemDialog(contact.contactDetails[position], this).show(fragmentManager, "dialog")
            true
        }

        if (contact.id == 0L)
            rlSummary.performLongClick()
    }

    override fun onSave(data: String) {
        Log.d("adamarla", "onSave -> $data")
        if (itemPosition == NEW_CONTACT_POSN) {
            val tokens = data.split(",").map{ it.trim() }
            contact.firstName = tokens[0]
            contact.lastName = tokens[1]
            contact.dob = tokens[2].toInt()
            contactsBox.put(contact)

            listOf(tokens[3], tokens[4]).forEachIndexed { idx, element ->
                val details = contact.contactDetails[idx]
                details.data = element
                details.contactId = contact.id
                detailsBox.put(details)
            }

        } else {
            contact.contactDetails[itemPosition].data = data
            detailsBox.put(contact.contactDetails[itemPosition])
        }
    }

    override fun onDelete() {
        Log.d("adamarla", "onDelete ->")
        if (itemPosition != NEW_CONTACT_POSN) {
            detailsBox.remove(contact.contactDetails[itemPosition].id)
            contact.contactDetails.removeAt(itemPosition)
        } else {

        }
    }

    companion object {
        val NEW_CONTACT_POSN = -1
    }
}

class ContactDetailAdapter(private val ctx: Context,
                           private val details: Array<ContactDetail>):
        ArrayAdapter<ContactDetail>(ctx, R.layout.detail_card, details) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val detail = details[position]
        val view = convertView ?:
            LayoutInflater.from(ctx).inflate(R.layout.detail_card, parent, false)
        val tvDetail = view!!.findViewById(R.id.tvDetail) as TextView
        tvDetail.text = detail.data
        return view
    }

    override fun getCount() = details.size
    override fun getItem(position: Int) = details[position]
}
