package com.example.project

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class SubjectViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val textViewSubjectName: TextView = itemView.findViewById(R.id.textViewSubjectName)
    val imageViewEdit: ImageView = itemView.findViewById(R.id.imageViewEdit)
    val imageViewDelete: ImageView = itemView.findViewById(R.id.imageViewDelete)

    fun bind(
        subject: Subject,
        editClickListener: OnEditSubjectClickListener,
        deleteClickListener: OnDeleteSubjectClickListener
    ) {
        textViewSubjectName.text = subject.name

        imageViewEdit.setOnClickListener {
            editClickListener.onEditClick(subject)
        }

        imageViewDelete.setOnClickListener {
            val id = subject.subjectId
            val name = subject.name
            if (id != null) {
                deleteClickListener.onDeleteClick(id, name)
            }
        }
    }
}