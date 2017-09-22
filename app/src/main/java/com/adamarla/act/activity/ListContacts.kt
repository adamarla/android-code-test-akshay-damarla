package com.adamarla.act.activity

import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.View
import com.adamarla.act.R
import data.Address
import data.Contact
import gui.ContactListAdapter


class ListContacts : AppCompatActivity() {

    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mAdapter: RecyclerView.Adapter<ContactListAdapter.

    ViewHolder>
    private lateinit var mLayoutManager: RecyclerView.LayoutManager

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
        mLayoutManager = LinearLayoutManager(this);
        mRecyclerView.layoutManager = mLayoutManager;

        // specify an adapter (see also next example)
        val contacts: List<Contact> = listOf(Contact("Josh", "Himmel", 20160501,
                listOf(Address("4411 Washington Blvd", "Jersey City", "NJ", "90210")),
                listOf("abcd@mail.com", "ab-cd@mail.com"),
                listOf("7744449404", "2123332323")),
                Contact("Josh", "Himmel", 20160501,
                        listOf(Address("4411 Washington Blvd", "Jersey City", "NJ", "90210")),
                        listOf("abcd@mail.com", "ab-cd@mail.com"),
                        listOf("7744449404", "2123332323")))
        mAdapter = ContactListAdapter(this, contacts)
        mRecyclerView.adapter = mAdapter

        val fab = findViewById(R.id.fab) as FloatingActionButton?
        fab!!.setOnClickListener(View.OnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        })
    }

}
