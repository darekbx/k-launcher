package com.klauncher.repository

import android.arch.persistence.db.SupportSQLiteDatabase
import android.arch.persistence.room.Room
import android.arch.persistence.room.migration.Migration
import android.content.Context
import com.klauncher.repository.tables.Entry

open class Repository(val context: Context) {

    companion object {
        val FILE = "launcher-db"
    }

    open fun database(): Database =
            Room
                    .databaseBuilder(context, Database::class.java, FILE)
                    .addMigrations(object: Migration(1, 2)  {
                        override fun migrate(database: SupportSQLiteDatabase) {
                            val TABLE_NAME_TEMP = "temp_entry"
                            val TABLE_NAME = "entry"
                            database.execSQL("CREATE TABLE IF NOT EXISTS `$TABLE_NAME_TEMP` (" +
                                    "`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                                    "`name` TEXT NOT NULL, " +
                                    "`packageName` TEXT NOT NULL, " +
                                    "`clickCount` INTEGER NOT NULL DEFAULT 0)")

                            database.execSQL("INSERT INTO $TABLE_NAME_TEMP (name, packageName, clickCount) "
                                    + "SELECT name, packageName, clickCount "
                                    + "FROM $TABLE_NAME")

                            database.execSQL("DROP TABLE $TABLE_NAME")

                            database.execSQL("ALTER TABLE $TABLE_NAME_TEMP RENAME TO $TABLE_NAME")
                        }
                    })
                    .allowMainThreadQueries()
                    .build()

    open fun increaseCountByOne(packageName: String, name: String) {
        val dao = database().entryDao()
        with (dao) {
            val entry = getByPackageName(packageName)
            when (entry) {
                null -> add(Entry(name, packageName, 1))
                else -> {
                    entry.clickCount += 1
                    update(entry)
                }
            }
        }
    }
}