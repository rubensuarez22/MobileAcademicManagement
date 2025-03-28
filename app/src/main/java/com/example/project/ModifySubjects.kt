package com.example.project

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

interface OnEditSubjectClickListener {
    fun onEditClick(subject: Subject)
}

interface OnDeleteSubjectClickListener {
    fun onDeleteClick(subjectId: String)
}

class ModifySubjects : AppCompatActivity(), OnEditSubjectClickListener, OnDeleteSubjectClickListener {

    private lateinit var etSubjectName: EditText
    private lateinit var etSubjectCode: EditText
    private lateinit var etDescription: EditText
    private lateinit var rvAssignTeachers: RecyclerView
    private lateinit var rvEnrollStudents: RecyclerView
    private lateinit var rvExistingSubjects: RecyclerView
    private lateinit var btnAddSubject: Button

    private lateinit var teachersAdapter: TeachersAdapter
    private lateinit var studentsAdapter: StudentsAdapter
    private lateinit var subjectsAdapter: SubjectsAdapter

    private val teachersList = mutableListOf<User>()
    private val studentsList = mutableListOf<User>()
    private val subjectsList = mutableListOf<Subject>()

    private val db = FirebaseFirestore.getInstance()
    private var editingSubjectId: String? = null // Para rastrear la asignatura que se está editando

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_modify_subjects)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        etSubjectName = findViewById(R.id.etSubjectName)
        etSubjectCode = findViewById(R.id.etSubjectCode)
        etDescription = findViewById(R.id.etDescription)
        rvAssignTeachers = findViewById(R.id.rvAssignTeachers)
        rvEnrollStudents = findViewById(R.id.rvEnrollStudents)
        rvExistingSubjects = findViewById(R.id.rvExistingSubjects)
        btnAddSubject = findViewById(R.id.btnAddSubject)
        btnAddSubject.text = "Add Subject" // Establecer el texto inicial del botón

        rvAssignTeachers.layoutManager = LinearLayoutManager(this)
        rvEnrollStudents.layoutManager = LinearLayoutManager(this)
        rvExistingSubjects.layoutManager = LinearLayoutManager(this)

        teachersAdapter = TeachersAdapter(teachersList)
        studentsAdapter = StudentsAdapter(studentsList)
        subjectsAdapter = SubjectsAdapter(subjectsList, this, this) // Pasar los listeners

        rvAssignTeachers.adapter = teachersAdapter
        rvEnrollStudents.adapter = studentsAdapter
        rvExistingSubjects.adapter = subjectsAdapter

        loadTeachersFromFirebase()
        loadStudentsFromFirebase()
        loadExistingSubjectsFromFirebase()

        btnAddSubject.setOnClickListener {
            saveOrUpdateSubject()
        }
    }

    private fun loadTeachersFromFirebase() {
        db.collection("users")
            .whereEqualTo("rol", "1")
            .get()
            .addOnSuccessListener { result ->
                teachersList.clear()
                for (document in result) {
                    val user = User(
                        name = document.getString("name") ?: "",
                        role = (document.getString("rol")?.toIntOrNull() ?: -1).toString(),
                        userId = document.id
                    )
                    teachersList.add(user)
                }
                teachersAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                println("Error al cargar profesores: $exception")
            }
    }

    private fun loadStudentsFromFirebase() {
        db.collection("users")
            .whereEqualTo("rol", "0")
            .get()
            .addOnSuccessListener { result ->
                studentsList.clear()
                for (document in result) {
                    val user = User(
                        name = document.getString("name") ?: "",
                        role = (document.getString("rol")?.toIntOrNull() ?: -1).toString(),
                        userId = document.id
                    )
                    studentsList.add(user)
                }
                studentsAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                println("Error al cargar estudiantes: $exception")
            }
    }


    private fun loadExistingSubjectsFromFirebase() {
        db.collection("subjects")
            .get()
            .addOnSuccessListener { result ->
                subjectsList.clear()
                for (document in result) {
                    val subjectName = document.getString("name") ?: "Nombre no encontrado" // Obtén el nombre
                    val subject = Subject(
                        name = subjectName,
                        code = document.getString("code") ?: "",
                        description = document.getString("description") ?: "",
                        subjectId = document.id
                    )
                    Log.d("FirebaseData", "Subject Name: $subjectName") // Agrega este log
                    subjectsList.add(subject)
                }
                subjectsAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                println("Error al cargar asignaturas: $exception")
            }
    }

    private fun saveOrUpdateSubject() {
        val subjectName = etSubjectName.text.toString()
        val subjectCode = etSubjectCode.text.toString()
        val description = etDescription.text.toString()

        val selectedTeacherIds = teachersAdapter.getSelectedTeacherIds().toList()
        val selectedStudentIds = studentsAdapter.getSelectedStudentIds().toList()

        if (subjectName.isNotBlank() && subjectCode.isNotBlank()) {
            val subjectData = hashMapOf(
                "name" to subjectName,
                "code" to subjectCode,
                "description" to description,
                "assignedTeachers" to selectedTeacherIds,
                "enrolledStudents" to selectedStudentIds
            )

            if (editingSubjectId != null) {
                // Editar asignatura existente
                db.collection("subjects")
                    .document(editingSubjectId!!)
                    .update(subjectData as Map<String, Any>)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Subject updated successfully", Toast.LENGTH_SHORT).show()
                        clearInputFieldsAndSelections()
                        loadExistingSubjectsFromFirebase()
                        editingSubjectId = null
                        btnAddSubject.text = "Add Subject"
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Error updating subject: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            } else {
                // Añadir nueva asignatura
                db.collection("subjects")
                    .add(subjectData)
                    .addOnSuccessListener { documentReference ->
                        Toast.makeText(this, "Subject added successfully", Toast.LENGTH_SHORT).show()
                        clearInputFieldsAndSelections()
                        loadExistingSubjectsFromFirebase()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Error adding subject: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            }
        } else {
            Toast.makeText(this, "Subject Name and Code are required", Toast.LENGTH_SHORT).show()
        }
    }

    private fun clearInputFieldsAndSelections() {
        etSubjectName.text.clear()
        etSubjectCode.text.clear()
        etDescription.text.clear()
        teachersAdapter.clearSelections()
        studentsAdapter.clearSelections()
    }

    override fun onEditClick(subject: Subject) {
        editingSubjectId = subject.subjectId
        etSubjectName.setText(subject.name)
        etSubjectCode.setText(subject.code)
        etDescription.setText(subject.description)
        btnAddSubject.text = "Update Subject"

        // Aquí podrías implementar la lógica para pre-seleccionar los profesores y estudiantes
        // asignados a esta asignatura si los guardaste en el documento.
    }

    override fun onDeleteClick(subjectId: String) {
        AlertDialog.Builder(this)
            .setTitle("Confirm Delete")
            .setMessage("Are you sure you want to delete this subject?")
            .setPositiveButton("Delete") { dialog, which ->
                deleteSubjectFromFirebase(subjectId)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun deleteSubjectFromFirebase(subjectId: String) {
        db.collection("subjects")
            .document(subjectId)
            .delete()
            .addOnSuccessListener {
                Toast.makeText(this, "Subject deleted successfully", Toast.LENGTH_SHORT).show()
                loadExistingSubjectsFromFirebase()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error deleting subject: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}