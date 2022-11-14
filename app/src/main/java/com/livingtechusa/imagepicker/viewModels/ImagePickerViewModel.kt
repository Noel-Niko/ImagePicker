package com.livingtechusa.imagepicker.viewModels

import android.app.Application
import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.livingtechusa.imagepicker.R
import com.livingtechusa.imagepicker.utils.FileResource
import com.livingtechusa.imagepicker.utils.MediaStoreUtils
import com.livingtechusa.imagepicker.utils.MediaStoreUtils.scanUri
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import java.io.IOException

class ImagePickerViewModel(
    application: Application,
    private val savedStateHandle: SavedStateHandle
) : AndroidViewModel(application) {

    private val context: Context
        get() = getApplication()

    private val _errorFlow = MutableSharedFlow<String>()
    val errorFlow: SharedFlow<String> = _errorFlow

    suspend fun createImageUri(): Uri? {
        val filename = context.getString(R.string.app_name) + "${System.currentTimeMillis()}.jpg"
        val uri = MediaStoreUtils.createImageUri(context, filename)
        return if (uri != null) {
            uri
        } else {
            _errorFlow.emit("Couldn't create an image Uri\n$filename")
            null
        }
    }

    suspend fun createVideoUri(): Uri? {
        val filename = context.getString(R.string.app_name) + "${System.currentTimeMillis()}.mp4"
        val uri = MediaStoreUtils.createVideoUri(context, filename)
        return if (uri != null) {
            uri
        } else {
            _errorFlow.emit("Could not create a video Uri\n$filename")
            null
        }
    }
}