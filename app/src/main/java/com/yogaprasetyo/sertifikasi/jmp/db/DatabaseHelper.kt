package com.yogaprasetyo.sertifikasi.jmp.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.yogaprasetyo.sertifikasi.jmp.db.DatabaseContract.UserColumns.Companion.ADDRESS
import com.yogaprasetyo.sertifikasi.jmp.db.DatabaseContract.UserColumns.Companion.GENDER
import com.yogaprasetyo.sertifikasi.jmp.db.DatabaseContract.UserColumns.Companion.LOCATION
import com.yogaprasetyo.sertifikasi.jmp.db.DatabaseContract.UserColumns.Companion.NAME
import com.yogaprasetyo.sertifikasi.jmp.db.DatabaseContract.UserColumns.Companion.PHONE
import com.yogaprasetyo.sertifikasi.jmp.db.DatabaseContract.UserColumns.Companion.PHOTO_PATH
import com.yogaprasetyo.sertifikasi.jmp.db.DatabaseContract.UserColumns.Companion.TABLE_NAME
import com.yogaprasetyo.sertifikasi.jmp.db.DatabaseContract.UserColumns.Companion._ID

class DatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onCreate(sqLiteDatabase: SQLiteDatabase) {
        sqLiteDatabase.execSQL(SQL_CREATE_TABLE_USERS)
    }

    override fun onUpgrade(sqLiteDatabase: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        sqLiteDatabase.execSQL(SQL_DROP_TABLE_IF_EXISTS)
        onCreate(sqLiteDatabase)
    }

    companion object {
        private const val DATABASE_NAME = "registration_user"
        private const val DATABASE_VERSION = 1
        private const val SQL_CREATE_TABLE_USERS = "CREATE TABLE $TABLE_NAME" +
                " ($_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                " $NAME TEXT NOT NULL," +
                " $ADDRESS TEXT NOT NULL," +
                " $PHONE TEXT NOT NULL," +
                " $GENDER TEXT NOT NULL," +
                " $LOCATION TEXT NOT NULL," +
                " $PHOTO_PATH TEXT NOT NULL)"

        private const val SQL_DROP_TABLE_IF_EXISTS = "DROP TABLE IF EXISTS $TABLE_NAME"
    }
}