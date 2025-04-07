package com.example.project

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.project.dataAplication.Companion.prefs
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.ArrayList
import java.util.Date
import java.util.Locale

class TeacherMain : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db : FirebaseFirestore

    // Para el RecyclerView que mostrará las clases filtradas por día
    private lateinit var rvTeacherClasses: RecyclerView
    private lateinit var teacherClassList: ArrayList<ClassItemTeacher>
    private lateinit var teacherClassAdapter: TeacherClassAdapter
    private lateinit var verAsistenciaBtn: Button

    private var selectedDateString: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_teacher_main)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        // Configuramos el RecyclerView para las clases filtradas
        rvTeacherClasses = findViewById(R.id.rvTeacherClasses)
        rvTeacherClasses.layoutManager = LinearLayoutManager(this)
        teacherClassList = ArrayList()
        teacherClassAdapter = TeacherClassAdapter(teacherClassList, selectedDateString)

        rvTeacherClasses.adapter = teacherClassAdapter

        verAsistenciaBtn = findViewById(R.id.btnVerAsistencia)
        verAsistenciaBtn.setOnClickListener {
            abrirSelectorDeFecha()
        }

        // Botones existentes
        val btnLogOut = findViewById<ImageView>(R.id.ivLogoutTeacher)

        btnLogOut.setOnClickListener {
            prefs.wipe()
            auth.signOut()
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    /**
     * Consulta Firestore para obtener las materias asignadas al profesor
     * que se imparten en el día seleccionado y las muestra en el RecyclerView.
     */
    private fun loadTeacherClasses(selectedDay: String) {
        val currentUserId = auth.currentUser?.uid ?: return
        db.collection("subjects")
            .whereArrayContains("assignedTeachers", currentUserId)
            .get()
            .addOnSuccessListener { documents ->
                teacherClassList.clear()
                for (doc in documents) {
                    val classDays = doc.get("days") as? List<String> ?: emptyList()
                    if (classDays.contains(selectedDay)) {
                        val className = doc.getString("name") ?: "Class name not found"
                        val time = doc.getString("time") ?: "Class time not found"
                        val classId = doc.id
                        teacherClassList.add(ClassItemTeacher(className,time ,"", classId))
                    }
                }
                teacherClassAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Error al cargar clases: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }

    fun abrirSelectorDeFecha() {
        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Selecciona una fecha")
            .build()

        datePicker.show(supportFragmentManager, "DATE_PICKER")

        datePicker.addOnPositiveButtonClickListener { selectedDateMillis ->
            val selectedDate = Date(selectedDateMillis)

            val sdfDia = SimpleDateFormat("EEEE", Locale.ENGLISH)
            val sdfFecha = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

// Fuerza UTC
            val utc = java.util.TimeZone.getTimeZone("UTC")
            sdfDia.timeZone = utc
            sdfFecha.timeZone = utc

            val diaSemana = sdfDia.format(selectedDate)
            val fechaSeleccionada = sdfFecha.format(selectedDate)

            Log.e("Fecha", "Raw millis: $selectedDateMillis")
            Log.e("Fecha", "Date obj (UTC): $selectedDate")
            Log.e("Fecha", "Día semana: $diaSemana")
            Log.e("Fecha", "Fecha seleccionada: $fechaSeleccionada")

// Luego lo usas
            loadTeacherClasses(diaSemana)
            selectedDateString = fechaSeleccionada
            teacherClassAdapter.updateDate(selectedDateString)
            verAsistenciaBtn.text = "$fechaSeleccionada"
        }
    }
}
