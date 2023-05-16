package com.bennohan.shopline.ui.editProfile

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bennohan.shopline.R
import com.bennohan.shopline.base.BaseActivity
import com.bennohan.shopline.data.Session
import com.bennohan.shopline.data.room.UserDao
import com.bennohan.shopline.databinding.ActivityEditProfileBinding
import com.crocodic.core.api.ApiStatus
import com.crocodic.core.extension.isEmptyRequired
import com.crocodic.core.extension.snacked
import com.crocodic.core.extension.textOf
import com.crocodic.core.extension.tos
import com.crocodic.core.helper.DateTimeHelper
import dagger.hilt.android.AndroidEntryPoint
import id.zelory.compressor.Compressor
import id.zelory.compressor.constraint.format
import id.zelory.compressor.constraint.quality
import id.zelory.compressor.constraint.resolution
import id.zelory.compressor.constraint.size
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import javax.inject.Inject

@AndroidEntryPoint
class EditProfileActivity :
    BaseActivity<ActivityEditProfileBinding, EditProfileViewModel>(R.layout.activity_edit_profile) {

    @Inject
    lateinit var session: Session
    private var username: String? = null
    private var userPhone: String? = null
    private var filePhoto: File? = null

    @Inject
    lateinit var userDao: UserDao


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.statusBarColor = ContextCompat.getColor(this, R.color.white)

        getUser()
        observe()

        binding.btnBack.setOnClickListener {
            onBackPressed()
        }

        binding.btnConfirm.setOnClickListener {
            validateForm()
            if (filePhoto == null){
                updateProfile()
            } else{
                updateProfilePhoto()
            }
        }

        binding.btnProfile.setOnClickListener {
            if (checkPermissionGallery()) {
                openGallery()
            } else {
                requestPermissionGallery()

            }
        }

    }

    private fun updateProfilePhoto() {
        val name = binding.tvName.textOf()
        val phone = binding.etPhone.textOf()

        lifecycleScope.launch {
                val compressesFile = compressFile(filePhoto!!)
                Log.d("Compress", "File: $compressesFile")
                if (compressesFile != null) {
                    viewModel.updateUserPhoto(name, phone, compressesFile)
                }
            }

    }

    private fun updateProfile() {
        val name = binding.tvName.textOf()
        val phone = binding.etPhone.textOf()

        if (name == username && phone == userPhone) {
                Log.d("cekUserName", "cekUserName : $username")
                binding.root.snacked("tidak ada data yang berubah")
                return
            } else{
                viewModel.updateUser(name, phone)
            }

    }

    private fun getUser() {
//        val user = session.getUser()
        userDao.getUser().observe(this@EditProfileActivity) { userData ->
            binding.user = userData
            username = userData.name
            userPhone = userData.phoneNumber
        }
        Log.d("cek nama","$username")
    }

    private fun validateForm() {
        val name = binding.tvName.textOf()
        val phone = binding.etPhone.textOf()

        if (name.isEmpty()) {
            binding.tvName.isEmptyRequired(R.string.emptyRequired)
        }

        if (phone.isEmpty()) {
            binding.etPhone.isEmptyRequired(R.string.emptyRequired)
        }
    }

    private fun observe() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.apiResponse.collect {
                        when (it.status) {
                            ApiStatus.LOADING -> loadingDialog.show("Update Profile")
                            ApiStatus.SUCCESS -> {
                                tos("Berhasil Update Profile")
                                loadingDialog.dismiss()
                                finish()
                            }
                            ApiStatus.ERROR -> {
                                disconnect(it)
                                loadingDialog.setResponse(it.message ?: return@collect)

                            }
                            ApiStatus.EXPIRED -> {
                                loadingDialog.setResponse(it.message ?: return@collect)

                            }
                            else -> loadingDialog.setResponse("Else")
                        }
                    }
                }
            }
        }
    }

    //MultiPart Gallery , photo not Done yet
    private var activityLauncherGallery =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            result.data?.data?.let {
                generateFileImage(it)
            }
        }

    //cek Permission Gallery
    private fun checkPermissionGallery(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            android.Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
    }

    // Request Permission Gallery
    private fun requestPermissionGallery() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
            110
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == 200) {
            if (grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                openGallery()
            } else {
                Toast.makeText(this, "Ijin gallery ditolak", Toast.LENGTH_SHORT).show()
            }
        }

    }


    // function Open Gallery
    private fun openGallery() {
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        activityLauncherGallery.launch(galleryIntent)
    }


    // Generate Image File
    private fun generateFileImage(uri: Uri) {
        try {
            val parcelFileDescriptor = contentResolver.openFileDescriptor(uri, "r")
            val fileDescriptor = parcelFileDescriptor?.fileDescriptor
            val image = BitmapFactory.decodeFileDescriptor(fileDescriptor)
            parcelFileDescriptor?.close()

            val orientation = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                getOrientation2(uri)
            } else {
                getOrientation(uri)
            }
            val file = if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
                createImageFile()
            } else {
                //File("${Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)}" + File.separator + "BurgerBangor", getNewFileName())
                File(externalCacheDir?.absolutePath, getNewFileName())
            }

            val fos = FileOutputStream(file)
            var bitmap = image

            if (orientation != -1 && orientation != 0) {

                val matrix = Matrix()
                when (orientation) {
                    6 -> matrix.postRotate(90f)
                    3 -> matrix.postRotate(180f)
                    8 -> matrix.postRotate(270f)
                    else -> matrix.postRotate(orientation.toFloat())
                }
                bitmap =
                    Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
            }

            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
            binding.ivProfile.setImageBitmap(bitmap)
            filePhoto = file
            Log.d("checkfile", "file : $filePhoto")
        } catch (e: Exception) {
            e.printStackTrace()
            binding.root.snacked("File ini tidak dapat digunakan")
        }
    }

    @SuppressLint("Range")
    private fun getOrientation(shareUri: Uri): Int {
        val orientationColumn = arrayOf(MediaStore.Images.Media.ORIENTATION)
        val cur = contentResolver.query(
            shareUri,
            orientationColumn,
            null,
            null,
            null
        )
        var orientation = -1
        if (cur != null && cur.moveToFirst()) {
            if (cur.columnCount > 0) {
                orientation = cur.getInt(cur.getColumnIndex(orientationColumn[0]))
            }
            cur.close()
        }
        return orientation
    }

    @SuppressLint("NewApi")
    private fun getOrientation2(shareUri: Uri): Int {
        val inputStream = contentResolver.openInputStream(shareUri)
        return getOrientation3(inputStream)
    }

    @SuppressLint("NewApi")
    private fun getOrientation3(inputStream: InputStream?): Int {
        val exif: ExifInterface
        var orientation = -1
        inputStream?.let {
            try {
                exif = ExifInterface(it)
                orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 0)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return orientation
    }

    //function Create Image
    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp = DateTimeHelper().createAtLong().toString()
        val storageDir =
            getAppSpecificAlbumStorageDir(Environment.DIRECTORY_DOCUMENTS, "Attachment")
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        )
    }

    //Get Image File Name
    private fun getNewFileName(isPdf: Boolean = false): String {
        val timeStamp = DateTimeHelper().createAtLong().toString()
        return if (isPdf) "PDF_${timeStamp}_.pdf" else "JPEG_${timeStamp}_.jpg"
    }

    //Get Image Album Storage
    private fun getAppSpecificAlbumStorageDir(albumName: String, subAlbumName: String): File {
        // Get the pictures directory that's inside the app-specific directory on
        // external storage.
        val file = File(getExternalFilesDir(albumName), subAlbumName)
        if (!file.mkdirs()) {
            //Log.e("fssfsf", "Directory not created")
        }
        return file
    }

    private suspend fun compressFile(filePhoto: File): File? {
        println("Compress 1")
        return try {
            println("Compress 2")
            Compressor.compress(this, filePhoto) {
                resolution(720, 720)
                quality(50)
                format(Bitmap.CompressFormat.JPEG)
                size(514)
            }
        } catch (e: Exception) {
            println("Compress 3")
            tos("Gagal kompress")
            e.printStackTrace()
            null
        }

    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (filePhoto != null) {
            unsavedAlert()
            return
        }
        if (binding.tvName.textOf().isNotEmpty() && binding.tvName.textOf() != username) {
            unsavedAlert()
            return
        }
        if (binding.etPhone.textOf().isNotEmpty() && binding.etPhone.textOf() != userPhone) {
            unsavedAlert()
            return
        }
        finish()
    }

    private fun unsavedAlert() {
        val builder = AlertDialog.Builder(this@EditProfileActivity)
        builder.setTitle("Unsaved Changes")
        builder.setMessage("You have unsaved changes. Are you sure you want to Dismiss changes?.")
            .setPositiveButton("Dismiss") { _, _ ->
                this@EditProfileActivity.finish()
            }
            .setNegativeButton("Keep Editing") { dialog, _ ->
                dialog.dismiss()
            }
        val dialog: AlertDialog = builder.create()

        // Set the color of the positive button text
        dialog.setOnShowListener {
            dialog.getButton(DialogInterface.BUTTON_POSITIVE)
                .setTextColor(ContextCompat.getColor(this, com.crocodic.core.R.color.text_red))
            dialog.getButton(DialogInterface.BUTTON_NEGATIVE)
                .setTextColor(ContextCompat.getColor(this, R.color.black))
        }
        dialog.show()

    }


}