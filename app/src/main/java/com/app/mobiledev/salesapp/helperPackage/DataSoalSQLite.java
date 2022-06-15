package com.app.mobiledev.salesapp.helperPackage;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

public class DataSoalSQLite extends SQLiteOpenHelper {

    static abstract class MyColumns implements BaseColumns {
        static final String TabelSoal = "trsoal";
    }
        private static final String DATABASE_NAME = "soal.db";
        private static final int DATABASE_VERSION = 1;

    private static final String tblken = " CREATE TABLE trsoal (\n" +
            "  id_auto  INTEGER PRIMARY KEY ,\n" +
            "  id_soal TEXT(11),\n" +
            "  soal TEXT(255),\n" +
            "  a TEXT(255),\n" +
            "  b TEXT(255),\n" +
            "  c TEXT(255),\n" +
            "  d TEXT(255),\n" +
            "  kunci TEXT(1),\n" +
            "  tanggal TEXT(255),\n" +
            "  aktif TEXT(10)\n"+
            ")";

    private static final String tblkaryawan = " CREATE TABLE tblkaryawan (\n" +
            "  id_auto  INTEGER PRIMARY KEY AUTOINCREMENT ,\n" +
            "  kyano TEXT(11),\n" +
            "  nama TEXT(255),\n" +
            "  divisi TEXT(255)\n" +
            ")";



    private static final  String tbljawaban=" CREATE TABLE trjawaban (\n" +
            " idauto INTEGER PRIMARY KEY AUTOINCREMENT,\n"+
            " id_soal TEXT(255),\n" +
            " jawaban TEXT(255)\n" +
            ")";



    private static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS "+ MyColumns.TabelSoal;


    public DataSoalSQLite(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(tblken);
        db.execSQL(tbljawaban);
        db.execSQL(tblkaryawan);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }
}