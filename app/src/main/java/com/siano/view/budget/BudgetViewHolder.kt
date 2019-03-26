package com.siano.view.budget

import android.view.View
import com.siano.base.DefinedViewHolder
import kotlinx.android.synthetic.main.item_budget_contact.view.*

class BudgetViewHolder(itemView: View) : DefinedViewHolder<BudgetAdapterItem>(itemView) {

    override fun bindStatic(item: BudgetAdapterItem) {
        itemView.budget_contact_name.text = item.name
        itemView.budget_contact_amount.text = item.amount.toString()
    }
}
