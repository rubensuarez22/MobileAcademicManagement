package com.example.project

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.project.dataAplication.Companion.prefs
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class Registry : AppCompatActivity() {
    // Instancia de Firebase Authentication para manejar el inicio/cierre de sesión
    private lateinit var auth: FirebaseAuth

    // Instancia de Firebase Firestore para manejar la base de datos en la nube
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registry)

        // Inicializar Firebase Authentication y Firestore
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        val rol = "0"
        val Nombre = findViewById<EditText>(R.id.Nombre)
        val Apellido = findViewById<EditText>(R.id.Apellido)
        val Matricula = findViewById<EditText>(R.id.Matricula)
        val Facultad = findViewById<EditText>(R.id.Facultad)
        val Sexo = findViewById<EditText>(R.id.Sexo)
        val btnSave = findViewById<Button>(R.id.btnSave) // Botón para guardar datos

        // Obtener el usuario actualmente autenticado en Firebase
        val user = auth.currentUser

        // Botón para guardar datos en Firebase Firestore
        btnSave.setOnClickListener {
            val name = Nombre.text.toString()
            val apellido = Apellido.text.toString()
            val matricula = Matricula.text.toString()
            val facultad = Facultad.text.toString()
            val sexo = Sexo.text.toString()

            // Validar que los campos no estén vacíos antes de guardar
            if (name.isNotEmpty() && apellido.isNotEmpty() && matricula.isNotEmpty() && facultad.isNotEmpty() && sexo.isNotEmpty()) {
                saveUserData(user?.uid, name, apellido, matricula, facultad, sexo, rol) // Guardar datos en Firestore
                startActivity(Intent(this, StudentMain::class.java)) // Regresar a la pantalla de inicio
                finish() // Finalizar la actividad actual
            } else {
                Toast.makeText(this, "Complete todos los campos", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // 📌 Función para guardar los datos del usuario en Firestore
    private fun saveUserData(userId: String?, name: String, apellido: String, matricula: String, facultad: String, sexo: String, rol: String) {
        // Si el ID del usuario es nulo, no se puede guardar la información
        if (userId == null) return

        // Se crea un mapa con los datos del usuario
        val user = hashMapOf(
            "name" to name,
            "apellido" to apellido,
            "matricula" to matricula,
            "facultad" to facultad,
            "sexo" to sexo,
            "rol" to rol
        )

        // Se guarda la información en Firestore dentro de la colección "users"
        db.collection("users").document(userId).set(user)
            .addOnSuccessListener {
                Toast.makeText(this, "Datos guardados exitosamente", Toast.LENGTH_SHORT).show()
                // --- Guardar en SharedPreferences después del registro ---
                prefs.saveNombre(name)
                prefs.saveApellido(apellido)
                prefs.saveMatricula(matricula)
                prefs.saveRol(rol)
                // --- Fin de guardado en SharedPreferences ---
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error al guardar", Toast.LENGTH_SHORT).show()
            }
    }
}