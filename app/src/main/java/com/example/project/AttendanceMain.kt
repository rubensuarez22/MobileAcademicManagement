package com.example.project

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

class AttendanceMain : AppCompatActivity() {

    private lateinit var rvAttendance: RecyclerView
    private lateinit var attendanceAdapter: AttendanceAdapter
    private val attendanceList = mutableListOf<DataAttendance>()
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_attendance)

        rvAttendance = findViewById(R.id.rvAttendance)
        rvAttendance.layoutManager = LinearLayoutManager(this)

        // Inicializamos el adapter y lo asignamos al RecyclerView
        attendanceAdapter = AttendanceAdapter(attendanceList)
        rvAttendance.adapter = attendanceAdapter

        // Recuperamos el subjectId enviado desde TeacherMain
        val subjectId = intent.getStringExtra("subjectId")
        if (subjectId.isNullOrEmpty()) {
            Toast.makeText(this, "Error: materia no encontrada", Toast.LENGTH_SHORT).show()
            finish()
        } else {
            startAttendanceListener(subjectId)
        }
    }

    private fun startAttendanceListener(subjectId: String) {
        db.collection("subjects")
            .document(subjectId)
            .collection("attendance")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.w("attendance", "Error al escuchar asistencia", error)
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    Log.d("attendance", "Documentos recibidos: ${snapshot.documents.size}")
                    attendanceList.clear()
                    for (document in snapshot.documents) {
                        val studentName = document.getString("name") ?: "Sin nombre"
                        val time = document.getString("time") ?: "Sin hora"
                        val attendanceEntry = DataAttendance(studentName, time, R.drawable.ic_check)
                        attendanceList.add(attendanceEntry)
                    }
                    attendanceAdapter.notifyDataSetChanged()
                }
            }
    }

}
