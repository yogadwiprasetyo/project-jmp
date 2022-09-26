package com.yogaprasetyo.sertifikasi.jmp

import android.Manifest
import android.content.ContentResolver
import android.content.Intent
import android.content.Intent.ACTION_PICK
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.yogaprasetyo.sertifikasi.jmp.databinding.ActivityMainBinding
import com.yogaprasetyo.sertifikasi.jmp.db.UserHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.io.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * created by yogaprasetyo on 22/09/26
 *
 * This the main activity to input form user data.
 * All the form should be filled and can't be empty.
 *
 * Also this activity handle permission for GPS location and directory access,
 * this permission to make sure app can get data for address name and image.
 * */
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var locationManager: LocationManager
    private lateinit var locationListener: LocationListener
    private lateinit var db: UserHelper
    private lateinit var gender: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        db = UserHelper.getInstance(this)

        radioCheckedListener()
        initListenerButton()
        locationListenerImpl()
    }

    /**
     * Handle RadioGroup for on checked event
     * */
    private fun radioCheckedListener() {
        binding.rgGender.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                binding.rbMan.id -> {
                    gender = binding.rbMan.text.toString()
                    Toast.makeText(this, gender, Toast.LENGTH_LONG).show()
                }
                binding.rbWoman.id -> {
                    gender = binding.rbWoman.text.toString()
                    Toast.makeText(this, gender, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    /**
     * Setup click listener for all button
     * */
    private fun initListenerButton() {
        binding.apply {
            btnLocation.setOnClickListener { requestLocationUpdate() }
            btnUploadImage.setOnClickListener { startGallery() }
            btnSubmit.setOnClickListener(::saveToDB)
        }
    }

    /**
     * Request location with new updates from [LocationManager]
     * */
    private fun requestLocationUpdate() {
        if (
            checkPermission(Manifest.permission.ACCESS_FINE_LOCATION) &&
            checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
        ) {
            locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                MIN_TIME_TO_UPDATES,
                MIN_DISTANCE,
                locationListener
            )
            locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
        } else {
            requestPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    /**
     * Launcher to request permission of GPS or Directory
     * */
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        when {
            permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false -> {
                requestLocationUpdate()
            }
            permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false -> {
                requestLocationUpdate()
            }
            permissions[Manifest.permission.READ_EXTERNAL_STORAGE] ?: false -> {
                startGallery()
            }
            else -> {}
        }
    }

    /**
     * Open folder with only show file type is image,
     * when permission to access directory not granted, request the permission.
     * */
    private fun startGallery() {
        if (!checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE)) {
            requestPermissionLauncher.launch(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE))
            return
        }

        val intent = Intent().apply {
            action = ACTION_PICK
            type = "image/*"
        }
        val chooser = Intent.createChooser(intent, "Pilih gambar")
        launcherIntentGallery.launch(chooser)
    }

    /**
     * Launcher to gallery on client
     * */
    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val selectedImage: Uri = result.data?.data as Uri
            val myFile = uriToFile(selectedImage)

            binding.ivProfile.setImageURI(selectedImage)
            binding.tvPathPhoto.text = myFile.absolutePath
        }
    }

    /**
     * Changing uri image to file using write byte array
     * */
    private fun uriToFile(selectedImg: Uri): File {
        val context = this
        val contentResolver: ContentResolver = context.contentResolver
        val myFile = createTempFile()

        val inputStream = contentResolver.openInputStream(selectedImg) as InputStream
        val outputStream: OutputStream = FileOutputStream(myFile)
        val buf = ByteArray(1024)
        var len: Int
        while (inputStream.read(buf).also { len = it } > 0) outputStream.write(buf, 0, len)
        outputStream.close()
        inputStream.close()

        return reduceFileImage(myFile)
    }

    /**
     * Reducing image until less then or equal 500Kb
     * */
    private fun reduceFileImage(file: File): File {
        val bitmap = BitmapFactory.decodeFile(file.path)
        var compressQuality = 100
        var streamLength: Int
        do {
            val bmpStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, bmpStream)
            val bmpPicByteArray = bmpStream.toByteArray()
            streamLength = bmpPicByteArray.size
            compressQuality -= 5
        } while (streamLength > 500000)
        bitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, FileOutputStream(file))
        return file
    }

    /**
     * Write temporary image file
     * */
    private fun createTempFile(): File {
        val dirLocation = File("${this.filesDir.absolutePath}/profile")
        if (!dirLocation.exists()) dirLocation.mkdir()
        return File.createTempFile(timeStamp, ".jpg", dirLocation)
    }

    /**
     * Time stamp for renaming photo file from gallery
     * */
    private val timeStamp: String = SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault())
        .format(System.currentTimeMillis())

    /**
     * Saving all input data form to database [UserHelper.insert]
     * */
    private fun saveToDB(view: View) {
        if (!isFormCompleted()) {
            Toast.makeText(this, "Semua input harus diisi!", Toast.LENGTH_LONG).show()
            return
        }

        val data = UserModel(
            gender = gender,
            name = binding.etName.text.toString(),
            address = binding.etAddress.text.toString(),
            phone = binding.etPhone.text.toString(),
            location = binding.tvLocation.text.toString(),
            imagePath = binding.tvPathPhoto.text.toString(),
        )

        // Add to DB using DatabaseHelper
        CoroutineScope(Dispatchers.Main).launch {
            db.open()
            val deferredResult = async(Dispatchers.IO) { db.insert(data) }
            val result = deferredResult.await()
            if (result < 0) {
                Toast.makeText(
                    this@MainActivity,
                    "Data tidak berhasil disimpan!",
                    Toast.LENGTH_LONG
                ).show()
                return@launch
            }
            db.close()
        }

        moveToListActivity()
    }

    /**
     * Validate all input in the form is filled and not empty.
     * */
    private fun isFormCompleted(): Boolean {
        val isInputFilled =
            !binding.etName.isEmpty && !binding.etAddress.isEmpty && !binding.etPhone.isEmpty
        val isLocationAndPhotoAvailable =
            binding.tvLocation.isNotEmpty && binding.tvPathPhoto.isNotEmpty
        return isInputFilled && isLocationAndPhotoAvailable && gender.isNotEmpty()
    }

    /**
     * Extension property to simplify checking value is empty or not, vice versa
     * */
    private val EditText.isEmpty get() = this.text.isEmpty()
    private val TextView.isNotEmpty get() = this.text.isNotEmpty()

    /**
     * Finish this activity, and move to [ListUserActivity]
     * */
    private fun moveToListActivity() {
        startActivity(Intent(this, ListUserActivity::class.java))
        finish()
    }

    /**
     * Implement interface [LocationListener] to handle
     * location changes when updated
     * */
    private fun locationListenerImpl() {
        locationListener = object : LocationListener {
            override fun onLocationChanged(location: Location) {
                binding.tvLocation.text = getAddressName(location)
            }

            override fun onProviderDisabled(provider: String) {
                Toast.makeText(
                    this@MainActivity,
                    "Nyalakan GPS kamu untuk mendapatkan data!",
                    Toast.LENGTH_LONG
                ).show()
            }

            override fun onProviderEnabled(provider: String) {
                Toast.makeText(
                    this@MainActivity,
                    "GPS dinyalakan! Tunggu sebentar sedang mencari data...",
                    Toast.LENGTH_LONG
                ).show()
            }

            override fun onFlushComplete(requestCode: Int) {}
            override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
        }
    }

    /**
     * Get address name from latitude and longitude,
     * if not detected, jus return "Unknown address"
     * */
    private fun getAddressName(location: Location): String {
        return try {
            val geocoder = Geocoder(this)
            val allAddress = geocoder.getFromLocation(location.latitude, location.longitude, 1)
            if (allAddress.isEmpty()) {
                "Unknown address"
            } else {
                allAddress[0].getAddressLine(0)
            }
        } catch (e: Exception) {
            "Something wrong when get address name"
        }
    }

    /**
     * Check all manifest permission
     * */
    private fun checkPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    companion object {
        private const val MIN_TIME_TO_UPDATES = 5000L
        private const val MIN_DISTANCE = 0f
    }
}