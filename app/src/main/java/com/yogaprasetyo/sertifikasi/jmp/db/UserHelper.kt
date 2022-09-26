package com.yogaprasetyo.sertifikasi.jmp.db

import android.content.ContentValues
import android.content.Context
import android.database.SQLException
import android.database.sqlite.SQLiteDatabase
import com.yogaprasetyo.sertifikasi.jmp.UserModel
import com.yogaprasetyo.sertifikasi.jmp.db.DatabaseContract.UserColumns.Companion.ADDRESS
import com.yogaprasetyo.sertifikasi.jmp.db.DatabaseContract.UserColumns.Companion.GENDER
import com.yogaprasetyo.sertifikasi.jmp.db.DatabaseContract.UserColumns.Companion.LOCATION
import com.yogaprasetyo.sertifikasi.jmp.db.DatabaseContract.UserColumns.Companion.NAME
import com.yogaprasetyo.sertifikasi.jmp.db.DatabaseContract.UserColumns.Companion.PHONE
import com.yogaprasetyo.sertifikasi.jmp.db.DatabaseContract.UserColumns.Companion.PHOTO_PATH
import com.yogaprasetyo.sertifikasi.jmp.db.DatabaseContract.UserColumns.Companion.TABLE_NAME
import com.yogaprasetyo.sertifikasi.jmp.db.DatabaseContract.UserColumns.Companion._ID

class UserHelper(context: Context) {

    private var dbHelper: DatabaseHelper = DatabaseHelper(context)
    private lateinit var database: SQLiteDatabase

    @Throws(SQLException::class)
    fun open() {
        database = dbHelper.writableDatabase
    }

    fun close() {
        dbHelper.close()
        if (database.isOpen) database.close()
    }

    fun queryAll(): ArrayList<UserModel> = database.query(
        DB_TABLE,
        null,
        null,
        null,
        null,
        null,
        "$_ID ASC"
    ).run {
        val userList = ArrayList<UserModel>()
        while (moveToNext()) {
            val id = getInt(getColumnIndexOrThrow(_ID))
            val name = getString(getColumnIndexOrThrow(NAME))
            val address = getString(getColumnIndexOrThrow(ADDRESS))
            val phone = getString(getColumnIndexOrThrow(PHONE))
            val gender = getString(getColumnIndexOrThrow(GENDER))
            val location = getString(getColumnIndexOrThrow(LOCATION))
            val imagePath = getString(getColumnIndexOrThrow(PHOTO_PATH))
            userList.add(UserModel(id, name, address, phone, gender, location, imagePath))
        }
        userList
    }

    fun queryById(id: String): UserModel {
        return database.query(DB_TABLE, null, "$_ID = ?", arrayOf(id), null, null, null, null).run {
            UserModel(
                name = getString(getColumnIndexOrThrow(NAME)),
                address = getString(getColumnIndexOrThrow(ADDRESS)),
                phone = getString(getColumnIndexOrThrow(PHONE)),
                gender = getString(getColumnIndexOrThrow(GENDER)),
                location = getString(getColumnIndexOrThrow(LOCATION)),
                imagePath = getString(getColumnIndexOrThrow(PHOTO_PATH))
            )
        }
    }

    fun insert(userModel: UserModel): Long {
        val (_, name, address, phone, gender, location, imagePath) = userModel
        val values = ContentValues().apply {
            put(NAME, name)
            put(ADDRESS, address)
            put(PHONE, phone)
            put(GENDER, gender)
            put(LOCATION, location)
            put(PHOTO_PATH, imagePath)
        }
        return database.insert(DB_TABLE, null, values)
    }

    companion object {
        private const val DB_TABLE = TABLE_NAME

        // Implement singleton pattern
        private var INSTANCE: UserHelper? = null
        fun getInstance(context: Context): UserHelper =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: UserHelper(context)
            }
    }
}