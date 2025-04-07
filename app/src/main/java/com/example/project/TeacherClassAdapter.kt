package com.example.project

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class TeacherClassAdapter(val classList: List<ClassItemTeacher>, var selectedDate: String) : RecyclerView.Adapter<TeacherClassViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TeacherClassViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_class_teacher, parent, false)
        return TeacherClassViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: TeacherClassViewHolder, position: Int) {
        val currentItem = classList[position]
        Log.e("FechaTCA","${selectedDate}")
        holder.bind(currentItem, selectedDate)
    }

    override fun getItemCount() = classList.size

    fun updateDate(newDate: String) {
        selectedDate = newDate
    }

}