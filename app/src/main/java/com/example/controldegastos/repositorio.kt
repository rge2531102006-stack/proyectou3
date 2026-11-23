package com.example.controldegastos

import com.example.controldegastos.data.Expense
import com.google.firebase.firestore.FirebaseFirestore

object ExpenseRepository {
    private val db: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }
    private val expensesCollection by lazy { db.collection("expenses") }

    private var nextId = 4
    private val expenses = mutableListOf(
        Expense(1, "Comida en la universidad", "Comida", 85.0, "2026-11-22"),
        Expense(2, "Pasaje de camión", "Transporte", 24.0, "2026-11-22"),
        Expense(3, "Copias y carpetas", "Escuela", 45.5, "2026-11-21")
    )

    fun getAll(): List<Expense> = expenses.sortedByDescending { it.date }

    fun fetchAll(
        onResult: (List<Expense>) -> Unit,
        onFailure: (Exception) -> Unit = {}
    ) {
        expensesCollection.get()
            .addOnSuccessListener { snapshot ->
                val remoteList = snapshot.documents.mapNotNull { doc ->
                    val id = doc.getLong("id")?.toInt() ?: return@mapNotNull null
                    val concept = doc.getString("concept").orEmpty()
                    val category = doc.getString("category").orEmpty()
                    val amount = doc.getDouble("amount") ?: 0.0
                    val date = doc.getString("date").orEmpty()
                    Expense(id, concept, category, amount, date)
                }
                expenses.clear()
                expenses.addAll(remoteList)
                val maxId = expenses.maxOfOrNull { it.id } ?: 3
                nextId = maxId + 1
                onResult(getAll())
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }

    fun findById(id: Int): Expense? = expenses.find { it.id == id }

    fun addExpense(
        concept: String,
        category: String,
        amount: Double,
        date: String,
        onSuccess: () -> Unit = {},
        onFailure: (Exception) -> Unit = {}
    ) {
        val newId = nextId++
        val expense = Expense(newId, concept, category, amount, date)
        expenses.add(0, expense)

        val data = hashMapOf(
            "id" to expense.id,
            "concept" to expense.concept,
            "category" to expense.category,
            "amount" to expense.amount,
            "date" to expense.date
        )

        expensesCollection.document(newId.toString())
            .set(data)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { exception -> onFailure(exception) }
    }

    fun updateExpense(
        id: Int,
        concept: String,
        category: String,
        amount: Double,
        date: String,
        onSuccess: () -> Unit = {},
        onFailure: (Exception) -> Unit = {}
    ) {
        val index = expenses.indexOfFirst { it.id == id }
        if (index != -1) {
            expenses[index] = Expense(id, concept, category, amount, date)
        }

        val data = hashMapOf(
            "id" to id,
            "concept" to concept,
            "category" to category,
            "amount" to amount,
            "date" to date
        )

        expensesCollection.document(id.toString())
            .set(data)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { exception -> onFailure(exception) }
    }

    fun deleteExpense(
        id: Int,
        onSuccess: () -> Unit = {},
        onFailure: (Exception) -> Unit = {}
    ) {
        expenses.removeAll { it.id == id }

        expensesCollection.document(id.toString())
            .delete()
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { exception -> onFailure(exception) }
    }

    fun totalAmount(): Double = expenses.sumOf { it.amount }
}