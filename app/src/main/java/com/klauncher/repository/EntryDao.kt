package com.klauncher.repository

import android.arch.persistence.room.*
import com.klauncher.repository.tables.Entry

@Dao
interface EntryDao {

    @Query("SELECT * FROM entry WHERE packageName = :packageName")
    fun getByPackageName(packageName: String): Entry

    @Query("SELECT * FROM entry")
    fun listAll(): List<Entry>

    @Insert
    fun addAll(vararg entries: Entry)

    @Insert
    fun add(entry: Entry)

    @Delete
    fun delete(entry: Entry)

    @Update
    fun update(entry: Entry)
}