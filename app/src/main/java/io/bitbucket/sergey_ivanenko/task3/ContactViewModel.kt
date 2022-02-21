package io.bitbucket.sergey_ivanenko.task3

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import io.bitbucket.sergey_ivanenko.task3.data.ContactDatabase
import io.bitbucket.sergey_ivanenko.task3.data.ContactRepository
import io.bitbucket.sergey_ivanenko.task3.data.entities.ContactData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ContactViewModel(application: Application) : AndroidViewModel(application) {

    private val contactRepository: ContactRepository
    val readAllContacts: LiveData<List<ContactData>>

    init {
        val contactDao = ContactDatabase.getDatabase(application).contactDao()
        contactRepository = ContactRepository(contactDao)
        readAllContacts = contactRepository.readAllContacts()
    }

    fun addContact(contact: ContactData) {
        viewModelScope.launch(Dispatchers.IO) {
            contactRepository.addContact(contact)
        }
    }
}