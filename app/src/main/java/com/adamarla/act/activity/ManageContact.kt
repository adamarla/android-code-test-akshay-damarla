package com.adamarla.act.activity

import android.content.Context
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.adamarla.act.ACTAD
import com.adamarla.act.R
import com.adamarla.act.data.Contact
import com.adamarla.act.data.ContactDetail
import com.adamarla.act.data.DetailType
import com.adamarla.act.gui.EditItemDialog
import io.objectbox.Box
import io.objectbox.BoxStore

class ManageContact : AppCompatActivity(), EditItemDialog.EditDialogHost {

    private lateinit var contact: Contact

    private var itemPosition = NOT_A_LIST_ITEM_POSITION

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage_contact)

        contact = intent.getParcelableExtra("contact")

        val rlSummary = findViewById(R.id.rlSummary)
        rlSummary!!.setOnLongClickListener { _ ->
            itemPosition = NOT_A_LIST_ITEM_POSITION
            val summary = if (contact.id == 0L) contact.newContactDetails
                else contact.profileDetails
            EditItemDialog(summary, this).show(fragmentManager, "editDialog")
            true
        }

        val lv = findViewById(R.id.lvContactInfo) as ListView
        lv.onItemLongClickListener = AdapterView.OnItemLongClickListener { _, _, position, _ ->
            itemPosition = position
            val item = contact.contactDetails[position]
            val allowDelete = when (item.detailType) {
                DetailType.Address -> contact.addresses.isNotEmpty()
                DetailType.Phone -> contact.phones.size > 1
                DetailType.Email -> contact.emails.size > 1
                else -> false
            }
            EditItemDialog(item, this, allowDelete).show(fragmentManager, "editDialog")
            true
        }

        listOf(R.id.fabAddEmail, R.id.fabAddMail, R.id.fabAddPhone).map { id ->
            val fab = findViewById(id) as FloatingActionButton
            fab.setOnClickListener { _ ->
                val detailType = when (id) {
                    R.id.fabAddEmail -> DetailType.Email
                    R.id.fabAddMail -> DetailType.Address
                    else -> DetailType.Phone
                }
                val data = if (detailType == DetailType.Address) ",,," else ""
                itemPosition = NEW_ITEM_POSITION
                val newItem = ContactDetail(0, ManageContact@contact.id, detailType, data)
                EditItemDialog(newItem, this).show(fragmentManager, "editDialog")
            }
        }

        setData()
        initBoxes()
        if (contact.id == 0L)
            rlSummary.performLongClick()
    }

    override fun onSave(data: String, detailType: DetailType) {
        when (itemPosition) {
            NOT_A_LIST_ITEM_POSITION -> {
                val tokens = data.split(",").map{ it.trim() }
                contact.firstName = tokens[0]
                contact.lastName = tokens[1]
                contact.dob = tokens[2].toInt()
                contactsBox.put(contact)

                if (tokens.size > 3) {
                    listOf(tokens[3], tokens[4]).forEachIndexed { idx, token ->
                        val detail = contact.contactDetails[idx]
                        detail.data = token
                        detail.contactId = contact.id
                        detailsBox.put(detail)
                    }
                }
            }
            NEW_ITEM_POSITION -> {
                val detail = ContactDetail(0, contact.id, detailType, data)
                contact.contactDetails.add(detail)
                detailsBox.put(detail)
            }
            else -> {
                contact.contactDetails[itemPosition].data = data
                detailsBox.put(contact.contactDetails[itemPosition])
            }
        }
        setData()
    }

    override fun onDelete() {
        detailsBox.remove(contact.contactDetails[itemPosition].id)
        contact.contactDetails.removeAt(itemPosition)
        setData()
    }

    override fun onDismiss() {
        if (contact.id == 0L) finish()
    }

    private fun setData() {
        val tvHeadLine = findViewById(R.id.tvHeadline) as TextView
        tvHeadLine.text = contact.fullName

        val tvBlurb = findViewById(R.id.tvBlurb) as TextView
        tvBlurb.text = "Born: ${contact.formattedDob}"

        val lv = findViewById(R.id.lvContactInfo) as ListView
        lv.adapter = ContactDetailAdapter(this, contact.contactDetails.toTypedArray())
    }

    private fun initBoxes() {
        val boxStore: BoxStore = (application as ACTAD).boxStore
        contactsBox = boxStore.boxFor(Contact::class.java)
        detailsBox = boxStore.boxFor(ContactDetail::class.java)
    }

    companion object {
        private const val NOT_A_LIST_ITEM_POSITION = -1
        private const val NEW_ITEM_POSITION = -2
        private lateinit var contactsBox: Box<Contact>
        private lateinit var detailsBox: Box<ContactDetail>
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
        val ivIcon = view.findViewById(R.id.ivIcon) as ImageView
        val iconId = when(detail.detailType) {
            DetailType.Phone -> R.mipmap.ic_call
            DetailType.Email -> R.mipmap.ic_email
            else -> R.mipmap.ic_home
        }
        ivIcon.setImageResource(iconId)
        return view
    }

    override fun getCount() = details.size
    override fun getItem(position: Int) = details[position]
}
