package com.example.project

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.project.dataAplication.Companion.prefs
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

class StudentMain : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ClassAdapter
    private lateinit var classList: ArrayList<ClassItem>
    private lateinit var logoutImage: ImageView
    private lateinit var ScanQr: Button
    private lateinit var daySpinner: Spinner

    private val daysOfWeek = arrayOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday")

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

        logoutImage = findViewById(R.id.LogOutImage)
        logoutImage.setOnClickListener {
            auth.signOut()
            prefs.wipe()
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        ScanQr = findViewById(R.id.btnScanQr)
        ScanQr.setOnClickListener {
            ImplementationQR(this).init()
        }

        daySpinner = findViewById(R.id.dayspinner)
        setupDaySpinner()

        loadEnrolledSubjects(selectedDay = daysOfWeek[0])
    }

    private fun setupDaySpinner() {
        val adapterSpinner = ArrayAdapter(this, android.R.layout.simple_spinner_item, daysOfWeek)
        adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        daySpinner.adapter = adapterSpinner

        daySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedDay = daysOfWeek[position]
                loadEnrolledSubjects(selectedDay)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    private fun loadEnrolledSubjects(selectedDay: String) {
        val currentUser = auth.currentUser
        currentUser?.let { user ->
            val userId = user.uid

            db.collection("subjects")
                .whereArrayContains("enrolledStudents", userId)
                .get()
                .addOnSuccessListener { querySnapshot ->
                    classList.clear()
                    for (document in querySnapshot) {
                        val classDays = document.get("days") as? List<String> ?: emptyList()
                        if (classDays.contains(selectedDay)) {
                            val className = document.getString("name") ?: ""
                            val time = document.getString("time") ?: ""
                            val grade = ""
                            val classItem = ClassItem(className, grade, time)
                            classList.add(classItem)
                        }
                    }
                    adapter.notifyDataSetChanged()
                }
                .addOnFailureListener { e ->
                    Log.w("StudentMain", "Error getting documents: ", e)
                }
        }
    }

    // ✅ Esta función registra la asistencia al leer el QR (con nombre desde Firestore)
    fun handleScannedQr(qrString: String) {
        val user = auth.currentUser ?: return
        val userId = user.uid
        val db = FirebaseFirestore.getInstance()

        try {
            val qrData = JSONObject(qrString)
            val subjectId = qrData.getString("subjectId")
            val currentTime = getCurrentTime()

            // 🔽 Obtener nombre desde Firestore (colección "users")
            db.collection("users").document(userId).get()
                .addOnSuccessListener { document ->
                    val name = document.getString("name") ?: "Sin nombre"

                    val attendanceData = mapOf(
                        "studentId" to userId,
                        "name" to name,
                        "time" to currentTime
                    )

                    db.collection("subjects")
                        .document(subjectId)
                        .collection("attendance")
                        .document(userId)
                        .set(attendanceData)
                        .addOnSuccessListener {
                            Toast.makeText(this, "Has pasado lista ✅", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener {
                            Toast.makeText(this, "Error al registrar asistencia", Toast.LENGTH_SHORT).show()
                        }
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Error al obtener tu nombre", Toast.LENGTH_SHORT).show()
                }

        } catch (e: Exception) {
            Toast.makeText(this, "QR inválido", Toast.LENGTH_SHORT).show()
            Log.e("QR_SCAN", "Error: ${e.message}")
        }
    }

    private fun getCurrentTime(): String {
        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        return sdf.format(Date())
    }
}
