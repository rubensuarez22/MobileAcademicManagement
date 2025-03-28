package com.example.project

data class User(
    val name: String,
    var role: String, // 2: Admin, 1: Teacher, 0: Student
    val userId: String? = null
)