package com.example.project

import android.app.AlertDialog
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
        currentUser = user

        userNameTextView.text = user.name
        updateRoleTextView(user.role)

        btnSetTeacherRole.setOnClickListener {
            currentUser.userId?.let { userId ->
                showRoleConfirmationDialog(userId, "1", "Teacher")
            }
        }

        btnSetStudentRole.setOnClickListener {
            currentUser.userId?.let { userId ->
                showRoleConfirmationDialog(userId, "0", "Student")
            }
        }
    }

    private fun updateRoleTextView(role: String) {
        userRoleTextView.text = when (role) {
            "2" -> "Admin User"
            "1" -> "Teacher"
            "0" -> "Student"
            else -> "Unknown Role"
        }
    }

    // ✅ Confirmation Dialog
    private fun showRoleConfirmationDialog(userId: String, newRole: String, roleName: String) {
        AlertDialog.Builder(itemView.context)
            .setTitle("Change Role")
            .setMessage("Are you sure you want to set ${currentUser.name} as $roleName?")
            .setPositiveButton("Yes") { _, _ ->
                updateUserRole(userId, newRole)
            }
            .setNegativeButton("No", null)
            .show()
    }

    private fun updateUserRole(userId: String, newRole: String) {
        db.collection("users")
            .document(userId)
            .update("rol", newRole)
            .addOnSuccessListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val user = adapter.userList[position]
                    user.role = newRole
                    adapter.notifyItemChanged(position)
                }
                println("User role updated to $newRole")
            }
            .addOnFailureListener { e ->
                println("Error updating user role: $e")
            }
    }
}
