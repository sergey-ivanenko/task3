package io.bitbucket.sergey_ivanenko.task3.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import io.bitbucket.sergey_ivanenko.task3.data.entities.ContactData

@Database(entities = [ContactData::class], version = 1, exportSchema = false)
abstract class ContactDatabase : RoomDatabase() {

    abstract fun contactDao(): ContactDao

    companion object {
        @Volatile
        private var INSTANCE: ContactDatabase? = null
        private const val DB_NAME = "contact_database"

        @Synchronized
        fun getDatabase(context: Context): ContactDatabase {
            val instance = INSTANCE
            if (instance != null) {
                return instance
            }
            val dbInstance = Room.databaseBuilder(
                context.applicationContext,
                ContactDatabase::class.java,
                DB_NAME
            ).build()
            INSTANCE = dbInstance

            return dbInstance
        }
    }
}