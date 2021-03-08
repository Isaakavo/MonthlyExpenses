package adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.monthlyexpenses.R
import data.Expenses
import java.text.DateFormat

class ExpenseListAdapter(private val onEditSelected: OnEditSelectedListener,
                         private val onClickListener: OnClickListener) :
    ListAdapter<Expenses, RecyclerView.ViewHolder>(ExpenseComparator()) {

    lateinit var expense: Expenses

    interface OnEditSelectedListener {
        fun sendExpenseToEdit(expense: Expenses)
        fun sendExpenseToDelete(expense: Expenses)
    }

    interface OnClickListener {
        fun onExpenseItemSelected(expense: Expenses)
    }

    private val SHOW_MENU = 1
    private val HIDE_MENU = 2

    override fun getItemViewType(position: Int): Int {
        val expenseItem = getItem(position)
        return if (expenseItem.showMenu) {
            SHOW_MENU
        } else {
            HIDE_MENU
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == SHOW_MENU){
            MenuViewHolder.create(parent)
        }else{
            ExpenseViewHolder.create(parent)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        expense = getItem(position)
        if (holder is ExpenseViewHolder){
            if (position != 0) {
                val expenseDateFormatted = holder.setDateFormat(expense.date)
                val previousExpense = getItem(position - 1)
                val previousExpenseDate = holder.setDateFormat(previousExpense.date)
                holder.setSectionDate(expenseDateFormatted, previousExpenseDate, expense)
                holder.itemView.setOnClickListener {
                    onClickListener.onExpenseItemSelected(
                        getItem(
                            position
                        )
                    )
                }
            } else if (position == 0) {
                holder.bind(expense)
                holder.itemView.setOnClickListener {
                    onClickListener.onExpenseItemSelected(
                        getItem(
                            position
                        )
                    )
                }
            }
        }else if (holder is MenuViewHolder){
            holder.buttonEdit?.setOnClickListener{
                holder.editItem(getItem(position), onEditSelected)
            }
            holder.deleteButton?.setOnClickListener{
                holder.deleteItem(getItem(position), onEditSelected)
            }
        }
    }

    class ExpenseViewHolder(view: View): RecyclerView.ViewHolder(view){
        private val tvExpense: TextView = view.findViewById(R.id.tvExpenseTitle)
        private val tvTotal: TextView = view.findViewById(R.id.tvTotal)
        private val tvSectionDate: TextView = view.findViewById(R.id.sectionDate)

        fun bind(expense: Expenses){
            tvExpense.text = expense.concept
            tvTotal.text = "$" + expense.total.toString()
            tvSectionDate.text = setDateFormat(expense.date)
        }

        fun setSectionDate(actualExpenseDate: String, previousExpenseDate: String,
                           expense: Expenses){
                if (actualExpenseDate == previousExpenseDate){
                    tvSectionDate.visibility = View.GONE
                    bind(expense)
                }else{
                    tvSectionDate.visibility = View.VISIBLE
                    bind(expense)
                }
        }
        fun setDateFormat(timestamp: Long): String{
            return DateFormat.getDateInstance().format(timestamp)
        }

        companion object {
            fun create(parent: ViewGroup): ExpenseViewHolder {
                val view: View = LayoutInflater.from(parent.context)
                    .inflate(R.layout.recyclerview_item, parent, false)
                return ExpenseViewHolder(view)
            }
        }
    }

    class MenuViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val buttonEdit: LinearLayout? = view.findViewById(R.id.editButton)
        val deleteButton: LinearLayout? = view.findViewById(R.id.deleteButton)
        fun editItem(expense: Expenses, listener: OnEditSelectedListener) {
            listener.sendExpenseToEdit(expense)
        }

        fun deleteItem(expense: Expenses, listener: OnEditSelectedListener) {
            listener.sendExpenseToDelete(expense)
        }

        companion object {
            fun create(parent: ViewGroup): MenuViewHolder {
                val view: View = LayoutInflater.from(parent.context)
                    .inflate(R.layout.recycler_menu, parent, false)
                return MenuViewHolder(view)
            }
        }
    }

    class ExpenseComparator: DiffUtil.ItemCallback<Expenses>() {
        override fun areItemsTheSame(oldItem: Expenses, newItem: Expenses): Boolean {
            return oldItem.id == newItem.id
        }
        override fun areContentsTheSame(oldItem: Expenses, newItem: Expenses): Boolean {
            return oldItem.date == newItem.date
        }
    }

    fun showMenu(position: Int){
        val expenseList = currentList
        for (i in 0 until expenseList.size){
            expenseList[i].showMenu = false
        }
        expenseList[position].showMenu = true
        notifyDataSetChanged()
    }
    fun closeMenu(){
        val expenseList = currentList
        for (i in 0 until expenseList.size){
            expenseList[i].showMenu= false
        }
        notifyDataSetChanged()
    }
}