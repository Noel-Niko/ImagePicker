package com.livingtechusa.imagepicker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.livingtechusa.imagepicker.ui.ImagePicker
import com.livingtechusa.imagepicker.ui.theme.ImagePickerTheme
import com.livingtechusa.imagepicker.utils.ImageUtil

@OptIn(ExperimentalPermissionsApi::class)
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ImagePickerTheme {
                val permissionsState = rememberMultiplePermissionsState(
                    permissions = listOf(
                        android.Manifest.permission.RECORD_AUDIO,
                        android.Manifest.permission.CAMERA,
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        android.Manifest.permission.READ_EXTERNAL_STORAGE
                    )
                )
                val lifecycleOwner = LocalLifecycleOwner.current
                DisposableEffect(
                    key1 = lifecycleOwner,
                    effect = {
                        val observer = LifecycleEventObserver { _, event ->
                            if (event == Lifecycle.Event.ON_START) {
                                permissionsState.launchMultiplePermissionRequest()
                                ImageUtil().verifyStoragePermission(this@MainActivity)
                            }
                        }
                        lifecycleOwner.lifecycle.addObserver(observer)
                        onDispose {
                            lifecycleOwner.lifecycle.removeObserver(observer)
                        }
                    }
                )
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    permissionsState.permissions.forEach { permis ->
                        when (permis.permission) {
                            android.Manifest.permission.CAMERA -> {
                                when {
                                    permis.hasPermission -> {
                                        Text(text = "Camera Permission Granted")
                                    }

                                    permis.shouldShowRationale -> {
                                        Text(text = "Camera Permission  for taking Photo")
                                    }

                                    !permis.hasPermission && !permis.shouldShowRationale -> {
                                        Text(text = "Camera Permission Denied.Go To App settings for enabling")
                                    }
                                }
                            }

                            android.Manifest.permission.RECORD_AUDIO -> {
                                when {
                                    permis.hasPermission -> {
                                        Text(text = "Record Permission Granted")
                                    }

                                    permis.shouldShowRationale -> {
                                        Text(text = "Record Permission  for recording Voice")
                                    }

                                    !permis.hasPermission && !permis.shouldShowRationale -> {
                                        Text(text = "Record permission denied. Go to app settings for enabling")
                                    }
                                }
                            }
                            android.Manifest.permission.WRITE_EXTERNAL_STORAGE -> {
                                when {
                                    permis.hasPermission -> {
                                        Text(text = "Write permission granted.")
                                    }

                                    permis.shouldShowRationale -> {
                                        Text(text = "Write permission for saving media.")
                                    }

                                    !permis.hasPermission && !permis.shouldShowRationale -> {
                                        Text(text = "Write permission denied. Go to app settings for enabling")
                                    }
                                }
                            }
                            android.Manifest.permission.READ_EXTERNAL_STORAGE -> {
                                when {
                                    permis.hasPermission -> {
                                        Text(text = "Read permission granted.")
                                    }

                                    permis.shouldShowRationale -> {
                                        Text(text = "Read permission for accessing media.")
                                    }

                                    !permis.hasPermission && !permis.shouldShowRationale -> {
                                        Text(text = "Read permission denied. Go to app settings for enabling.")
                                    }
                                }
                            }
                        }
                    }
                }
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    ImagePicker(context = this)
                }
            }
        }
    }

}


@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    ImagePickerTheme {
        ImagePicker(context = LocalContext.current)
    }
}