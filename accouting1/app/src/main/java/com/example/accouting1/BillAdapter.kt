package com.example.accouting1

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class BillAdapter(private val bills: MutableList<Bill>,private val dbHelper: DatabaseHelper) : RecyclerView.Adapter<BillAdapter.BillViewHolder>() {
    //继承RecyclerView.Adapter

    inner class BillViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        //内部类，这个类的作用是为 RecyclerView 中的每个项目视图提供一个容器，用于存储和管理项目视图中的各个组件。
        val amountTextView: TextView = itemView.findViewById(R.id.amountTextView)
        val purposeTextView: TextView = itemView.findViewById(R.id.purposeTextView)
        val timeTextView: TextView = itemView.findViewById(R.id.timeTextView)

        fun bind(bill: Bill) {
            // 这个函数用于将数据绑定到 ViewHolder 上。
            amountTextView.text = bill.amount.toString()
            // 将账单的金额转换为字符串后，设置为 amountTextView 的文本。
            val formattedInput = "-${amountTextView.text}"
            amountTextView.text = formattedInput
            //加上-号
            purposeTextView.text = bill.purpose
            timeTextView.text = bill.time
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BillViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.bill_item_layout, parent, false)
        return BillViewHolder(view)
    }
    //创建并返回一个新的ViewHolder实例，用于在 RecyclerView 中展示数据项。

    override fun onBindViewHolder(holder: BillViewHolder, position: Int) {
        val bill = bills[position]
        // 获取当前需要展示的账单数据。
        holder.bind(bill)
        // 通过调用 ViewHolder 的 bind 函数来绑定账单数据到视图。

        // 为 itemView 设置长按事件监听器
        holder.itemView.setOnLongClickListener {
            val dialogView =
                LayoutInflater.from(holder.itemView.context).inflate(R.layout.dialog_layout, null)
            val dialog = AlertDialog.Builder(holder.itemView.context)
                .setView(dialogView)
                .create()
            dialog.show()

            // 获取 dialogView 中的按钮并设置点击事件
            dialogView.findViewById<Button>(R.id.button1).setOnClickListener {
                if (position != RecyclerView.NO_POSITION) {
                    val success = dbHelper.deleteBillById(bill.id) // 删除数据库中的记录
                    if (success) {
                        bills.removeAt(position)  // 删除内存中的记录
                        notifyItemRemoved(position) // 通知 RecyclerView 数据已改变
                        Toast.makeText(holder.itemView.context, "删除成功", Toast.LENGTH_SHORT)
                            .show()
                    } else {
                        Toast.makeText(holder.itemView.context, "删除失败", Toast.LENGTH_SHORT)
                            .show()
                    }
                    dialog.dismiss()
                }
            }
            dialogView.findViewById<Button>(R.id.button2).setOnClickListener {
                val editDialogView =
                    LayoutInflater.from(holder.itemView.context).inflate(R.layout.bill_layout1, null)
                val editDialog = AlertDialog.Builder(holder.itemView.context)
                    .setView(editDialogView)
                    .setTitle("编辑账单")
                    .setPositiveButton("保存") { _, _ ->
                        // 获取用户输入的修改值
                        val newAmount =
                            editDialogView.findViewById<TextView>(R.id.editText1).text.toString()
                                .toDouble()
                        val newPurpose =
                            editDialogView.findViewById<TextView>(R.id.editText2).text.toString()
                        val newTime =
                            editDialogView.findViewById<TextView>(R.id.editText3).text.toString()

                        // 更新数据库
                        GlobalScope.launch {
                            try {
                                val updatedBill = Bill(bill.id, newAmount, newPurpose, newTime)
                                val success = dbHelper.updateBill(updatedBill)
                                if (success) {
                                    // 更新内存中的账单并刷新 RecyclerView
                                    bills[position] = updatedBill
                                    (holder.itemView.context as MainActivity).runOnUiThread {
                                        notifyItemChanged(position)
                                        Toast.makeText(
                                            holder.itemView.context,
                                            "修改成功",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                } else {
                                    (holder.itemView.context as MainActivity).runOnUiThread {
                                        Toast.makeText(
                                            holder.itemView.context,
                                            "修改失败",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }


                            } finally {

                            }
                        }
                    }
                    .setNegativeButton("取消", null)
                    .create()
                editDialog.show()

                // 设置默认值到编辑对话框中
                editDialogView.findViewById<TextView>(R.id.editText1).text = bill.amount.toString()
                editDialogView.findViewById<TextView>(R.id.editText2).text = bill.purpose
                editDialogView.findViewById<TextView>(R.id.editText3).text = bill.time

                dialog.dismiss() // 关闭原有的对话框
            }
            true
        }


    }

    //将数据绑定到特定位置的 ViewHolder 上，以便在 RecyclerView 中正确显示数据。
    override fun getItemCount(): Int = bills.size
}
