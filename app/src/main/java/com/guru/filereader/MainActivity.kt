package com.guru.filereader

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import androidx.core.content.FileProvider
import com.guru.filereader.utils.getPath
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.single.PermissionListener
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.io.IOException

class MainActivity : AppCompatActivity() {
    private val REQUEST_CAMERA = 1
    private val REQUEST_GALLERY = 2
    private val REQUEST_FILE = 3

    private var capturedImage: Uri? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnCamera.setOnClickListener {
            cameraClicked()
        }

        btnGallery.setOnClickListener {
            val pickIntent =
                Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(pickIntent, REQUEST_GALLERY)
        }
        btnFile.setOnClickListener {
            val fileIntent = Intent(Intent.ACTION_GET_CONTENT)
            fileIntent.addCategory(Intent.CATEGORY_OPENABLE)
            fileIntent.type = "*/*"
            startActivityForResult(fileIntent, REQUEST_FILE)
        }
    }

    private fun cameraClicked() {
        Dexter.withActivity(this)
            .withPermission(Manifest.permission.CAMERA)
            .withListener(object : PermissionListener {

                override fun onPermissionGranted(response: PermissionGrantedResponse?) {
                    capturedImage = setImageUri()

                    val takePhotoIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                    takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, capturedImage)
                    startActivityForResult(takePhotoIntent, REQUEST_CAMERA)
                }


                override fun onPermissionDenied(response: PermissionDeniedResponse?) {

                }

                override fun onPermissionRationaleShouldBeShown(
                    permission: com.karumi.dexter.listener.PermissionRequest?,
                    token: PermissionToken?
                ) {
                    token?.continuePermissionRequest()
                }
            }).check()
    }

    private fun setImageUri(): Uri? {
        var imageUri: Uri? = null
        try {
            val folder: File? = getExternalFilesDir(null)
            if (folder?.exists() == false)
                folder.mkdirs()
            val file = File(folder, "Image_Tmp1.jpg")
            if (file.exists()) file.delete()
            try {
                file.createNewFile()
            } catch (e: IOException) {
                e.printStackTrace()
            }
            val authprity: String =
                packageName + getString(R.string.file_provider_name)
            imageUri = FileProvider.getUriForFile(
                this, authprity,
                file
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return imageUri
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            var uri = capturedImage
            if (data != null && data.data != null)
                uri = data.data

            val path: String = getPath(uri)
            // val extension: String = getExtension(uri!!).toLowerCase()

            txtFilePath.text = path

        }
    }

}