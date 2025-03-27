package com.example.project

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class TeachersAdapter(private val teachersList: List<User>) : RecyclerView.Adapter<TeacherViewHolder>() {

    private val selectedTeachers = mutableSetOf<String>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TeacherViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_person_with_checkbox, parent, false)
        return TeacherViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: TeacherViewHolder, position: Int) {
        val teacher = teachersList[position]
        holder.bind(teacher) { isChecked ->
            if (isChecked) {
                teacher.userId?.let { selectedTeachers.add(it) }
            } else {
                teacher.userId?.let { selectedTeachers.remove(it) }
            }
        }
    }

    override fun getItemCount(): Int = teachersList.size

    fun getSelectedTeacherIds(): Set<String> {
        return selectedTeachers
    }

    fun clearSelections() {
        selectedTeachers.clear()
        notifyDataSetChanged() // Para que se desmarquen los checkboxes en la UI
    }
}