package com.example.project

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class StudentGradesAdapter(
    private val studentsList: List<StudentGrade>,
    private val onAssignClick: (StudentGrade, String) -> Unit
) : RecyclerView.Adapter<StudentGradeViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudentGradeViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_student_grade, parent, false)
        return StudentGradeViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: StudentGradeViewHolder, position: Int) {
        val student = studentsList[position]
        holder.bind(student, onAssignClick)
    }

    override fun getItemCount(): Int = studentsList.size
}
