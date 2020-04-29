package com.test.nfc_demo.sql;

/**
 *  A helper class used to execute the SQL statements on SQLite.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.test.nfc_demo.pojo.ContactInfo;

import java.util.ArrayList;

public class SQLHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "contact.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_NAME = "contactInfo";
    private static final String KEY_ID = " id integer primary key autoincrement";
    private static final String KEY_NAME = "name";
    private static final String KEY_NUMBER = "number";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_URL = "url";
    private static final String KEY_ADDRESS = "address";


    private static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "("
            + KEY_ID + ","
            + KEY_NAME + " TEXT not null,"
            + KEY_NUMBER + " TEXT not null,"
            + KEY_URL + " TEXT not null,"
            + KEY_EMAIL + " TEXT not null,"
            + KEY_ADDRESS + " TEXT not null);";


    private ContentValues values;
    private Cursor cursor;


    public SQLHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    //called to create table
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    //called when database version has changed
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        if (oldVersion >= newVersion) return;
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    //add contact to database
    public void addContact(ContactInfo item) {
        SQLiteDatabase db = this.getWritableDatabase();
        values = new ContentValues();
        values.put(KEY_NAME, item.getName());
        values.put(KEY_NUMBER, item.getNumber());
        values.put(KEY_EMAIL, item.getEmail());
        values.put(KEY_URL, item.getUrl());
        values.put(KEY_ADDRESS, item.getAddress());
        db.insert(TABLE_NAME, null, values);
        db.close();
    }

    //delete contact from database
    public void deleteContact(ContactInfo item){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, KEY_NAME + "=?", new String[] {item.getName()});
        db.close();
    }


    //query database and return ArrayList of all contacts
    public ArrayList<ContactInfo> getContactList() {
        ArrayList<ContactInfo> contactList = new ArrayList<>();

        SQLiteDatabase db = this.getWritableDatabase();
        cursor = db.query(TABLE_NAME,
                new String[]{KEY_NAME, KEY_NUMBER, KEY_EMAIL, KEY_URL, KEY_ADDRESS},
                null, null, null, null, KEY_NAME);
        //write contents of Cursor to list
        while (cursor.moveToNext()) {
            String strName = cursor.getString(cursor.getColumnIndex(KEY_NAME));
            String phoneNumber = cursor.getString(cursor.getColumnIndex(KEY_NUMBER));
            String strEmail = cursor.getString(cursor.getColumnIndex(KEY_EMAIL));
            String strUrl = cursor.getString(cursor.getColumnIndex(KEY_URL));
            String strAddress = cursor.getString(cursor.getColumnIndex(KEY_ADDRESS));
            contactList.add(new ContactInfo(strName, phoneNumber, strEmail, strUrl, strAddress));
        }
        db.close();
        return contactList;
    }


    //query database and return ArrayList of all contacts
    public ArrayList<String> getContactNamesList() {
        ArrayList<String> contactList = new ArrayList<>();

        SQLiteDatabase db = this.getWritableDatabase();
        cursor = db.query(TABLE_NAME,
                new String[]{KEY_NAME},
                null, null, null, null, KEY_NAME);

        //write contents of Cursor to list
        while (cursor.moveToNext()) {
            String strName = cursor.getString(cursor.getColumnIndex(KEY_NAME));
            contactList.add(strName);
        }
        db.close();
        return contactList;
    }
}
