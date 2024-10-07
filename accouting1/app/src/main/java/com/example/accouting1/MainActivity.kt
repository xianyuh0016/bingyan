package com.example.accouting1

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var billAdapter: BillAdapter
    private val bills: MutableList<Bill> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        val floatingActionButton = findViewById<FloatingActionButton>(R.id.floatingActionButton)
        floatingActionButton.setOnClickListener {
            val intent = Intent(this, BillActivity::class.java)
            startActivity(intent)
        }
        // 初始化 DatabaseHelper
        dbHelper = DatabaseHelper(this)

        // 初始化 RecyclerView
        recyclerView = findViewById(R.id.recyclerview)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // 从数据库加载账单数据
        bills.addAll(loadBillsFromDatabase())

        // 初始化适配器并设置给 RecyclerView
        billAdapter = BillAdapter(bills, dbHelper)
        recyclerView.adapter = billAdapter // 将适配器设置给 RecyclerView

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun loadBillsFromDatabase(): List<Bill> {
        val bills = mutableListOf<Bill>()
        val db = dbHelper.readableDatabase
        val cursor = db.query(DatabaseHelper.TABLE_NAME, null, null, null, null, null, null)

        while (cursor.moveToNext()) {
            val id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ID))
            val amount = cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_AMOUNT))
            val purpose = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PURPOSE))
            val time = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_TIME))
            bills.add(Bill(id, amount, purpose, time))
        }

        cursor.close()
        return bills
    }


}