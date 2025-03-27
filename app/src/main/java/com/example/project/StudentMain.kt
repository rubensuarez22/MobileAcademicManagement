package com.example.project

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class StudentMain : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_student_main)
        ImplementationQR(this).init()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val btnGradesStudent = findViewById<Button>(R.id.btnGradeStudent)
        val btnLogOut = findViewById<Button>(R.id.btnLogOut)

        btnGradesStudent.setOnClickListener {
            startActivity(Intent(this, GradesStudent::class.java))
            finish()
        }

        btnLogOut.setOnClickListener{
            startActivity(Intent(this, MainActivity::class.java))
        }
    }
}