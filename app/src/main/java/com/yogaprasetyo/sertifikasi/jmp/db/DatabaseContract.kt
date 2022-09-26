package com.yogaprasetyo.sertifikasi.jmp.db

import android.provider.BaseColumns

internal class DatabaseContract {

    internal class UserColumns : BaseColumns {
        companion object {
            const val TABLE_NAME = "users"
            const val _ID = "_id"
            const val NAME = "name"
            const val PHONE = "phone"
            const val ADDRESS = "address"
            const val LOCATION = "location"
            const val GENDER = "gender"
            const val PHOTO_PATH = "photo_path"
        }
    }
}