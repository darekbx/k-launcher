package com.klauncher.repository

import android.arch.persistence.room.Room
import android.content.Context
import com.klauncher.repository.tables.Entry

open class Repository(val context: Context) {

    companion object {
        val FILE = "launcher-db"
    }

    open fun database(): Database =
            Room
                    .databaseBuilder(context, Database::class.java, FILE)
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