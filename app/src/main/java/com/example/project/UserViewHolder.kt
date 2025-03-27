package com.example.project

import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

class UserViewHolder(view: View, private val adapter: UserAdapter) : RecyclerView.ViewHolder(view) {

    val userImageView: ImageView = view.findViewById(R.id.userImageView)
    val userNameTextView: TextView = view.findViewById(R.id.userNameTextView)
    val userRoleTextView: TextView = view.findViewById(R.id.userRoleTextView)
    val btnSetTeacherRole: Button = view.findViewById(R.id.btnSetTeacherRole)
    val btnSetStudentRole: Button = view.findViewById(R.id.btnSetStudentRole)

    private val db = FirebaseFirestore.getInstance()

    private lateinit var currentUser: User

    fun bind(user: User) {
        currentUser = user // Guardamos el usuario actual para usarlo en los listeners

        userNameTextView.text = user.name
        updateRoleTextView(user.role)

        // Por ahora no cargamos imágenes

        btnSetTeacherRole.setOnClickListener {
            currentUser.userId?.let { userId ->
                updateUserRole(userId, "1") // 1 para Teacher
            }
        }

        btnSetStudentRole.setOnClickListener {
            currentUser.userId?.let { userId ->
                updateUserRole(userId, "2") // 2 para Student
            }
        }
    }

    private fun updateRoleTextView(role: String) {
        userRoleTextView.text = when (role) {
            "0" -> "Admin User"
            "1" -> "Teacher"
            "2" -> "Student"
            else -> "Unknown Role"
        }
    }

    private fun updateUserRole(userId: String, newRole: String) {
        db.collection("users") // Asegúrate de que tu colección de usuarios se llama "users"
            .document(userId)
            .update("rol", newRole)
            .addOnSuccessListener {
                // Actualizar la lista local y notificar al adapter
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val user = adapter.userList[position]
                    user.role = newRole
                    adapter.notifyItemChanged(position)
                }
                println("Rol de usuario actualizado a $newRole")
            }
            .addOnFailureListener { e ->
                println("Error al actualizar el rol del usuario: $e")
                // Puedes mostrar un mensaje de error al usuario
            }
    }
}