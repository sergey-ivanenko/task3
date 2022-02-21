package io.bitbucket.sergey_ivanenko.task3

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import io.bitbucket.sergey_ivanenko.task3.data.entities.ContactData

class ContactDialogFragment : DialogFragment() {

    private val mContactViewModel: ContactViewModel by viewModels()

    private val phoneList = mutableListOf<String>()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let { it ->
            // Use the Builder class for convenient dialog construction
            val builder = AlertDialog.Builder(it)
            val list: List<ContactData>? = mContactViewModel.readAllContacts.value
            list?.let { contactList ->
                for (contact in contactList) {
                    phoneList.add(contact.phoneNumber)
                }
            }
            builder.setTitle("Pick a phone number")
                .setItems(phoneList.toTypedArray(),
                    DialogInterface.OnClickListener { dialog, which ->
                        // The 'which' argument contains the index position
                        // of the selected item
                        Toast.makeText(context, "Выбранный ${phoneList[which]}", Toast.LENGTH_SHORT).show()

                    })
            builder.create()

        } ?: throw IllegalStateException("Activity cannot be null")
    }

}