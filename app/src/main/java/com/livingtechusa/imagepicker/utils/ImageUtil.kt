package com.livingtechusa.imagepicker.utils

import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.core.app.ActivityCompat
import java.io.File
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.Date


class ImageUtil {
    companion object {
        val MEDIA_TYPE_IMAGE = 1
        val MEDIA_TYPE_VIDEO = 3
    }

    private val REQUEST_EXTERNAL_STORAGE = 1
    private val permissionStorage = arrayOf<String>(
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_EXTERNAL_STORAGE
    )

    fun verifyStoragePermission(activity: Activity?) {
        val permissionWrite = ActivityCompat.checkSelfPermission(
            activity!!,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        val permissionRead =
            ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE)
        if (permissionWrite != PackageManager.PERMISSION_GRANTED || permissionRead != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                activity,
                permissionStorage,
                REQUEST_EXTERNAL_STORAGE
            )
        }
    }
//
//    private fun saveMedia(fileName: String, fileType: String): Uri? {
//        val tag = "Save_Image"
//        val fos: OutputStream?
//        try {
//            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.Q) {
//                val resolver: ContentResolver = ImagePickerApplication().getApplicationInstance().contentResolver
//                val contentValues = ContentValues()
//                contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, "$fileName") // "$fileName.jpg")
//                contentValues.put(MediaStore.MediaColumns.MIME_TYPE, fileType)  // "image/jpg")
//                val imageUri =
//                    resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
//                fos = imageUri?.let { resolver.openOutputStream(it) }
//                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
//                return imageUri
//            }
//        } catch (e: Exception) {
//            Log.e(tag, "Error: " + e.message + " with cause " + e.cause)
//        }
//        return null
//    }

    /** Create a file Uri for saving an image or video */
    fun getOutputMediaFileUri(type: Int): Uri {
        return Uri.fromFile(getOutputMediaFile(type))
    }

    /** Create a File for saving an image or video */
    fun getOutputMediaFile(type: Int): File? {
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        val mediaStorageDir = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
            "ImagePicker"
        )

        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        mediaStorageDir.apply {
            if (!exists()) {
                if (!mkdirs()) {
                    Log.d("ImagePicker", "failed to create directory")
                    return null
                }
            }
        }

        // Create a media file name
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        return when (type) {
            MEDIA_TYPE_IMAGE -> {
                File("${mediaStorageDir.path}${File.separator}IMG_$timeStamp.jpg")
            }

            MEDIA_TYPE_VIDEO -> {
                File("${mediaStorageDir.path}${File.separator}VID_$timeStamp.mp4")
            }

            else -> null
        }
    }
}