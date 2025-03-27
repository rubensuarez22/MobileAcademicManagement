package com.example.project

data class User(
    val name: String,
    var role: String, // 0: Admin, 1: Teacher, 2: Student
    val userId: String? = null
)