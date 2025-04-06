package com.example.project

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.project.dataAplication.Companion.prefs
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.common.BitMatrix

class TeacherMain : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private val db = FirebaseFirestore.getInstance()
    private var selectedSubjectId: String? = null

    // Agregamos la variable daysOfWeek para el spinner de días
    private val daysOfWeek = arrayOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday")

    // Para el RecyclerView que mostrará las clases filtradas por día
    private lateinit var rvTeacherClasses: RecyclerView
    private val teacherClassesList = mutableListOf<ClassItem>()
    private lateinit var teacherClassesAdapter: ClassAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_teacher_main)
        auth = FirebaseAuth.getInstance()

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

        // Configuramos el RecyclerView para las clases filtradas
        rvTeacherClasses = findViewById(R.id.rvTeacherClasses)
        rvTeacherClasses.layoutManager = LinearLayoutManager(this)
        teacherClassesAdapter = ClassAdapter(teacherClassesList)
        rvTeacherClasses.adapter = teacherClassesAdapter

        // Spinner de materias asignadas al profesor (ya existente)
        // val spiClass = findViewById<Spinner>(R.id.spinner)
        val subjectNames = mutableListOf<String>()
        val subjectIds = mutableListOf<String>()
        val currentUserId = auth.currentUser?.uid ?: ""

        // Cargar materias asignadas al profesor
        db.collection("subjects")
            .whereArrayContains("assignedTeachers", currentUserId)
            .get()
            .addOnSuccessListener { documents ->
                for (doc in documents) {
                    val name = doc.getString("name") ?: "Sin nombre"
                    subjectNames.add(name)
                    subjectIds.add(doc.id)
                }
                val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, subjectNames)
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
//                spiClass.adapter = adapter

//                spiClass.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
//                    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
//                        selectedSubjectId = subjectIds[position]
//                    }
//                    override fun onNothingSelected(parent: AdapterView<*>?) {
//                        selectedSubjectId = null
//                    }
//                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error cargando clases", Toast.LENGTH_SHORT).show()
            }

        // Botones existentes
//        val btnShowQr = findViewById<Button>(R.id.btnQR)
        val btnLogOut = findViewById<ImageView>(R.id.ivLogoutTeacher)
//        val btnAssistance = findViewById<Button>(R.id.btnAssistance)
//        val btnGrades = findViewById<Button>(R.id.btnGrades)

        btnLogOut.setOnClickListener {
            prefs.wipe()
            auth.signOut()
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

//        btnAssistance.setOnClickListener {
//            selectedSubjectId?.let {
//                val intent = Intent(this, attendance::class.java)
//                intent.putExtra("subjectId", it)
//                startActivity(intent)
//            } ?: Toast.makeText(this, "Selecciona una clase", Toast.LENGTH_SHORT).show()
//        }
//
//        btnGrades.setOnClickListener {
//            selectedSubjectId?.let {
//                val intent = Intent(this, TeacherGrades::class.java)
//                intent.putExtra("subjectId", it)
//                startActivity(intent)
//            } ?: Toast.makeText(this, "Selecciona una clase", Toast.LENGTH_SHORT).show()
//        }
//
//        btnShowQr.setOnClickListener {
//            selectedSubjectId?.let {
//                showQrDialog(it)
//            } ?: Toast.makeText(this, "Selecciona una clase", Toast.LENGTH_SHORT).show()
//        }
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
                teacherClassesList.clear()
                for (doc in documents) {
                    val classDays = doc.get("days") as? List<String> ?: emptyList()
                    if (classDays.contains(selectedDay)) {
                        val className = doc.getString("name") ?: ""
                        val time = doc.getString("time") ?: ""
                        // En este ejemplo, usamos el campo 'grade' para mostrar un dato (puede ser el ID o dejarlo vacío)
                        teacherClassesList.add(ClassItem(className, "", time))
                    }
                }
                teacherClassesAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Error al cargar clases: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun showQrDialog(subjectId: String) {
        db.collection("subjects").document(subjectId).get()
            .addOnSuccessListener { document ->
                val qrString = document.getString("qrString")
                if (!qrString.isNullOrEmpty()) {
                    val qrBitmap = generateQrCode(qrString)
                    if (qrBitmap != null) {
                        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_qr, null)
                        val qrImageView = dialogView.findViewById<ImageView>(R.id.ivQrCode)
                        qrImageView.setImageBitmap(qrBitmap)
                        AlertDialog.Builder(this)
                            .setTitle("Código QR de Asistencia")
                            .setView(dialogView)
                            .setPositiveButton("Cerrar", null)
                            .show()
                    } else {
                        Toast.makeText(this, "No se pudo generar el QR", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "No hay QR disponible para esta clase", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error al obtener los datos", Toast.LENGTH_SHORT).show()
            }
    }

    private fun generateQrCode(data: String): Bitmap? {
        return try {
            val matrix: BitMatrix = MultiFormatWriter().encode(data, BarcodeFormat.QR_CODE, 600, 600)
            val width = matrix.width
            val height = matrix.height
            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
            for (x in 0 until width) {
                for (y in 0 until height) {
                    bitmap.setPixel(x, y, if (matrix[x, y]) Color.BLACK else Color.WHITE)
                }
            }
            bitmap
        } catch (e: Exception) {
            Log.e("QR_DEBUG", "Error generating QR: ${e.message}")
            null
        }
    }
}

