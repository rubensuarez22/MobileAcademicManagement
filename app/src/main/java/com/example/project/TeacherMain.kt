package com.example.project

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Spinner
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

        val btnLogOut = findViewById<Button>(R.id.btnLogOutTeacher)
        val btnAssistance = findViewById<Button>(R.id.button7)
        val btnGrades = findViewById<Button>(R.id.button5)
        val spiClass = findViewById<Spinner>(R.id.spinner)
        val spiSchedule = findViewById<Spinner>(R.id.spinner3)


        btnLogOut.setOnClickListener {
            prefs.wipe()
            auth.signOut() // Cierra sesión en Firebase
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        btnAssistance.setOnClickListener {
            startActivity(Intent(this, attendance::class.java))
        }

        btnGrades.setOnClickListener {
            startActivity(Intent(this, TeacherGrades::class.java))
        }
    }
}