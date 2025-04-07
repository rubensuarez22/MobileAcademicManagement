package com.example.project

import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView

class StudentGradeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val tvStudentName: TextView = itemView.findViewById(R.id.tvStudentName)
    val btnAssignGrade: Button = itemView.findViewById(R.id.btnAssignGrade)
    val etGrade: EditText = itemView.findViewById(R.id.etGrade)

    fun bind(student: StudentGrade, onAssignClick: (StudentGrade, String) -> Unit) {
        tvStudentName.text = student.name

        btnAssignGrade.setOnClickListener {
            val gradeText = etGrade.text.toString()
            if (gradeText.isNotEmpty()) {
                onAssignClick(student, gradeText)
            } else {
                Toast.makeText(itemView.context, "Ingresa una calificación", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
