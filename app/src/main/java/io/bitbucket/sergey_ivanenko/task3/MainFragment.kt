package io.bitbucket.sergey_ivanenko.task3

import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.ContactsContract
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import io.bitbucket.sergey_ivanenko.task3.data.entities.ContactData
import io.bitbucket.sergey_ivanenko.task3.databinding.FragmentMainBinding

class MainFragment : Fragment(R.layout.fragment_main) {

    private var _binding: FragmentMainBinding? = null
    private val binding get() = requireNotNull(_binding)

    private val mContactViewModel: ContactViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentMainBinding.bind(view)
        val intent = Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI)


        val projection = arrayOf(
            ContactsContract.Contacts._ID,
            ContactsContract.Contacts.HAS_PHONE_NUMBER
        )

        val resultLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()) { result ->
            if (result?.resultCode == AppCompatActivity.RESULT_OK) {
                val data: Intent? = result.data

                // номер телефона, имя, фамилию, email
                val contactUri = data?.data ?: return@registerForActivityResult
                var cursor = context?.contentResolver?.query(
                    contactUri,
                    projection,
                    null,
                    null,
                    null
                )?.use {
                    if (it.moveToFirst()) {
                        do {
                            val contactId = it.getString(it.getColumnIndexOrThrow(projection[0]))
                            val hasPhone = it.getInt(it.getColumnIndexOrThrow(projection[1]))

                            var phoneNumber: String? = null
                            if (hasPhone > 0) {
                                val phoneCursor = context?.contentResolver?.query(
                                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                    null,
                                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                                    arrayOf(contactId),
                                    null) ?: return@registerForActivityResult
                                if (phoneCursor.moveToFirst()) {
                                    phoneNumber =
                                        phoneCursor.getString(phoneCursor.getColumnIndexOrThrow(
                                            ContactsContract.CommonDataKinds.Phone.NUMBER));
                                    phoneCursor.close();
                                }
                            }

                            var firstName: String? = null
                            var lastName: String? = null
                            val fullNameCursor = context?.contentResolver?.query(
                                ContactsContract.Data.CONTENT_URI,
                                arrayOf(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME,
                                    ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME),
                                "${ContactsContract.Data.MIMETYPE} = ?",
                                arrayOf(ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE),
                                null) ?: return@registerForActivityResult
                            if (fullNameCursor.moveToFirst()) {
                                firstName =
                                    fullNameCursor.getString(fullNameCursor.getColumnIndexOrThrow(
                                        ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME))
                                lastName =
                                    fullNameCursor.getString(fullNameCursor.getColumnIndexOrThrow(
                                        ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME))
                                fullNameCursor.close();
                            }

                            var email: String? = null;
                            val emailCursor = context?.contentResolver?.query(
                                ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                                null,
                                ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?",
                                arrayOf(contactId),
                                null) ?: return@registerForActivityResult
                            if (emailCursor.moveToFirst()) {
                                email = emailCursor.getString(emailCursor.getColumnIndexOrThrow(
                                    ContactsContract.CommonDataKinds.Email.DATA))
                                emailCursor.close();
                            }

                            // Save data
                            val contact: ContactData = ContactData(
                                contactId = contactId,
                                phoneNumber = phoneNumber ?: "No phone number",
                                firstName = firstName,
                                lastName = lastName,
                                email = email
                            )

                            mContactViewModel.addContact(contact)
                            Toast.makeText(
                                context,
                                "Сохранено успешно",
                                Toast.LENGTH_SHORT).show()

                        } while (it.moveToNext())
                    }
                }
            }
        }

        binding.chooseContactButton.setOnClickListener {
            if (checkContactPermission()) {
                resultLauncher.launch(intent)
            } else {
                requestContactPermission()
            }
        }

        binding.showContactsButton.setOnClickListener {
            showDialog(it)
        }
    }

    private fun showDialog(view: View) {
        val phoneList = mutableListOf<String>()
        val builder = AlertDialog.Builder(requireContext())

        val listData: LiveData<List<ContactData>> = mContactViewModel.readAllContacts
        val list = listData.value?.toMutableList()

        builder.setTitle("Pick a phone number")
            .setItems(phoneList.toTypedArray(),
                DialogInterface.OnClickListener { dialog, which ->
                    Toast.makeText(context, "Выбранный ${phoneList[which]}", Toast.LENGTH_SHORT)
                        .show()

                })
        builder.create()
    }

    private fun checkContactPermission(): Boolean {
        //check if permission was granted/allowed or not
        return ContextCompat.checkSelfPermission(
            requireContext(),
            android.Manifest.permission.READ_CONTACTS
        ) == PackageManager.PERMISSION_GRANTED
    }

    private val CONTACT_PERMISSION_CODE = 1;
    private fun requestContactPermission() {
        //request the READ_CONTACTS permission
        val permission = arrayOf(android.Manifest.permission.READ_CONTACTS)
        ActivityCompat.requestPermissions(requireActivity(), permission, CONTACT_PERMISSION_CODE)
    }
}