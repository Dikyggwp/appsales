package com.app.mobiledev.salesapp.database;

import android.content.Context;
        import android.database.sqlite.SQLiteDatabase;
        import android.database.sqlite.SQLiteOpenHelper;
        import android.provider.BaseColumns;

public class DatabaseOpenHelper extends SQLiteOpenHelper {

    static abstract class MyColumns implements BaseColumns {
        static final String TabelKendaraan = "likuid";
    }
    private static final String DATABASE_NAME = "likuid.db";
    private static final int DATABASE_VERSION = 1;

    private static final String tblConfig = "CREATE TABLE mst_outlet (\n" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
            "id_outlet  INTEGER,\n" +
            "    outlet  TEXT(200),\n" +
            "    pic  TEXT(200),\n" +
            "    telpon  TEXT(200),\n" +
            "    alamat  TEXT(200),\n" +
            "    kode_area  TEXT(255),\n" +
            "    foto_npwp  TEXT(10),\n" +
            "    npwp  TEXT(12),\n" +
            "    status  TEXT(10)\n" +
            ")";

    private static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS "+ MyColumns.TabelKendaraan;


    public DatabaseOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(tblConfig);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }
}