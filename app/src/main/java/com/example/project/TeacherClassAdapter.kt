package com.example.project

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class TeacherClassAdapter(val classList: List<ClassItemTeacher>) : RecyclerView.Adapter<TeacherClassViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TeacherClassViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_class_teacher, parent, false)
        return TeacherClassViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: TeacherClassViewHolder, position: Int) {
        val currentItem = classList[position]
        holder.bind(currentItem)
    }

    override fun getItemCount() = classList.size
}