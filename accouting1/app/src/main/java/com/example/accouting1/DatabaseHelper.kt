package com.example.accouting1

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
//继承自SQLiteOpenHelper
class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "mydatabase.db"
        private const val DATABASE_VERSION = 2
        const val TABLE_NAME = "bills"
        const val COLUMN_ID = "id"
        const val COLUMN_AMOUNT = "amount"
        const val COLUMN_PURPOSE = "purpose"
        const val COLUMN_TIME = "time"
    }

    //创建数据库，初始化数据
    override fun onCreate(db: SQLiteDatabase) {
        val createTableQuery = "CREATE TABLE IF NOT EXISTS $TABLE_NAME " +
                "($COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, $COLUMN_AMOUNT REAL, $COLUMN_PURPOSE TEXT, $COLUMN_TIME TEXT)"
        db.execSQL(createTableQuery)
    }

    // 处理数据库升级，如果需要的话
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {

    }

    fun deleteBillById(id: Int): Boolean {
        val db = this.writableDatabase
        return db.delete(TABLE_NAME, "$COLUMN_ID=?", arrayOf(id.toString())) > 0
    }

    fun updateBill(bill: Bill): Boolean {
        val db = this.writableDatabase
        val contentValues = ContentValues().apply {
            put(COLUMN_AMOUNT, bill.amount)
            put(COLUMN_PURPOSE, bill.purpose)
            put(COLUMN_TIME, bill.time)
        }
        val rows = db.update(TABLE_NAME, contentValues, "$COLUMN_ID=?", arrayOf(bill.id.toString()))
        return rows > 0
    }
}