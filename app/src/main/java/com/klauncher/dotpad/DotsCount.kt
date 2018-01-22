package com.klauncher.dotpad

import android.database.sqlite.SQLiteDatabase
import android.os.Environment

class DotsCount {

    companion object {
        val DOTS_DB_FILE = "dotpad.sqlite"
    }

    fun countActiveDots(): Int {
        openDataBase().use { db ->
            val cursor = db.query("dots", null,
                    "isArchival = 0 AND is_sticked = 0", null,
                    null, null, null, null)
            cursor?.use { cursor ->
                return cursor.count
            }
        }
        return -1
    }

    fun openDataBase(): SQLiteDatabase {
        val path = "${Environment.getExternalStorageDirectory()}/$DOTS_DB_FILE"
        return SQLiteDatabase.openDatabase(path, null,
                SQLiteDatabase.OPEN_READWRITE or SQLiteDatabase.NO_LOCALIZED_COLLATORS)
    }
}