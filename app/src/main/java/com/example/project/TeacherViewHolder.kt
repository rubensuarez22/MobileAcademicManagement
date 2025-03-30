package com.example.project

import android.view.View
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class TeacherViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val checkBoxTeacher: CheckBox = itemView.findViewById(R.id.checkBoxPerson)
    val textViewTeacherName: TextView = itemView.findViewById(R.id.textViewPersonName)

    private lateinit var currentTeacher: User

    fun bind(teacher: User, isSelected: Boolean, onItemSelected: (Boolean) -> Unit) {
        currentTeacher = teacher
        textViewTeacherName.text = teacher.name
        checkBoxTeacher.setOnCheckedChangeListener(null) // Avoid unwanted triggers when recycling
        checkBoxTeacher.isChecked = isSelected // <-- Pre-select if needed
        checkBoxTeacher.setOnCheckedChangeListener { _, isChecked ->
            onItemSelected(isChecked)
        }
    }
}