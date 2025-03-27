package com.example.project

import android.view.View
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class StudentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val checkBoxStudent: CheckBox = itemView.findViewById(R.id.checkBoxPerson)
    val textViewStudentName: TextView = itemView.findViewById(R.id.textViewPersonName)

    private lateinit var currentStudent: User

    fun bind(student: User, onItemSelected: (Boolean) -> Unit) {
        currentStudent = student
        textViewStudentName.text = student.name
        checkBoxStudent.setOnCheckedChangeListener { _, isChecked ->
            onItemSelected(isChecked)
        }
        checkBoxStudent.isChecked = false // Asegurarse de que estén desmarcados al principio
    }
}