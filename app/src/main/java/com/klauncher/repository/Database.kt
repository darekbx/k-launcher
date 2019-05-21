package com.klauncher.repository

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import com.klauncher.repository.tables.Entry

@Database(entities = arrayOf(Entry::class), version = 2, exportSchema = false)
abstract class Database: RoomDatabase() {

    abstract fun entryDao(): EntryDao
}