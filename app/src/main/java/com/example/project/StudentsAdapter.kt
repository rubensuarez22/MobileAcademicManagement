package com.example.project

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class StudentsAdapter(private val studentsList: List<User>) : RecyclerView.Adapter<StudentViewHolder>() {

    private val selectedStudents = mutableSetOf<String>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudentViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_person_with_checkbox, parent, false)
        return StudentViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: StudentViewHolder, position: Int) {
        val student = studentsList[position]
        val isSelected = selectedStudents.contains(student.userId)
        holder.bind(student, isSelected) { isChecked ->
            if (isChecked) {
                student.userId?.let { selectedStudents.add(it) }
            } else {
                student.userId?.let { selectedStudents.remove(it) }
            }
        }
    }

    override fun getItemCount(): Int = studentsList.size

    fun getSelectedStudentIds(): Set<String> {
        return selectedStudents
    }

    fun clearSelections() {
        selectedStudents.clear()
        notifyDataSetChanged() // Para que se desmarquen los checkboxes en la UI
    }

    fun selectStudent(position: Int) {
        val student = studentsList.getOrNull(position) ?: return
        student.userId?.let { selectedStudents.add(it) }
        notifyDataSetChanged()
    }
}