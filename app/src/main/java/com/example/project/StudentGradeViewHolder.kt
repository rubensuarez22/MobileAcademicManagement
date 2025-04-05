package com.example.project

import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView

class StudentGradeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val tvStudentName: TextView = itemView.findViewById(R.id.textView20)
    val tvStudentId: TextView = itemView.findViewById(R.id.textView21)
    val btnAssignGrade: Button = itemView.findViewById(R.id.button3)
    val etGrade: EditText = itemView.findViewById(R.id.editTextNumberSigned)

    fun bind(student: StudentGrade, onAssignClick: (StudentGrade, String) -> Unit) {
        tvStudentName.text = student.name
        tvStudentId.text = student.id.toString()

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
