package com.example.cancionesbd;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class SQLiteBaseDeDatos extends SQLiteOpenHelper {

    public SQLiteBaseDeDatos(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version){
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase BaseDatos) {

        BaseDatos.execSQL("create table canciones(" +
                "titulo text primary key," +
                "autor text," +
                "tonalidad text," +
                "letra_cancion text," +
                "foto text)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
