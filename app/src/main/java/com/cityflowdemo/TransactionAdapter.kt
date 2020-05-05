package com.cityflowdemo

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.cityflowdemo.databinding.ItemUnconfirmedTransactionBinding
import com.cityflowdemo.model.Transaction


class TransactionAdapter : RecyclerView.Adapter<TransactionAdapter.Holder>() {
    private var list = ArrayList<Transaction>()

    inner class Holder(val binding: ItemUnconfirmedTransactionBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.item_unconfirmed_transaction,
                parent, false
            )
        )
    }

    override fun getItemCount(): Int = if (list.size > 5) 5 else list.size

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.binding.transaction = list[position]
    }

    fun setList(list: ArrayList<Transaction>) {
        this.list = list
//        this.list.sortedWith(compareBy<Transaction>(Transaction::time)).reversed()
        notifyDataSetChanged()
    }


}