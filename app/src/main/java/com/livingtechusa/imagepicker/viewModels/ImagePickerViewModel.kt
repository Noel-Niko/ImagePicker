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

    companion object {
        private val TAG = this::class.java.simpleName
        const val PHOTO_KEY = "IMAGE"
        const val VIDEO_KEY = "VIDEO"
    }


    private val context: Context
        get() = getApplication()

    val canWriteInMediaStore: Boolean
        get() = MediaStoreUtils.canWriteInMediaStore(context)

    private val _errorFlow = MutableSharedFlow<String>()
    val errorFlow: SharedFlow<String> = _errorFlow

    /**
     * We keep the current media [Uri] in the savedStateHandle to re-render it if there is a
     * configuration change and we expose it as a [LiveData] to the UI
     */
    val photo: LiveData<FileResource?> =
        savedStateHandle.getLiveData<FileResource?>(PHOTO_KEY)

    val video: LiveData<FileResource?> =
        savedStateHandle.getLiveData<FileResource?>(VIDEO_KEY)

    @Composable
    fun selectImage() {
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.GetContent(),
            onResult = { uri ->
                savedStateHandle[PHOTO_KEY] = uri
            }
        )
    }

    @Composable
    fun selectVideo() = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri ->
            savedStateHandle[VIDEO_KEY] = uri
        }
    )

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

    fun onImageCapture(uri: Uri) {
        viewModelScope.launch {
            MediaStoreUtils.scanUri(context, uri, "image/jpg")
            savedStateHandle[PHOTO_KEY] = MediaStoreUtils.getResourceByUri(context, uri)
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

    fun onVideoCapture(uri: Uri) {
        viewModelScope.launch {
            MediaStoreUtils.scanUri(context, uri, "video/mp4")
            savedStateHandle[VIDEO_KEY] = MediaStoreUtils.getResourceByUri(context, uri)
        }
    }

}