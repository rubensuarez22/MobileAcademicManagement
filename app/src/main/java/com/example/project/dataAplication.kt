package com.example.project // Asegúrate de usar el nombre de paquete de tu aplicación

import android.app.Application

class dataAplication: Application() {

    companion object {
        lateinit var prefs: Prefs
    }

    override fun onCreate() {
        super.onCreate()
        prefs = Prefs(applicationContext)
    }
}