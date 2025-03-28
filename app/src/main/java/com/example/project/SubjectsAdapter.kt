package com.example.project

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class SubjectsAdapter(
    private val subjectsList: List<Subject>,
    private val editClickListener: OnEditSubjectClickListener,
    private val deleteClickListener: OnDeleteSubjectClickListener
) : RecyclerView.Adapter<SubjectViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubjectViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_subject, parent, false)
        return SubjectViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: SubjectViewHolder, position: Int) {
        val subject = subjectsList[position]
        holder.bind(subject, editClickListener, deleteClickListener)
    }

    override fun getItemCount(): Int = subjectsList.size
}