package com.example.project

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.project.dataAplication.Companion.prefs
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    // Instancia de Firebase Firestore para manejar la base de datos en la nube
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Inicializamos FirebaseAuth
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        checkUserValues()

        val etEmail = findViewById<EditText>(R.id.etEmail)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val btnRegister = findViewById<Button>(R.id.btnRegister)
        val btnLogIn = findViewById<Button>(R.id.btnLogIn)

        // Botón para iniciar sesión
        btnLogIn.setOnClickListener {
            // Se convierten en String los apartados de email y contraseña
            val email = etEmail.text.toString()
            val password = etPassword.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                signIn(email, password)
            } else {
                Toast.makeText(this, "Ingrese correo y contraseña", Toast.LENGTH_SHORT).show()
            }
        }

        // Botón para registrar un nuevo usuario
        btnRegister.setOnClickListener {
            // Se convierten en String los apartados de email y contraseña
            val email = etEmail.text.toString()
            val password = etPassword.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                register(email, password)
            } else {
                Toast.makeText(this, "Ingrese correo y contraseña", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun checkUserValues() {
        val nombre = prefs.getNombre()
        val rol = prefs.getRol()

        if (nombre?.isNotEmpty() == true && rol?.isNotEmpty() == true) {
            when (rol) {
                "0" -> goToStudentMain()
                "1" -> goToTeacherMain()
                "2" -> goToAdminMain()
                else -> Toast.makeText(this, "Rol no válido o no encontrado", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Función para iniciar sesión
    private fun signIn(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show()
                    val user = auth.currentUser
                    if (user != null) {
                        loadUserData(user.uid) { userData ->
                            if (userData != null) {
                                prefs.saveNombre(userData["name"] as? String ?: "")
                                prefs.saveApellido(userData["apellido"] as? String ?: "")
                                prefs.saveMatricula(userData["matricula"] as? String ?: "")
                                prefs.saveRol(userData["rol"] as? String ?: "")

                                when (userData["rol"] as? String) {
                                    "0" -> goToStudentMain()
                                    "1" -> goToTeacherMain()
                                    "2" -> goToAdminMain()
                                    else -> Toast.makeText(this, "Rol no válido o no encontrado", Toast.LENGTH_SHORT).show()
                                }
                                finish()
                            } else {
                                Toast.makeText(this, "Error al cargar los datos del usuario", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                } else {
                    Toast.makeText(this, "Error: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    // Función para registrar un usuario nuevo
    private fun register(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Registro exitoso", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, Registry::class.java))
                    finish()
                } else {
                    Toast.makeText(this, "Error: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun loadUserData(userId: String, onResult: (Map<String, Any>?) -> Unit) {
        db.collection("users").document(userId).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    onResult(document.data) // Pasar el mapa de datos completo
                } else {
                    onResult(null)
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error al cargar datos", Toast.LENGTH_SHORT).show()
                onResult(null)
            }
    }
    private fun goToStudentMain() {
        startActivity(Intent(this, StudentMain::class.java))
        finish()
    }

    private fun goToTeacherMain() {
        startActivity(Intent(this, TeacherMain::class.java))
        finish()
    }

    private fun goToAdminMain() {
        startActivity(Intent(this, AdminMain::class.java))
        finish()
    }

}