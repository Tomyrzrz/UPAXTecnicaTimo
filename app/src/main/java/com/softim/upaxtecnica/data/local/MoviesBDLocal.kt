package com.softim.upaxtecnica.data.local

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class MoviesBDLocal (context: Context, name: String, factory: SQLiteDatabase.CursorFactory?, version: Int) : SQLiteOpenHelper(context, name, factory, version) {
    //Base de datos en SQLite para almacenamiento local.
    //Sin utilizar aun.
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("create table movies (id int primary key, title text, poster_path text, overview text, " +
                "vote_average text)")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {

    }
}