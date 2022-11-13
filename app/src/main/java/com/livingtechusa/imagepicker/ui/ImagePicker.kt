package com.livingtechusa.imagepicker.ui

import android.content.Context
import android.net.Uri
import android.os.Environment
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import com.livingtechusa.imagepicker.utils.ImageUtil
import com.livingtechusa.imagepicker.utils.ImageUtil.Companion.MEDIA_TYPE_IMAGE
import com.livingtechusa.imagepicker.utils.ImageUtil.Companion.MEDIA_TYPE_VIDEO

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun ImagePicker(
    modifier: Modifier = Modifier,
    context: Context
) {

    var hasImage by remember {
        mutableStateOf(false)
    }

    var imageUri by remember {
        mutableStateOf<Uri?>(null)
    }

    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri ->
            hasImage = uri != null
            imageUri = uri
        }
    )

    val cameraImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
        onResult = { success ->
            hasImage = success
        }
    )

    var hasVideo by remember {
        mutableStateOf(false)
    }

    var videoUri by remember {
        mutableStateOf<Uri?>(null)
    }

    val videoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri ->
            hasVideo = uri != null
            videoUri = uri
        }
    )

    val cameraVideoLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CaptureVideo(),
        onResult = { success ->
            hasVideo = success
        }
    )
    var visible by rememberSaveable { mutableStateOf(false) }

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
                        imagePicker.launch("image/*")
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
                        // check Environment.getExternalStorageState()
                        val uri = getImageURI(context = context, MEDIA_TYPE_IMAGE)     // ImageUtil().getOutputMediaFileUri(MEDIA_TYPE_IMAGE)     //ComposeFileProvider.getImageURI(context)
                        imageUri = uri
                        cameraImageLauncher.launch(uri)
                        hasImage = imageUri != null
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
                        videoPicker.launch("video/*")
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
                        val uri = getImageURI(context = context, MEDIA_TYPE_VIDEO)     //ImageUtil().getOutputMediaFileUri(MEDIA_TYPE_VIDEO)  //ComposeFileProvider.getImageURI(context)
                        videoUri = uri
                        cameraVideoLauncher.launch(uri)
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






