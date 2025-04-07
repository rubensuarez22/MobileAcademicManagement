package com.example.project

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView


class AttendanceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val tvIndex: TextView = itemView.findViewById(R.id.tvIndex)
    val tvStudentName: TextView = itemView.findViewById(R.id.tvStudentName)
    val tvTime: TextView = itemView.findViewById(R.id.tvClassHour)
    val ivStatus: ImageView = itemView.findViewById(R.id.ivStatus)

    fun bind(student: DataAttendance) {
        tvIndex.text = "${adapterPosition + 1}."
        tvStudentName.text = student.name
        tvTime.text = student.time
        // Puedes usar el valor de statusIcon definido en dataAttendance
        ivStatus.setImageResource(student.statusIcon)
    }
}


