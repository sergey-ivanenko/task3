package io.bitbucket.sergey_ivanenko.task3.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.bitbucket.sergey_ivanenko.task3.data.entities.ContactData

@Dao
interface ContactDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addContact(contact: ContactData)

    @Query("SELECT * FROM contact_table ORDER BY contactId")
    fun readAllContacts() : LiveData<List<ContactData>>
}