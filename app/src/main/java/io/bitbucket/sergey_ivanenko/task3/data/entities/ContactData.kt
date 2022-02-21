package io.bitbucket.sergey_ivanenko.task3.data.entities

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity(tableName = "contact_table")
@Parcelize
data class ContactData(
    @PrimaryKey(autoGenerate = false)
    val contactId: String,
    val phoneNumber: String,
    val firstName: String? = "",
    val lastName: String? = "",
    val email: String? = ""
) : Parcelable