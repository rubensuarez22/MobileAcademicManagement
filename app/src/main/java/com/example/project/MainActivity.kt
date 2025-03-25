package com.example.project

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
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

    // Función para iniciar sesión
    private fun signIn(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show()
                    // Obtener el usuario actualmente autenticado en Firebase
                    val user = auth.currentUser
                    // Si el usuario está autenticado, mostramos su correo y cargamos sus datos
                    if (user != null) {
                        loadUserData(user.uid) { rol ->
                            Log.e("TAG", "Rol obtenido: $rol")
                            when (rol) {
                                "0" -> startActivity(Intent(this, StudentMain::class.java))
                                "1" -> startActivity(Intent(this, TeacherMain::class.java))
                                "2" -> startActivity(Intent(this, AdminMain::class.java))
                                else -> Toast.makeText(this, "Rol no válido o no encontrado", Toast.LENGTH_SHORT).show()
                            }
                            finish()
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

    private fun loadUserData(userId: String, onResult: (String?) -> Unit) {
        db.collection("users").document(userId).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val rol = document.getString("rol")
                    onResult(rol)
                } else {
                    onResult(null)
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error al cargar datos", Toast.LENGTH_SHORT).show()
                onResult(null)
            }
    }
}