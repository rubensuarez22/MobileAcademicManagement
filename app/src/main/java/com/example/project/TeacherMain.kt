package com.example.project

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class TeacherMain : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_teacher_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val btnGrades = findViewById<Button>(R.id.btnGradesTeacher)
        val btnQr = findViewById<Button>(R.id.btnQRTeacher)
        val btnLogOut = findViewById<Button>(R.id.btnLogOutTeacher)

        btnGrades.setOnClickListener {
            startActivity(Intent(this, TeacherGrades::class.java))
        }

        btnQr.setOnClickListener {

        }

        btnLogOut.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }
    }
}