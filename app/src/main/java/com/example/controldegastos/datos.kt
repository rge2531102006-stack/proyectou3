package com.example.controldegastos.data

data class Expense(
    val id: Int,
    val concept: String,
    val category: String,
    val amount: Double,
    val date: String
)