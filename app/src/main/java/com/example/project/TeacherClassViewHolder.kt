package com.example.project

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.common.BitMatrix

class TeacherClassViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    private val tvClassName: TextView = view.findViewById(R.id.tvClassNameTeacher)
    private val tvHour: TextView = view.findViewById(R.id.tvHourTeacher)
    private val tvComment: TextView = view.findViewById(R.id.tvCommentTeacher)
    private val btnAssistance: Button = view.findViewById(R.id.btnAssistanceTeacher)
    private val btnGrades: Button = view.findViewById(R.id.btnGradesTeacher)
    private val btnShowQr: Button = view.findViewById(R.id.btnQRTeacher)

    private lateinit var db : FirebaseFirestore

    fun bind(classItem: ClassItemTeacher, selectd: String) {

        db = FirebaseFirestore.getInstance()
        // Asigna los datos a las vistas
        tvClassName.text = classItem.className
        tvHour.text = classItem.classHour
        tvComment.text = classItem.classComment
        // Obtenemos el subjectId del objeto
        val classId = classItem.classId

        btnAssistance.setOnClickListener {
            if (classId.isNotEmpty()) {
                // Redirige a la vista de asistencia (AttendanceActivity)
                val attendanceMain = AttendanceMain.newInstance(classId, selectd)
                attendanceMain.show((itemView.context as AppCompatActivity).supportFragmentManager, "AttendanceMain")
            } else {
                Toast.makeText(itemView.context, "Selecciona una clase", Toast.LENGTH_SHORT).show()
            }
        }

        btnGrades.setOnClickListener {
            if (classId.isNotEmpty()) {
                // Redirige a la vista de asignar calificaciones (TeacherGradesActivity)
                val intent = Intent(itemView.context, TeacherGrades::class.java)
                intent.putExtra("subjectId", classId)
                itemView.context.startActivity(intent)
            } else {
                Toast.makeText(itemView.context, "Selecciona una clase", Toast.LENGTH_SHORT).show()
            }
        }

        btnShowQr.setOnClickListener {
            if (classId.isNotEmpty()) {
                // Muestra el diálogo con el QR
                showQrDialog(classId)
            } else {
                Toast.makeText(itemView.context, "Selecciona una clase", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showQrDialog(subjectId: String) {
        db.collection("subjects").document(subjectId).get()
            .addOnSuccessListener { document ->
                val qrString = document.getString("qrString")
                if (!qrString.isNullOrEmpty()) {
                    val qrBitmap = generateQrCode(qrString)
                    if (qrBitmap != null) {
                        val dialogView = LayoutInflater.from(itemView.context).inflate(R.layout.dialog_qr, null)
                        val qrImageView = dialogView.findViewById<ImageView>(R.id.ivQrCode)
                        qrImageView.setImageBitmap(qrBitmap)
                        AlertDialog.Builder(itemView.context)
                            .setTitle("Código QR de Asistencia")
                            .setView(dialogView)
                            .setPositiveButton("Cerrar", null)
                            .show()
                    } else {
                        Toast.makeText(itemView.context, "No se pudo generar el QR", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(itemView.context, "No hay QR disponible para esta clase", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener {
                Toast.makeText(itemView.context, "Error al obtener los datos", Toast.LENGTH_SHORT).show()
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