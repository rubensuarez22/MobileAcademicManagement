package com.example.project

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.project.dataAplication.Companion.prefs
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class StudentMain : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ClassAdapter
    private lateinit var classList: ArrayList<ClassItem>
    private lateinit var btnLogout: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_student_main)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        recyclerView = findViewById(R.id.rvClasses)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)

        classList = ArrayList()
        adapter = ClassAdapter(classList)
        recyclerView.adapter = adapter

        btnLogout = findViewById(R.id.btnLogout)
        btnLogout.setOnClickListener {
            auth.signOut() // Cerrar sesión en Firebase
            prefs.wipe() // Borrar datos de SharedPreferences
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        loadEnrolledSubjects()
    }

    private fun loadEnrolledSubjects() {
        val currentUser = auth.currentUser
        currentUser?.let { user ->
            val userId = user.uid

            db.collection("subjects")
                .whereArrayContains("enrolledStudents", userId)
                .get()
                .addOnSuccessListener { querySnapshot ->
                    classList.clear()
                    for (document in querySnapshot) {
                        val className = document.getString("name") ?: ""
                        val description = document.getString("description") ?: ""
                        val grade = ""
                        val classItem = ClassItem(className, grade, description)
                        classList.add(classItem)
                    }
                    adapter.notifyDataSetChanged()
                }
                .addOnFailureListener { e ->
                    Log.w("StudentMain", "Error getting documents: ", e)
                }
        }
    }
}