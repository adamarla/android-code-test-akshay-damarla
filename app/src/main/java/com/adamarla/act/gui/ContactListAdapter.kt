package com.adamarla.act.gui

import android.content.Context
import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.adamarla.act.ACTAD
import com.adamarla.act.R
import com.adamarla.act.activity.ListContacts
import com.adamarla.act.activity.ManageContact
import com.adamarla.act.data.Contact
import com.adamarla.act.data.ContactDetail
import com.adamarla.act.data.ContactDetail_
import io.objectbox.Box


/**
 * Created by adamarla on 9/20/17.
 */

class ContactListAdapter(val context: Context, var contacts: List<Contact>):
        RecyclerView.Adapter<ContactListAdapter.ViewHolder>() {

    var detailsBox: Box<ContactDetail>

    fun updateContacts(contacts: List<Contact>) {
        this.contacts = contacts
        Log.d("adamarla", this.contacts[0].firstName)
        notifyDataSetChanged()
    }

    init {
        val actad = (context as ListContacts).application as ACTAD
        detailsBox = actad.boxStore.boxFor(ContactDetail::class.java)
    }

    inner class ViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView){
        init {
            itemView?.setOnClickListener { _ ->
                val selectedContact = contacts[adapterPosition]
                selectedContact.contactDetails.clear()
                val allDetailsQuery = detailsBox.query().equal(ContactDetail_.contactId, selectedContact.id).build()
                selectedContact.contactDetails.addAll(allDetailsQuery.find())

                val intent = Intent(context, ManageContact::class.java)
                intent.putExtra("contact", selectedContact)
                (context as ListContacts).startActivityForResult(intent, 2000)
            }
        }

        val tvHeadline: TextView = itemView?.findViewById(R.id.tvHeadline) as TextView
        val tvBlurb: TextView = itemView?.findViewById(R.id.tvBlurb) as TextView

    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent!!.context)
        return ViewHolder(inflater.inflate(R.layout.summary_card, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        val contact = contacts[position]
        holder!!.tvHeadline.text = contact.fullName
        holder.tvBlurb.text = "Age ${contact.age}"
    }

    override fun getItemCount(): Int = contacts.size

}