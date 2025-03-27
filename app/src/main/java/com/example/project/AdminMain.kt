package com.example.project

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class AdminMain : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_admin_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val btnCreateNewClass = findViewById<Button>(R.id.btnNewClass)
        val btnAddStudent= findViewById<Button>(R.id.btnAddStudent)
        val btnModifyUsers = findViewById<Button>(R.id.btnModifyUsers)

        btnCreateNewClass.setOnClickListener {
            startActivity(Intent(this, CreateNewClass::class.java))
        }

        btnAddStudent.setOnClickListener {
            startActivity(Intent(this, AddStudets::class.java))
        }

        btnModifyUsers.setOnClickListener {
            startActivity(Intent(this, ModifyUsers::class.java))
        }

    }
}