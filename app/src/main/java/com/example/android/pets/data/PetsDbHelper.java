package com.example.android.pets.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;

public class PetsDbHelper extends SQLiteOpenHelper {

    public static int VERSION = 1;
    public static final String NAME = "Pets";
    public static final String CREATE_TABLE = "CREATE TABLE " + PetsContract.PetsEntry.TABLE_NAME + " (" +
            PetsContract.PetsEntry._ID + " INTEGER " + "PRIMARY KEY " + "AUTOINCREMENT, " + PetsContract.PetsEntry.COLUMN_PET_NAME + " TEXT, " +
            PetsContract.PetsEntry.COLUMN_PET_BREED + " TEXT, " + PetsContract.PetsEntry.COLUMN_PET_GENDER + " INTEGER, " +
            PetsContract.PetsEntry.COLUMN_PET_WEIGHT + " INTEGER);";

    public PetsDbHelper(@Nullable Context context) {
        super(context, NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        sqLiteDatabase.execSQL(CREATE_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
