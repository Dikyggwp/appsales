package com.app.mobiledev.salesapp.database;

import android.content.ContentValues;
        import android.content.Context;
        import android.database.Cursor;
        import android.database.sqlite.SQLiteDatabase;
        import android.database.sqlite.SQLiteOpenHelper;
        import java.text.SimpleDateFormat;
        import java.util.ArrayList;
        import java.util.Date;
        import java.util.HashMap;
        import java.util.Locale;

public class DatabaseAccess {
    private SQLiteOpenHelper openHelper;
    private SQLiteDatabase database;
    private static DatabaseAccess instance;

    /**
     * Private constructor to avoid object creation from outside classes.
     *
     * @param context
     */
    private DatabaseAccess(Context context) {
        this.openHelper = new DatabaseOpenHelper(context);
    }

    /**
     * Return a singleton instance of DatabaseAccess.
     *
     * @param context the Context
     * @return the instance of DabaseAccess
     */
    public static DatabaseAccess getInstance(Context context) {


        if (instance == null) {
            instance = new DatabaseAccess(context);
        }
        return instance;
    }

    /**
     * Open the database connection.
     */
    public void open() {
        this.database = openHelper.getWritableDatabase();
    }

    /**
     * Close the database connection.
     */
    public void close() {
        if (database != null) {
            this.database.close();
        }
    }
    //get shop information
    public ArrayList<HashMap<String, String>> adsInformation(String id_branch) {
        ArrayList<HashMap<String, String>> shopInfo = new ArrayList<>();
        Cursor cursor = database.rawQuery("SELECT * FROM Config where id_branch='"+id_branch+"' and url_video<>''", null);
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<>();
                map.put("id_branch", cursor.getString(6));
                map.put("branch", cursor.getString(5));
                map.put("expired", cursor.getString(7));
                map.put("url_video", cursor.getString(1));
                map.put("url_video2", cursor.getString(2));
                map.put("url_image", cursor.getString(3));
                map.put("url_image2", cursor.getString(4));
                map.put("status", cursor.getString(8));
                map.put("jenis", cursor.getString(9));
                map.put("id_ads", cursor.getString(10));

                shopInfo.add(map);
            } while (cursor.moveToNext());
        }
        cursor.close();
        database.close();
        return shopInfo;
    }
    // get ads nonaktif
    public ArrayList<HashMap<String, String>> getads_nonaktif(String id_branch) {
        ArrayList<HashMap<String, String>> shopInfo = new ArrayList<>();
        Cursor cursor = database.rawQuery("SELECT * FROM Config where id_branch='"+id_branch+"' and url_video<>'' and status='0'", null);
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<>();
                map.put("id_branch", cursor.getString(6));
                map.put("branch", cursor.getString(5));
                map.put("expired", cursor.getString(7));
                map.put("url_video", cursor.getString(1));
                map.put("url_video2", cursor.getString(2));
                map.put("url_image", cursor.getString(3));
                map.put("url_image2", cursor.getString(4));
                map.put("status", cursor.getString(8));
                map.put("jenis", cursor.getString(9));
                map.put("id_ads", cursor.getString(10));

                shopInfo.add(map);
            } while (cursor.moveToNext());
        }
        cursor.close();
        database.close();
        return shopInfo;
    }

    // get ads aktif
    public ArrayList<HashMap<String, String>> getads_aktif(String id_branch) {
        ArrayList<HashMap<String, String>> shopInfo = new ArrayList<>();
        Cursor cursor = database.rawQuery("SELECT * FROM Config where id_branch='"+id_branch+"' and url_video<>'' and status='1'", null);
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<>();
                map.put("id_branch", cursor.getString(6));
                map.put("branch", cursor.getString(5));
                map.put("expired", cursor.getString(7));
                map.put("url_video", cursor.getString(1));
                map.put("url_video2", cursor.getString(2));
                map.put("url_image", cursor.getString(3));
                map.put("url_image2", cursor.getString(4));
                map.put("status", cursor.getString(8));
                map.put("jenis", cursor.getString(9));
                map.put("id_ads", cursor.getString(10));

                shopInfo.add(map);
            } while (cursor.moveToNext());
        }
        cursor.close();
        database.close();
        return shopInfo;
    }

    public int getCount(String id_branch) {
        int jml = 0;
        Cursor cursor = database.rawQuery("SELECT count(*) jml FROM Config WHERE id_branch="+id_branch+" and url_video<>'' and status='1'", null);
        if (cursor.moveToFirst()) {
            do {
                jml = cursor.getInt(0);
            } while (cursor.moveToNext());
        }
        cursor.close();
        database.close();
        return jml;
    }

    //insert customer
    public boolean add_ads(String branch_id, String branch, String url_video, String url_image, String expired, String status, String jenis) {
        ContentValues values = new ContentValues();
        values.put("id_branch", branch_id);
        values.put("branch", branch);
        values.put("url_video", url_video);
        values.put("url_image", url_image);
        values.put("expired", expired);
        values.put("status", status);
        values.put("jenis", jenis);
        long check = database.insert("Config", null, values);

        //if data insert success, its return 1, if failed return -1
        if (check == -1) {
            return false;
        } else {
            return true;
        }
    }

}