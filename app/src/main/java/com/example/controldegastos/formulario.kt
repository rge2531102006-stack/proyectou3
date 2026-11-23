package com.example.controldegastos.ui

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.controldegastos.R
import com.example.controldegastos.ExpenseRepository
import com.example.controldegastos.databinding.ActivityFormularioBinding

class ExpenseFormActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFormularioBinding
    private var editingExpenseId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFormularioBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener {
            finish()
        }

        editingExpenseId = intent.getIntExtra(EXTRA_EXPENSE_ID, -1)
        if (editingExpenseId != -1) {
            ExpenseRepository.findById(editingExpenseId)?.let { expense ->
                binding.etConcept.setText(expense.concept)
                binding.etCategory.setText(expense.category)
                binding.etAmount.setText(expense.amount.toString())
                binding.etDate.setText(expense.date)
            }
        }

        binding.btnSaveExpense.setOnClickListener {
            if (validateAndSaveExpense()) {
                finish()
            }
        }
    }

    private fun validateAndSaveExpense(): Boolean {
        binding.tilConcept.error = null
        binding.tilCategory.error = null
        binding.tilAmount.error = null
        binding.tilDate.error = null

        val concept = binding.etConcept.text?.toString()?.trim().orEmpty()
        val category = binding.etCategory.text?.toString()?.trim().orEmpty()
        val rawAmount = binding.etAmount.text?.toString()?.trim().orEmpty()
        val date = binding.etDate.text?.toString()?.trim().orEmpty()

        val dateRegex = Regex("""^\d{4}-\d{2}-\d{2}$""")
        var valid = true

        if (concept.isEmpty()) {
            binding.tilConcept.error = getString(R.string.err_required)
            valid = false
        }

        if (category.isEmpty()) {
            binding.tilCategory.error = getString(R.string.err_required)
            valid = false
        }

        val amount = rawAmount.toDoubleOrNull()
        if (rawAmount.isEmpty()) {
            binding.tilAmount.error = getString(R.string.err_required)
            valid = false
        } else if (amount == null || amount <= 0.0) {
            binding.tilAmount.error = getString(R.string.err_amount)
            valid = false
        }

        if (date.isEmpty()) {
            binding.tilDate.error = getString(R.string.err_required)
            valid = false
        } else if (!dateRegex.matches(date)) {
            binding.tilDate.error = getString(R.string.err_date)
            valid = false
        }

        if (!valid || amount == null) return false

        if (editingExpenseId != -1) {
            ExpenseRepository.updateExpense(
                id = editingExpenseId,
                concept = concept,
                category = category,
                amount = amount,
                date = date,
                onSuccess = {
                    Toast.makeText(this, "Gasto actualizado en Firestore", Toast.LENGTH_SHORT).show()
                },
                onFailure = { e ->
                    Log.e("FirestoreSave", "Error al actualizar", e)
                    Toast.makeText(this, "Error al guardar: ${e.message}", Toast.LENGTH_LONG).show()
                }
            )
        } else {
            ExpenseRepository.addExpense(
                concept = concept,
                category = category,
                amount = amount,
                date = date,
                onSuccess = {
                    Toast.makeText(this, "Gasto guardado en Firestore", Toast.LENGTH_SHORT).show()
                },
                onFailure = { e ->
                    Log.e("FirestoreSave", "Error al guardar", e)
                    Toast.makeText(this, "Error al guardar: ${e.message}", Toast.LENGTH_LONG).show()
                }
            )
        }
        return true
    }

    companion object {
        const val EXTRA_EXPENSE_ID = "extra_expense_id"
    }
}