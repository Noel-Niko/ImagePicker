package com.livingtechusa.imagepicker.utils

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import com.livingtechusa.imagepicker.BuildConfig
import com.livingtechusa.imagepicker.R
import java.io.File

class ComposeFileProvider: FileProvider (
    R.xml.provider_paths
) {
    companion object {
        fun getImageURI(context: Context, mediaType: Int): Uri {
//            val directory = File(context.cacheDir, "images")
//            directory.mkdirs()

            val file = ImageUtil().getOutputMediaFile(mediaType)!!
//                File.createTempFile(
//                "selected_image_",
//                ".jpg",
//                directory
//            )

            val authority = "com.livingtechusa.imagepicker.provider"

            return getUriForFile(
                context,
                authority,
                file
            )

        }
    }
}


