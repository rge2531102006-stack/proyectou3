package com.example.controldegastos.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.controldegastos.R
import com.example.controldegastos.ExpenseRepository
import com.example.controldegastos.databinding.ActivityMainBinding
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Source
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: ExpenseAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter = ExpenseAdapter(
            items = emptyList(),
            onExpenseClick = { expense ->
                val intent = Intent(this, ExpenseFormActivity::class.java).apply {
                    putExtra(ExpenseFormActivity.EXTRA_EXPENSE_ID, expense.id)
                }
                startActivity(intent)
            },
            onDeleteClick = { expense ->
                ExpenseRepository.deleteExpense(expense.id)
                refreshExpenses()
            }
        )

        binding.rvExpenses.layoutManager = LinearLayoutManager(this)
        binding.rvExpenses.adapter = adapter
        verifyFirebaseConnection()

        binding.btnAddExpense.setOnClickListener {
            startActivity(Intent(this, ExpenseFormActivity::class.java))
        }

        binding.btnSettings.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }

        binding.btnBack.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            startActivity(intent)
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        refreshExpenses()
    }

    private fun refreshExpenses() {
        val expenses = ExpenseRepository.getAll()
        adapter.updateExpenses(expenses)
        val total = ExpenseRepository.totalAmount()
        binding.tvTotalExpenses.text = String.format(
            Locale.getDefault(),
            "Total gastado: $ %.2f  (%d movimientos)",
            total,
            expenses.size
        )
    }

    private fun verifyFirebaseConnection() {
        val firebaseApp = FirebaseApp.initializeApp(this)
        if (firebaseApp == null) {
            binding.tvFirebaseStatus.text = getString(R.string.firebase_not_configured)
            return
        }

        FirebaseFirestore.getInstance(firebaseApp)
            .collection("expenses")
            .limit(1)
            .get(Source.SERVER)
            .addOnSuccessListener { tokenResult ->
                binding.tvFirebaseStatus.text = getString(R.string.firebase_connected)
            }
            .addOnFailureListener { error ->
                if (error is FirebaseFirestoreException &&
                    error.code == FirebaseFirestoreException.Code.PERMISSION_DENIED
                ) {
                    binding.tvFirebaseStatus.text =
                        getString(R.string.firebase_firestore_permission_denied)
                } else {
                    Log.e(TAG, "Firestore connectivity check failed", error)
                    binding.tvFirebaseStatus.text = getString(R.string.firebase_unavailable)
                }
            }
    }

    companion object {
        private const val TAG = "MainActivity"
    }
}