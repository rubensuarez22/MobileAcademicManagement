package com.example.project

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.project.dataAplication.Companion.prefs
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.ArrayList

class TeacherMain : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db : FirebaseFirestore

    // Para el RecyclerView que mostrará las clases filtradas por día
    private lateinit var rvTeacherClasses: RecyclerView
    private lateinit var teacherClassList: ArrayList<ClassItemTeacher>
    private lateinit var teacherClassAdapter: TeacherClassAdapter

    // Agregamos la variable daysOfWeek para el spinner de días
    private val daysOfWeek = arrayOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_teacher_main)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        // Configuramos el RecyclerView para las clases filtradas
        rvTeacherClasses = findViewById(R.id.rvTeacherClasses)
        rvTeacherClasses.layoutManager = LinearLayoutManager(this)
        teacherClassList = ArrayList()
        teacherClassAdapter = TeacherClassAdapter(teacherClassList)
        rvTeacherClasses.adapter = teacherClassAdapter

        // Spinner para los días (ya definido en el layout con id spinnerDaysTeacher)
        val spiSchedule = findViewById<Spinner>(R.id.spinnerDaysTeacher)
        val dayAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, daysOfWeek)

        dayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spiSchedule.adapter = dayAdapter

        // Cada vez que se seleccione un día, se carga el RecyclerView con las clases de ese día
        spiSchedule.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedDay = daysOfWeek[position]
                loadTeacherClasses(selectedDay)
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
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
}