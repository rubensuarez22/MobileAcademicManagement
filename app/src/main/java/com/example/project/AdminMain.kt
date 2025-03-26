package com.example.project

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth

class AdminMain : AppCompatActivity() {
    // Instancia de Firebase Authentication para manejar el inicio/cierre de sesión
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_main)

        // Inicializar Firebase Authentication
        auth = FirebaseAuth.getInstance()

        val newClass = findViewById<Button>(R.id.btnNewClass)
        val addStudent = findViewById<Button>(R.id.btnAddStudent)
        val modUser = findViewById<Button>(R.id.btnModifyUsers)
        val btnLogOut = findViewById<Button>(R.id.btnlo)


        newClass.setOnClickListener {
            val intent = Intent(this, CreateNewClass::class.java)
            startActivity(intent)
        }

        // Botón para iniciar sesión
        addStudent.setOnClickListener {
            val intent = Intent(this, AddStudets::class.java)
            startActivity(intent)
        }

        // Botón para iniciar sesión
        modUser.setOnClickListener {
            val intent = Intent(this, ModifyUsers::class.java)
            startActivity(intent)
        }

        // Botón para iniciar sesión
        btnLogOut.setOnClickListener {
            auth.signOut() // Cerrar sesión en Firebase
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }
}