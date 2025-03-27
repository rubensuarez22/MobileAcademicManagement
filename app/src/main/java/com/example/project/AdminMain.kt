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

        val newClass = findViewById<Button>(R.id.btnNewClass)
        val addStudent = findViewById<Button>(R.id.btnAddStudent)
        val modUser = findViewById<Button>(R.id.btnModifyUsers)
        val btnLogOut = findViewById<Button>(R.id.btnlo)


        newClass.setOnClickListener {
            val intent = Intent(this, CreateNewClass::class.java)
            startActivity(intent)
        }

        // Botón para Agregar Usuario
        addStudent.setOnClickListener {
            val intent = Intent(this, AddStudets::class.java)
            startActivity(intent)
        }

        // Botón para Modificar Usuario
        modUser.setOnClickListener {
            val intent = Intent(this, ModifyUsers::class.java)
            startActivity(intent)
        }

        // Botón para Cerrar Sesion
        btnLogOut.setOnClickListener {
            auth.signOut() // Cerrar sesión en Firebase
            prefs.wipe() // Borrar datos de SharedPreferences
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}