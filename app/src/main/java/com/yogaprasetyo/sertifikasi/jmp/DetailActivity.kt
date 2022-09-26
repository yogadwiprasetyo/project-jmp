package com.yogaprasetyo.sertifikasi.jmp

import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Base64
import androidx.appcompat.app.AppCompatActivity
import com.yogaprasetyo.sertifikasi.jmp.databinding.ActivityDetailBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

/**
 * created by yogaprasetyo on 22/09/26
 *
 * Showing the detail information about user.
 * The activity receive intent from [ListUserActivity] as [UserModel] then
 * show the data in editText with multiline type.
 * */
class DetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val intent = intent?.getParcelableExtra<UserModel>(EXTRA_USER) as UserModel
        initView(intent)
    }

    /**
     * Process the intent that receive for showing to the app
     * */
    private fun initView(intent: UserModel) {
        val (id, name, address, phone, gender, location, imagePath) = intent
        val fullData = """
            ID: $id
            Nama: $name
            Alamat: $address
            No. HP: $phone
            Gender: $gender
            Lokasi Pendaftaran: $location""".trimIndent()

        binding.etData.setText(fullData)
        binding.ivProfile.setImageURI(Uri.fromFile(File(imagePath)))
    }

    companion object {
        const val EXTRA_USER = "extra_user"
    }
}