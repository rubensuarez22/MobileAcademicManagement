package com.example.project

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
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
        holder.descriptionTextView.text = currentItem.description

        // Aquí puedes agregar la lógica para el botón "Scan Attendance" si es necesario
        holder.scanAttendanceButton.setOnClickListener {
            // Handle click event
        }
    }

    override fun getItemCount() = classList.size

    class ClassViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val classNameTextView: TextView = itemView.findViewById(R.id.tvClassName)
        val gradeTextView: TextView = itemView.findViewById(R.id.tvGrade)
        val descriptionTextView: TextView = itemView.findViewById(R.id.tvDescription)
        val scanAttendanceButton: Button = itemView.findViewById(R.id.btnScanAttendance)
    }
}