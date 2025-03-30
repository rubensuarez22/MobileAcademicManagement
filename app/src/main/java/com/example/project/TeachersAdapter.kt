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
        val isSelected = selectedTeachers.contains(teacher.userId)
        holder.bind(teacher, isSelected) { isChecked ->
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

    fun selectTeacher(position: Int) {
        val teacher = teachersList.getOrNull(position) ?: return
        teacher.userId?.let { selectedTeachers.add(it) }
        notifyItemChanged(position)
    }
}