package com.example.accouting1
import android.content.ContentValues
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView

class BillActivity : AppCompatActivity() {

    private lateinit var amountEditText: EditText
    private lateinit var purposeEditText: EditText
    private lateinit var timeEditText: EditText
    private lateinit var saveButton: Button
    private lateinit var dbHelper: DatabaseHelper
    private  lateinit var recyclerview: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.bill_layout)

        dbHelper = DatabaseHelper(this)

        amountEditText = findViewById(R.id.editText1)
        purposeEditText = findViewById(R.id.editText2)
        timeEditText = findViewById(R.id.editText3)
        saveButton = findViewById(R.id.savebutton)

        saveButton.setOnClickListener {
            val amount = amountEditText.text.toString().toDoubleOrNull()
            val purpose = purposeEditText.text.toString()
            val time = timeEditText.text.toString()

            if (amount!= null) {
                saveBillToDatabase(amount, purpose, time)
                setResult(RESULT_OK)
                finish()
            }
        }
    }
    //将给定的金额、用途和时间数据保存到数据库中。
    private fun saveBillToDatabase(amount: Double, purpose: String, time: String) {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(DatabaseHelper.COLUMN_AMOUNT, amount)
            put(DatabaseHelper.COLUMN_PURPOSE, purpose)
            put(DatabaseHelper.COLUMN_TIME, time)
        }
        db.insert(DatabaseHelper.TABLE_NAME, null, values)
        recyclerview.invalidate()//强制刷新
    }
}