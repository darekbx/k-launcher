package com.klauncher.repository.tables

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity(tableName = "entry")
data class Entry(
        @ColumnInfo(name = "name") var name: String,
        @ColumnInfo(name = "packageName") var packageName: String,
        @ColumnInfo(name = "clickCount") var clickCount: Int) {

    @ColumnInfo(name = "id")
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
}