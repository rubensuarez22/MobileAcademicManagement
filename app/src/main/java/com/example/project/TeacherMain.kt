package com.example.project

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.project.dataAplication.Companion.prefs
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth // Import FirebaseAuth

class TeacherMain : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth // Inicializa FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_teacher_main)
        auth = FirebaseAuth.getInstance() // Inicializa FirebaseAuth
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val btnLogOut = findViewById<Button>(R.id.btnLogOutTeacher)
        btnLogOut.setOnClickListener {
            prefs.wipe()
            auth.signOut() // Cierra sesión en Firebase
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }
}