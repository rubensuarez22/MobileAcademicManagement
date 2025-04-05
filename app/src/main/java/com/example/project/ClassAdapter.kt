package com.example.project

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ClassAdapter(private val classList: List<ClassItem>) : RecyclerView.Adapter<ClassAdapter.ClassViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClassViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_class, parent, false)
        return ClassViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ClassViewHolder, position: Int) {
        val currentItem = classList[position]
        holder.classNameTextView.text = currentItem.name
        holder.gradeTextView.text = currentItem.grade
        holder.timeTextView.text = currentItem.time
    }

    override fun getItemCount() = classList.size

    class ClassViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val classNameTextView: TextView = itemView.findViewById(R.id.tvClassName)
        val gradeTextView: TextView = itemView.findViewById(R.id.tvGrade)
        val timeTextView: TextView = itemView.findViewById(R.id.tvIdClass)
    }
}