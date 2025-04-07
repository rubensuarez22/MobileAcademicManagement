package com.example.project

import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.TimePicker
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import com.google.zxing.BarcodeFormat
import com.journeyapps.barcodescanner.BarcodeEncoder
import java.io.ByteArrayOutputStream
import com.google.firebase.storage.FirebaseStorage


interface OnEditSubjectClickListener {
    fun onEditClick(subject: Subject)
}

interface OnDeleteSubjectClickListener {
    fun onDeleteClick(subjectId: String, subjectName : String)
}

class ModifySubjects : AppCompatActivity(), OnEditSubjectClickListener, OnDeleteSubjectClickListener {

    private lateinit var etSubjectName: EditText
    private lateinit var etSubjectCode: EditText
    private lateinit var tvSelectedDays: TextView
    private lateinit var timePicker: TimePicker
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

    private val days = arrayOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday")
    private val selectedDays = BooleanArray(days.size)
    private val selectedList = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_modify_subjects)

        etSubjectName = findViewById(R.id.etSubjectName)
        etSubjectCode = findViewById(R.id.etSubjectCode)
        tvSelectedDays = findViewById(R.id.tvSelectedDays)
        timePicker = findViewById(R.id.timePicker)
        rvAssignTeachers = findViewById(R.id.rvAssignTeachers)
        rvEnrollStudents = findViewById(R.id.rvEnrollStudents)
        rvExistingSubjects = findViewById(R.id.rvExistingSubjects)
        btnAddSubject = findViewById(R.id.btnAddSubject)

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

        tvSelectedDays.setOnClickListener {
            showDaySelectionDialog()
        }

        btnAddSubject.setOnClickListener {
            saveOrUpdateSubject()
        }
    }

    private fun showDaySelectionDialog() {
        AlertDialog.Builder(this)
            .setTitle("Select your 2 days of class")
            .setMultiChoiceItems(days, selectedDays) { dialog, which, isChecked ->
                if (isChecked) {
                    if (selectedList.size < 2) {
                        selectedList.add(days[which])
                    } else {
                        (dialog as AlertDialog).listView.setItemChecked(which, false)
                        Toast.makeText(this, "Only two days can be selected", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    selectedList.remove(days[which])
                }
            }
            .setPositiveButton("OK") { _, _ ->
                tvSelectedDays.text = selectedList.joinToString(", ")
            }
            .setNegativeButton("Cancel", null)
            .show()
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
                    val subjectName = document.getString("name") ?: "Nombre no encontrado"
                    val subject = Subject(
                        name = subjectName,
                        code = document.getString("code") ?: "",
                        days = document.get("days") as? List<String> ?: emptyList(),
                        time = document.getString("time") ?: "",
                        assignedTeachers = document.get("assignedTeachers") as? List<String> ?: emptyList(),
                        enrolledStudents = document.get("enrolledStudents") as? List<String> ?: emptyList(),
                        subjectId = document.id
                    )
                    Log.d("FirebaseData", "Subject Name: $subjectName, Days: ${subject.days}, Time: ${subject.time}, Teachers: ${subject.assignedTeachers}, Students: ${subject.enrolledStudents}")
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
        val selectedTeacherIds = teachersAdapter.getSelectedTeacherIds().toList()
        val selectedStudentIds = studentsAdapter.getSelectedStudentIds().toList()
        val time = "${timePicker.hour}:${String.format("%02d", timePicker.minute)}"

        if (subjectName.isNotBlank() && subjectCode.isNotBlank() && selectedList.isNotEmpty()) {
            val subjectData = hashMapOf(
                "name" to subjectName,
                "code" to subjectCode,
                "days" to selectedList,
                "time" to time,
                "assignedTeachers" to selectedTeacherIds,
                "enrolledStudents" to selectedStudentIds
            )

            if (editingSubjectId != null) {
                db.collection("subjects").document(editingSubjectId!!)
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
                val newSubjectRef = db.collection("subjects").document()
                val subjectId = newSubjectRef.id

                newSubjectRef.set(subjectData)
                    .addOnSuccessListener {
                        val qrPayload = mapOf(
                            "subjectId" to subjectId,
                            "timestamp" to System.currentTimeMillis(),
                            "type" to "attendance"
                        )
                        val qrString = Gson().toJson(qrPayload)

                        db.collection("subjects").document(subjectId).update("qrString", qrString)
                            .addOnFailureListener { e ->
                                Log.d("QR_DEBUG", "QR generation failed: ${e.message}")
                            }

                        Toast.makeText(this, "Subject added successfully", Toast.LENGTH_SHORT).show()
                        clearInputFieldsAndSelections()
                        loadExistingSubjectsFromFirebase()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Error adding subject: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            }
        } else {
            Toast.makeText(this, "Name, Code, and at least one day are required", Toast.LENGTH_SHORT).show()
        }
    }


    private fun clearInputFieldsAndSelections() {
        etSubjectName.text.clear()
        etSubjectCode.text.clear()
        selectedList.clear()
        selectedDays.fill(false)
        tvSelectedDays.text = ""
        teachersAdapter.clearSelections()
        studentsAdapter.clearSelections()
    }

    override fun onEditClick(subject: Subject) {
        editingSubjectId = subject.subjectId
        etSubjectName.setText(subject.name)
        etSubjectCode.setText(subject.code)

        // Restore selected days
        selectedList.clear()
        selectedDays.fill(false)
        val daysFromSubject = subject.days ?: listOf()
        for ((index, day) in days.withIndex()) {
            if (daysFromSubject.contains(day)) {
                selectedDays[index] = true
                selectedList.add(day)
            }
        }
        tvSelectedDays.text = selectedList.joinToString(", ")

        // Restore time
        val timeParts = subject.time?.split(":") // time is saved like "14:30"
        if (timeParts?.size == 2) {
            val hour = timeParts[0].toIntOrNull() ?: 0
            val minute = timeParts[1].toIntOrNull() ?: 0
            timePicker.hour = hour
            timePicker.minute = minute
        }

        btnAddSubject.text = "Update Subject"

        // ✅ Pre-seleccionar profesores
        teachersAdapter.clearSelections()
        Log.d("PreSelect", "Assigned Teachers IDs: ${subject.assignedTeachers}")
        Log.d("PreSelect", "Available Teachers IDs: ${teachersList.map { it.userId }}")
        subject.assignedTeachers?.let { assigned ->
            for (i in teachersList.indices) {
                val teacherId = teachersList[i].userId
                if (assigned.contains(teacherId)) {
                    Log.d("PreSelect", "Selecting Teacher at index $i with ID: $teacherId")
                    teachersAdapter.selectTeacher(i)
                }
            }
        }

        // ✅ Pre-seleccionar estudiantes
        studentsAdapter.clearSelections()
        Log.d("PreSelect", "Enrolled Students IDs: ${subject.enrolledStudents}")
        Log.d("PreSelect", "Available Students IDs: ${studentsList.map { it.userId }}")
        subject.enrolledStudents?.let { enrolled ->
            for (i in studentsList.indices) {
                val studentId = studentsList[i].userId
                if (enrolled.contains(studentId)) {
                    Log.d("PreSelect", "Selecting Student at index $i with ID: $studentId")
                    studentsAdapter.selectStudent(i)
                }
            }
        }
    }

    override fun onDeleteClick(subjectId: String, subjectName: String) {
        AlertDialog.Builder(this)
            .setTitle("Confirm Delete")
            .setMessage("Are you sure you want to delete the subject '$subjectName'?")
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

    private fun generateQrCode(subject: Subject): Bitmap? {
        val json = Gson().toJson(subject)
        return try {
            val barcodeEncoder = BarcodeEncoder()
            barcodeEncoder.encodeBitmap(json, BarcodeFormat.QR_CODE, 400, 400)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}