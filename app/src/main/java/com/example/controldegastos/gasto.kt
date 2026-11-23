package com.example.controldegastos.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.controldegastos.data.Expense
import com.example.controldegastos.databinding.ActivityGastoBinding
import java.util.Locale


class ExpenseAdapter(
    private var items: List<Expense>,
    private val onExpenseClick: (Expense) -> Unit,
    private val onDeleteClick: (Expense) -> Unit
) : RecyclerView.Adapter<ExpenseAdapter.ExpenseViewHolder>() {

    inner class ExpenseViewHolder(
        private val binding: ActivityGastoBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(expense: Expense) {
            binding.tvConcept.text = expense.concept
            binding.tvCategoryDate.text = "${expense.category}  •  ${expense.date}"
            binding.tvAmount.text = String.format(Locale.getDefault(), "- $ %.2f", expense.amount)

            binding.root.setOnClickListener { onExpenseClick(expense) }
            binding.btnDeleteExpense.setOnClickListener { onDeleteClick(expense) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpenseViewHolder {
        val binding = ActivityGastoBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ExpenseViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ExpenseViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    fun updateExpenses(newItems: List<Expense>) {
        items = newItems
        notifyDataSetChanged()
    }
}