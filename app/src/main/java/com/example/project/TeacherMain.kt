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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_teacher_main)
        auth = FirebaseAuth.getInstance()

        val btnShowQr = findViewById<Button>(R.id.button6)
        val btnLogOut = findViewById<Button>(R.id.btnLogOutTeacher)
        val btnAssistance = findViewById<Button>(R.id.button7)
        val btnGrades = findViewById<Button>(R.id.button5)
        val spiClass = findViewById<Spinner>(R.id.spinner)
        val spiSchedule = findViewById<Spinner>(R.id.spinner3)

        val subjectNames = mutableListOf<String>()
        val subjectIds = mutableListOf<String>()
        val currentUserId = auth.currentUser?.uid ?: ""

        // 🔄 Cargar materias asignadas al profesor
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
                spiClass.adapter = adapter

                spiClass.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                        selectedSubjectId = subjectIds[position]
                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {
                        selectedSubjectId = null
                    }
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error cargando clases", Toast.LENGTH_SHORT).show()
            }

        btnLogOut.setOnClickListener {
            prefs.wipe()
            auth.signOut()
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        btnAssistance.setOnClickListener {
            selectedSubjectId?.let {
                val intent = Intent(this, attendance::class.java)
                intent.putExtra("subjectId", it)
                startActivity(intent)
            } ?: Toast.makeText(this, "Selecciona una clase", Toast.LENGTH_SHORT).show()
        }

        btnGrades.setOnClickListener {
            startActivity(Intent(this, TeacherGrades::class.java))
        }

        btnShowQr.setOnClickListener {
            selectedSubjectId?.let {
                showQrDialog(it)
            } ?: Toast.makeText(this, "Selecciona una clase", Toast.LENGTH_SHORT).show()
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
