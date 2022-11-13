package com.livingtechusa.imagepicker

import android.app.Application

class ImagePickerApplication: Application() {
    private lateinit var appInstance: ImagePickerApplication
    fun getApplicationInstance(): ImagePickerApplication {
        if (appInstance == null) {
            appInstance = ImagePickerApplication()
        }
        return appInstance
    }
}