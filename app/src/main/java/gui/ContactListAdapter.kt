package gui

import android.content.Context
import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.adamarla.act.R
import com.adamarla.act.activity.ManageContact
import data.Contact


/**
 * Created by adamarla on 9/20/17.
 */

class ContactListAdapter(val context: Context, var contacts: List<Contact>):
        RecyclerView.Adapter<ContactListAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView){
        init {
            itemView?.setOnClickListener { _ ->
                val intent = Intent(context, ManageContact::class.java)
                intent.putExtra("contact", contacts[adapterPosition])
                context.startActivity(intent)
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
        holder!!.tvHeadline.text = "${contact.firstName} ${contact.lastName}"
        holder!!.tvBlurb.text = contact.dob.toString()
    }

    override fun getItemCount(): Int = contacts.size

}