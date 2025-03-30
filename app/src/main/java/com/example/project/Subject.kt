package com.example.project

data class Subject(
    val name: String = "",
    val code: String = "",
    val days: List<String>? = null,
    val time: String? = null,
    val subjectId: String = "",
    val assignedTeachers: List<String>? = null,
    val enrolledStudents: List<String>? = null
)