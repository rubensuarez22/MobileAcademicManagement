package com.example.project

import android.view.View
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class StudentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val checkBoxStudent: CheckBox = itemView.findViewById(R.id.checkBoxPerson)
    val textViewStudentName: TextView = itemView.findViewById(R.id.textViewPersonName)

    private lateinit var currentStudent: User

    fun bind(student: User, isSelected: Boolean, onItemSelected: (Boolean) -> Unit) {
        currentStudent = student
        textViewStudentName.text = student.name
        checkBoxStudent.setOnCheckedChangeListener(null) // Avoid unwanted triggers when recycling
        checkBoxStudent.isChecked = isSelected // <-- Pre-select if needed
        checkBoxStudent.setOnCheckedChangeListener { _, isChecked ->
            onItemSelected(isChecked)
        }
    }
}