package com.example.project

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class AttendanceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val tvIndex: TextView = itemView.findViewById(R.id.tvIndex)
    val tvStudentName: TextView = itemView.findViewById(R.id.tvStudentName)
    val tvTime: TextView = itemView.findViewById(R.id.tvTime)
    val ivStatus: ImageView = itemView.findViewById(R.id.ivStatus)

    fun bind(student: Asistencia) {
        tvIndex.text = "${adapterPosition + 1}."
        tvStudentName.text = student.nombre ?: "Desconocido"
        tvTime.text = student.hora ?: "Sin hora"
        ivStatus.setImageResource(R.drawable.ic_check) // O usa el icono correcto
    }

}


