package io.bitbucket.sergey_ivanenko.task3.data

import androidx.lifecycle.LiveData
import io.bitbucket.sergey_ivanenko.task3.data.entities.ContactData

class ContactRepository(private val contactDao: ContactDao) {

    fun readAllContacts(): LiveData<List<ContactData>> = contactDao.readAllContacts()

    suspend fun addContact(contact: ContactData) {
        contactDao.addContact(contact)
    }
}