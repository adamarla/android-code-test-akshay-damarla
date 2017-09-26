package com.adamarla.act.gui

import android.app.DialogFragment
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

class EditItemDialog(): DialogFragment() {

    private lateinit var item: ContactDetail
    private lateinit var host: EditDialogHost
    private lateinit var editFields: List<Triple<Int, String, String>>

    constructor(item: ContactDetail, host: EditDialogHost): this() {
        this.item = item
        this.host = host
    }

    interface EditDialogHost {
        fun onSave(data: String)
        fun onDelete()
    }

    override fun onCreateView(inflater: LayoutInflater?,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        val view = inflater!!.inflate(R.layout.dialog_edit_detail, container, false)

        listOf(R.id.btnSave, R.id.btnDelete).map {
            (view.findViewById(it) as Button).setOnClickListener { view ->
                if (view.id == R.id.btnSave)
                    host.onSave(getTexts())
                else
                    host.onDelete()
                dialog.dismiss()
            }
        }

        editFields = when (item.detailType) {
            DetailType.Address -> {
                val tokens = item.data.split(",").map { it.trim() }
                listOf(Triple(R.id.etLine, "Street", tokens[0]),
                        Triple(R.id.etCity, "City", tokens[1]),
                        Triple(R.id.etState, "State", tokens[2]),
                        Triple(R.id.etZip, "Zip", tokens[3]))
            }
            DetailType.Email -> {
                listOf(Triple(R.id.etEmail, "Email", item.data))
            }
            DetailType.Phone -> {
                listOf(Triple(R.id.etPhone, "Phone (+1234567890)", item.data))
            }
            else -> {
                val tokens = item.data.split(",")
                listOf(Triple(R.id.etFirstName, "First Name", tokens[0]),
                        Triple(R.id.etLastName, "Last Name", tokens[1]),
                        Triple(R.id.etDob, "Date of Birth", tokens[2]),
                        Triple(R.id.etPhone, "Phone", tokens[3]),
                        Triple(R.id.etEmail, "Email", tokens[4]))
            }
        }

        editFields.map { setTexts(view, it) }
        return view
    }

    /**
     * Required to ensure dialog extends to parent width
     */
    override fun onStart() {
        super.onStart()
        dialog.window.setLayout(MATCH_PARENT, WRAP_CONTENT)
    }

    private fun setTexts(dialog: View, fieldInfo: Triple<Int, String, String>) {
        val editText = dialog.findViewById(fieldInfo.first) as EditText
        editText.setText(fieldInfo.third, TextView.BufferType.EDITABLE)
        editText.hint = fieldInfo.second
        editText.visibility = View.VISIBLE
    }

    private fun getTexts() = editFields.map { editField ->
        (dialog.findViewById(editField.first) as EditText).text.toString()
    }.joinToString(separator = ",")

}