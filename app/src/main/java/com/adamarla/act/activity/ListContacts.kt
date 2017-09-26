package com.adamarla.act.activity

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.util.Log
import com.adamarla.act.ACTAD
import com.adamarla.act.R
import com.adamarla.act.data.Contact
import com.adamarla.act.data.ContactDetail
import com.adamarla.act.data.DetailType
import com.adamarla.act.gui.ContactListAdapter
import io.objectbox.Box


class ListContacts : AppCompatActivity() {

    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mAdapter: RecyclerView.Adapter<ContactListAdapter.ViewHolder>

    lateinit var contactsBox: Box<Contact>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_contacts)
        val toolbar = findViewById(R.id.tbMain) as Toolbar?
        setSupportActionBar(toolbar)

        mRecyclerView = findViewById(R.id.rvList) as RecyclerView

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true)
        mRecyclerView.isClickable = true

        // use a linear layout manager
        mRecyclerView.layoutManager = LinearLayoutManager(this)


        Log.d("adamarla", application.javaClass.name)
        contactsBox = (application as ACTAD).boxStore.boxFor(Contact::class.java)

        // specify an adapter (see also next example)
        val contact1 = Contact(0, "Able", "Kind", 20110101,
                mutableListOf(ContactDetail(0, 0, DetailType.Address,
                        "4411 Washington Blvd, Jersey City, NJ, 90210"),
                        ContactDetail(0, 0, DetailType.Email, "abc@mail.com"),
                        ContactDetail(0, 0, DetailType.Email, "abc@mail.com"),
                        ContactDetail(0, 0, DetailType.Phone, "+17744209890")))

        val contact2 = Contact(0, "Able", "Kind", 20110101,
                mutableListOf(ContactDetail(0, 0, DetailType.Address,
                        "4411 Washington Blvd, Jersey City, NJ, 90210"),
                        ContactDetail(0, 0, DetailType.Email, "abc@mail.com"),
                        ContactDetail(0, 0, DetailType.Email, "abc@mail.com"),
                        ContactDetail(0, 0, DetailType.Phone, "+17744209890")))

        val allContactsQuery = contactsBox.query().build()
        Log.d("adamarla", "saved items = ${allContactsQuery.find().size}")

        val contacts: List<Contact> = allContactsQuery.find()
//        contacts.map {
//            val allDetailsQuery = detailsBox.query().equal(ContactDetail_.contactId, it.id).build()
//            it.contactDetails.addAll(allDetailsQuery.find())
//            Log.d("adamarla", "${it.firstName} has ${allDetailsQuery.find().size} details")
//        }

        mAdapter = ContactListAdapter(this, contacts)
        mRecyclerView.adapter = mAdapter

        val fab = findViewById(R.id.fab) as FloatingActionButton?
        fab?.setOnClickListener { _ ->
            val context = ListContacts@this
            val intent = Intent(context, ManageContact::class.java)
            intent.putExtra("contact", Contact(0, "", "", 0,
                mutableListOf(ContactDetail(0, 0, DetailType.Email, ""),
                    ContactDetail(0, 0, DetailType.Phone, ""))))
            context.startActivity(intent)
        }
    }

}
