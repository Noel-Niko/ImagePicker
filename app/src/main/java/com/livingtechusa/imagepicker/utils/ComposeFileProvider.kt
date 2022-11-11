package com.livingtechusa.imagepicker.utils

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import com.livingtechusa.imagepicker.R
import java.io.File

class ComposeFileProvider: FileProvider (
    R.xml.file_paths
) {
    companion object {
        fun getImageURI(context: Context): Uri {
            val directory = File(context.cacheDir, "images")
            directory.mkdirs()

            val file = File.createTempFile(
                "selected_image_",
                ".jpg",
                directory
            )

            val authority = "com.livingtechusa.imagepicker.utils.ComposeFileProvider"

            return getUriForFile(
                context,
                authority,
                file
            )
        }
    }
}

