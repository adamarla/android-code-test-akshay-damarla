package com.adamarla.act.gui

import android.app.DialogFragment
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.adamarla.act.R
import com.adamarla.act.data.ContactDetail
import com.adamarla.act.data.DetailType

/**
 * Generic "dynamic" edit dialog
 *
 * Created by adamarla on 9/24/17.
 */

class EditItemDialog(private val item: ContactDetail = ContactDetail(detailType = DetailType.Phone),
                     private val host: EditDialogHost? = null,
                     private val allowDelete: Boolean = false): DialogFragment() {

    interface EditDialogHost {
        fun onSave(data: String, detailType: DetailType)
        fun onDelete()
        fun onDismiss()
    }

    override fun onDismiss(dialog: DialogInterface?) {
        super.onDismiss(dialog)
        host!!.onDismiss()
    }

    override fun onCreateView(inflater: LayoutInflater?,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        val view = inflater!!.inflate(R.layout.dialog_edit_detail, container, false)

        listOf(R.id.btnSave, R.id.btnDelete).map {
            (view.findViewById(it) as Button).setOnClickListener { view ->
                if (view.id == R.id.btnSave)
                    host!!.onSave(getTexts(getEditFields(item)), item.detailType)
                else
                    host!!.onDelete()
                dialog.dismiss()
            }
        }

        if (!allowDelete)
            view.findViewById(R.id.btnDelete).visibility = View.GONE

        setTexts(view, getEditFields(item))
        return view
    }

    /**
     * Required to ensure dialog extends to parent width
     */
    override fun onStart() {
        super.onStart()
        dialog.window.setLayout(MATCH_PARENT, WRAP_CONTENT)
    }

    private val setTexts = { dialog: View, editFields: List<FieldInfo> ->
        editFields.map { editField ->
            val editText = dialog.findViewById(editField.id) as EditText
            editText.setText(editField.text, TextView.BufferType.EDITABLE)
            editText.hint = editField.hint
            editText.visibility = View.VISIBLE
        }
    }

    private val getTexts = { editFields: List<FieldInfo> ->
        editFields.map { editField ->
            (dialog.findViewById(editField.id) as EditText).text.toString()
        }.joinToString(separator = ",")
    }

    private val getEditFields = { editItem: ContactDetail ->
        val tokens = editItem.data.split(",").map { it.trim().replace(Regex("^0$"), "") }
        when (editItem.detailType) {
            DetailType.Address -> listOf(FieldInfo(R.id.etLine, "Street", tokens[0]),
                    FieldInfo(R.id.etCity, "City", tokens[1]),
                    FieldInfo(R.id.etState, "State", tokens[2]),
                    FieldInfo(R.id.etZip, "Zip", tokens[3]))
            DetailType.Email -> listOf(FieldInfo(R.id.etEmail, "Email", tokens[0]))
            DetailType.Phone -> listOf(FieldInfo(R.id.etPhone, "Phone", tokens[0]))
            DetailType.Profile -> listOf(FieldInfo(R.id.etFirstName, "First Name", tokens[0]),
                    FieldInfo(R.id.etLastName, "Last Name", tokens[1]),
                    FieldInfo(R.id.etDob, "Date of Birth (YYYYMMDD)", tokens[2]))
            else ->listOf(FieldInfo(R.id.etFirstName, "First Name", tokens[0]),
                    FieldInfo(R.id.etLastName, "Last Name", tokens[1]),
                    FieldInfo(R.id.etDob, "Date of Birth (YYYYMMDD)", tokens[2]),
                    FieldInfo(R.id.etPhone, "Phone", tokens[3]),
                    FieldInfo(R.id.etEmail, "Email", tokens[4]))
        }
    }
}

data class FieldInfo(val id: Int, val hint: String, val text: String)