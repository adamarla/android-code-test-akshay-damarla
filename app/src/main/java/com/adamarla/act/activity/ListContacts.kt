package com.adamarla.act.activity

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import com.adamarla.act.ACTAD
import com.adamarla.act.R
import com.adamarla.act.data.Contact
import com.adamarla.act.gui.ContactListAdapter
import io.objectbox.Box


class ListContacts : AppCompatActivity() {

    lateinit var contactsBox: Box<Contact>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_contacts)

        val toolbar = findViewById(R.id.tbMain) as Toolbar?
        setSupportActionBar(toolbar)

        val etFilter = findViewById(R.id.etFilter) as EditText
        etFilter.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) { }
            override fun afterTextChanged(editable: Editable?) {
                if (!editable.isNullOrBlank()) {
                    var contacts = contactsBox.query().build().find()
                    contacts = contacts.filter { it.toString().contains(editable.toString(), true) }
                    (recyclerView.adapter as ContactListAdapter).updateContacts(contacts)
                }
            }
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) { }
        })

        recyclerView = findViewById(R.id.rvList) as RecyclerView
        recyclerView.setHasFixedSize(true)
        recyclerView.isClickable = true
        recyclerView.layoutManager = LinearLayoutManager(this)

        contactsBox = (application as ACTAD).boxStore.boxFor(Contact::class.java)

        val contacts = contactsBox.query().build().find()
        val adapter = ContactListAdapter(this, contacts.sortedBy { it.fullName })
        recyclerView.adapter = adapter

        val fab = findViewById(R.id.fab) as FloatingActionButton?
        fab?.setOnClickListener { _ ->
            val context = ListContacts@this
            val intent = Intent(context, ManageContact::class.java)
            intent.putExtra("contact", Contact())
            context.startActivityForResult(intent, 1000)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val contacts: List<Contact> = contactsBox.query().build().find()
        (recyclerView.adapter as ContactListAdapter).updateContacts(contacts)
    }

    private lateinit var recyclerView: RecyclerView

}
