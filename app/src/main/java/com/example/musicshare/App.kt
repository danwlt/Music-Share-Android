package com.example.musicshare

import android.app.Application

class App : Application() {
    companion object {
        lateinit var instance: App
    }

    var username: String = ""

    override fun onCreate() {
        super.onCreate()
        instance = this
    }
}