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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import com.livingtechusa.imagepicker.utils.ComposeFileProvider.Companion.getImageURI
import androidx.lifecycle.viewmodel.compose.viewModel
import com.livingtechusa.imagepicker.utils.ImageUtil.Companion.MEDIA_TYPE_VIDEO
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
    val capturedMedia by viewModel.capturedMedia.observeAsState()
    val addedMedia by viewModel.addedMedia.observeAsState()


    val scope = rememberCoroutineScope()
    var targetImageUri by rememberSaveable { mutableStateOf<Uri?>(null) }
    var targetVideoUri by rememberSaveable { mutableStateOf<Uri?>(null) }

    var hasImage by remember {
        mutableStateOf(false)
    }

    var imageUri by remember {
        mutableStateOf<Uri?>(null)
    }

    val selectImage = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri ->
            hasImage = uri != null
            imageUri = uri
        }
    )

    val takePicture = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()) { _ ->
        targetImageUri?.let { uri ->
            viewModel.onImageCapture(uri)
            targetImageUri = null
            imageUri = uri
        }
    }

    var hasVideo by remember {
        mutableStateOf(false)
    }

    var videoUri by remember {
        mutableStateOf<Uri?>(null)
    }

    val selectVideo = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri ->
            hasVideo = uri != null
            videoUri = uri
        }
    )

    val takeVideo = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CaptureVideo() ) { _ ->
        targetVideoUri?.let { uri ->
            viewModel.onVideoCapture(uri)
            targetVideoUri = null
            videoUri = uri
        }

    }
    var visible by rememberSaveable { mutableStateOf(false) }

    Box (
        mod
            )
    val imageLoader = ImageLoader.Builder(context)
        .components {
            add(VideoFrameDecoder.Factory())
        }.crossfade(true)
        .build()
    val painter = rememberAsyncImagePainter(
        model = videoUri,
        imageLoader = imageLoader
    )

    Box(
        modifier = modifier.fillMaxSize(),
    ) {
        if (imageUri != null) {
            AsyncImage(
                model = imageUri,
                modifier = Modifier.fillMaxWidth(),
                contentDescription = "Selected image",
            )
        }
        if (videoUri != null) {
            Image(
                painter = painter,
                contentDescription = "Selected Video Thumbnail",
                contentScale = ContentScale.Crop,
                alignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            )
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
                        selectImage.launch("image/*")
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
                    modifier = Modifier.padding(top = 16.dp),
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
                    modifier = Modifier.padding(top = 16.dp),
                    onClick = {
                        scope.launch {
                            viewModel.createVideoUri()?.let { uri ->
                                targetVideoUri = uri
                                takeVideo.launch(uri)
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






