package com.yogaprasetyo.sertifikasi.jmp

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * created by yogaprasetyo on 22/09/26
 *
 * The data class is used for handle result operation query DB and also
 * for simplify access in adapter.
 *
 * Using on [MainActivity] [DetailActivity] [ListUserActivity]
 * Using on [UserAdapter] [com.yogaprasetyo.sertifikasi.jmp.db.UserHelper]
 * */
@Parcelize
data class UserModel(
    val id: Int = 0,
    val name: String,
    val address: String,
    val phone: String,
    val gender: String,
    val location: String,
    val imagePath: String
) : Parcelable
