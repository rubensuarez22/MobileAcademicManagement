package com.example.project

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnLogIn = findViewById<Button>(R.id.btnLogIn)

        btnLogIn.setOnClickListener{
            val intent = Intent(this@MainActivity, StudentMain::class.java)
            startActivity(intent)

        }
    }
}