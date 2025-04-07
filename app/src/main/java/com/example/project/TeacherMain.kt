package com.example.project

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
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

    // Agregamos la variable daysOfWeek para el spinner de días
    private val daysOfWeek = arrayOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday")
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
        teacherClassAdapter = TeacherClassAdapter(teacherClassList) { classItem ->
            if (selectedDateString.isNotEmpty()) {
                consultarAsistencia(classItem.classId, classItem.className, selectedDateString)
            } else {
                Toast.makeText(this, "Primero selecciona una fecha 📅", Toast.LENGTH_SHORT).show()
            }
        }

        rvTeacherClasses.adapter = teacherClassAdapter

        // Spinner para los días (ya definido en el layout con id spinnerDaysTeacher)
        val dayAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, daysOfWeek)


        val verAsistenciaBtn: Button = findViewById(R.id.btnVerAsistencia)
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

            val sdfDia = SimpleDateFormat("EEEE", Locale.ENGLISH) // Día como "Tuesday"
            val sdfFecha = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

            val diaSemana = sdfDia.format(selectedDate)  // ej: "Tuesday"
            val fechaSeleccionada = sdfFecha.format(selectedDate)

            // ✅ Cargar clases que ocurren ese día
            loadTeacherClasses(diaSemana)

            // ✅ Guardar fecha seleccionada para consulta de asistencia después (opcional)
            selectedDateString = fechaSeleccionada // si quieres usarlo luego
        }
    }

    fun consultarAsistencia(idClase: String, nombreClase: String, fecha: String) {
        db.collection("Asistencias")
            .whereEqualTo("idClase", idClase)
            .whereEqualTo("fecha", fecha)
            .get()
            .addOnSuccessListener { asistencias ->
                val estudiantes = asistencias.map { it.getString("idEstudiante") ?: "Desconocido" }
                Log.d("Asistencia", "Clase: $nombreClase ($fecha) - Asistieron: $estudiantes")
            }
    }
}