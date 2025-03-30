package com.example.project

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.project.dataAplication.Companion.prefs
import com.google.firebase.auth.FirebaseAuth

class AdminMain : AppCompatActivity() {
    // Instancia de Firebase Authentication para manejar el inicio/cierre de sesión
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_main)

        // Inicializar Firebase Authentication
        auth = FirebaseAuth.getInstance()

        val btnSubjectManagement = findViewById<Button>(R.id.btnSubjectManagement)
        val btnUserManagement = findViewById<Button>(R.id.btnUserManagement)
        val btnLogout = findViewById<Button>(R.id.btnlo) // Usando el ID correcto del botón Log Out

        // Botón para Gestión de Asignaturas
        btnSubjectManagement.text = "Subject Management"
        btnSubjectManagement.setOnClickListener {
            val intent = Intent(this, ModifySubjects::class.java)
            startActivity(intent)
        }

        // Botón para Gestión de Usuarios
        btnUserManagement.text = "User Management"
        btnUserManagement.setOnClickListener {
            val intent = Intent(this, ModifyUsers::class.java)
            startActivity(intent)
        }

        // Botón para Cerrar Sesión
        btnLogout.setOnClickListener {
            auth.signOut() // Cerrar sesión en Firebase
            prefs.wipe() // Borrar datos de SharedPreferences
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}