package com.livingtechusa.imagepicker.ui

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import coil.decode.VideoFrameDecoder
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import androidx.lifecycle.viewmodel.compose.viewModel
import com.livingtechusa.imagepicker.viewModels.ImagePickerViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun ImagePicker(
    modifier: Modifier = Modifier,
    context: Context,
    viewModel: ImagePickerViewModel = viewModel()
) {
    val scaffoldState = rememberScaffoldState()
    val error by viewModel.errorFlow.collectAsState(null)
    val scope = rememberCoroutineScope()
    var targetImageUri by rememberSaveable { mutableStateOf<Uri?>(null) }
    var targetVideoUri by rememberSaveable { mutableStateOf<Uri?>(null) }

    var showPhoto by rememberSaveable { mutableStateOf(false) }
    var showVideo by rememberSaveable { mutableStateOf(false) }

    fun showingPhoto() {
        showVideo = false
        showPhoto = true
    }

    fun showingVideo() {
        showVideo = true
        showPhoto = false
    }

    var photoUri by rememberSaveable {
        mutableStateOf<Uri?>(null)
    }

    val selectPhoto = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri ->
            photoUri = uri
            showingPhoto()
        }
    )

    val takePicture = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { _ ->
        targetImageUri?.let { uri ->
            viewModel.onImageCapture(uri)
            targetImageUri = null
            photoUri = uri
            showingPhoto()
        }
    }

    var videoUri by rememberSaveable {
        mutableStateOf<Uri?>(null)
    }

    val selectVideo = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri ->
            videoUri = uri
            showingVideo()
        }
    )

    val takeVideo = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CaptureVideo()
    ) { _ ->
        targetVideoUri?.let { uri ->
            viewModel.onVideoCapture(uri)
            targetVideoUri = null
            videoUri = uri
            showingVideo()
        }

    }

    val imageLoader = ImageLoader.Builder(context)
        .components {
            add(VideoFrameDecoder.Factory())
        }.crossfade(true)
        .build()

    val videoPainter = rememberAsyncImagePainter(
        model = videoUri,
        imageLoader = imageLoader
    )

    LaunchedEffect(error) {
        error?.let { scaffoldState.snackbarHostState.showSnackbar(it) }
    }

    Box(
        modifier = modifier.fillMaxSize(),
    ) {
        Row(
            modifier = Modifier.fillMaxSize()
        ) {
            if (photoUri != null && showPhoto) {
                AsyncImage(
                    model = photoUri,
                    modifier = Modifier.fillMaxWidth(),
                    contentDescription = "Selected image",
                )
            }
        }
        Row(
            modifier = Modifier.fillMaxSize()
        ) {
            if (videoUri != null && showVideo) {
                Image(
                    painter = videoPainter,
                    contentDescription = "Selected Video Thumbnail",
                    contentScale = ContentScale.Crop,
                    alignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Row(
                modifier = Modifier
                    .align(Alignment.Start)
                    .padding(start = 32.dp)
            ) {
                Button(
                    onClick = {
                         selectPhoto.launch("image/*")
                    },
                ) {
                    Text(
                        text = "Select Photo"
                    )
                }
            }
            Row(
                modifier = Modifier
                    .align(Alignment.Start)
                    .padding(start = 32.dp)
            ) {
                Button(
                    onClick = {
                        scope.launch {
                            viewModel.createImageUri()?.let { uri ->
                                targetImageUri = uri
                                takePicture.launch(uri)
                            }
                        }
                    },
                ) {
                    Text(
                        text = "Take photo"
                    )
                }
            }
            Row(
                modifier = Modifier
                    .align(Alignment.Start)
                    .padding(start = 32.dp)
            ) {
                Button(
                    onClick = {
                        selectVideo.launch("video/*")
                        showingVideo()
                    },
                ) {
                    Text(
                        text = "Select Video"
                    )
                }
            }
            Row(
                modifier = Modifier
                    .align(Alignment.Start)
                    .padding(start = 32.dp)
            ) {
                Button(
                    onClick = {
                        scope.launch {
                            viewModel.createVideoUri()?.let { uri ->
                                targetVideoUri = uri
                                takeVideo.launch(uri)
                                showingVideo()
                            }
                        }
                    },
                ) {
                    Text(
                        text = "Take Video"
                    )
                }
            }
        }
    }
}









