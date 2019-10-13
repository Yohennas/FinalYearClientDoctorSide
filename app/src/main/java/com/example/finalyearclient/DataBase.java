package com.example.finalyearclient;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.ResultSet;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

public class DataBase extends SQLiteOpenHelper {
    //private static  final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "PatientDatabase.db";

    private String TABLE;
    private String password;
    private static final String Date_Time = "date_and_time";
    private static final String NAME = "name";
    private static final String PASSWORD = "password";
    private static final String AGE = "age";
    private static final String HEIGHT = "height";
    private static final String WEIGHT = "weight";
    private static final String SEX = "sex";
    private static final String Blood_Pressure = "blood_pressure";
    private static final String Respiration_Rate = "respiration_rate";
    private static final String Blood_Oxygen = "blood_oxygen";
    private static final String Heart_Rate = "heart_rate";

    SQLiteDatabase db;

    //at sign in want new table, at login just wanna check existing tables

    DataBase(Context context, String PatientName,int version, String Password) {
        super(context, DATABASE_NAME, null, version);
        TABLE = PatientName;
        password = Password;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "create table " + TABLE + "(date_and_time BLOB , name BLOB, " +
                "password BLOB, age BLOB , height BLOB , weight BLOB , sex BLOB, blood_pressure BLOB" +
                ",respiration_rate BLOB,blood_oxygen BLOB,heart_rate BLOB);";

        db.execSQL(createTable);
        this.db = db;

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String query = "DROP TABLE IF EXISTS "+TABLE;
        db.execSQL(query);
        onCreate(db);
        this.db = db;
    }

    public byte[] databaseEncrypt(String input) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update(password.getBytes());
        byte[] MDkey = md.digest();
        SecretKeySpec key = new SecretKeySpec(MDkey, "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE,key);
        //Log.e("EN Key",key+"");
        byte[] encrypted = cipher.doFinal(input.getBytes());
        return encrypted;
    }


    public String databaseDecrypt(byte[] encrypted) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update(password.getBytes());
        byte[] MDkey = md.digest();
        SecretKeySpec key = new SecretKeySpec(MDkey, "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE,key);
        //Log.e("DE Key",key+"");
        //byte[] ByteDecrypt = Base64.decode(encrypted,Base64.DEFAULT);
        byte[] decrypted = cipher.doFinal(encrypted);
        return new String(decrypted);
    }


    public void addInstance(Pateint p) {
        db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date date = new Date();
        try {
            values.put(Date_Time,databaseEncrypt(df.format(date)));
            values.put(NAME, databaseEncrypt(p.getName()));
            values.put(PASSWORD, databaseEncrypt(p.getPassword()));
            values.put(AGE, databaseEncrypt(p.getAge()));
            values.put(HEIGHT, databaseEncrypt(p.getHeight()));
            values.put(WEIGHT, databaseEncrypt(p.getWeight()));
            values.put(SEX, databaseEncrypt(p.getGender()));
            values.put(Blood_Pressure, databaseEncrypt(p.getBloodPressure()));
            values.put(Respiration_Rate, databaseEncrypt(p.getRespirationRate()));
            values.put(Blood_Oxygen, databaseEncrypt(p.getBloodOxygen()));
            values.put(Heart_Rate, databaseEncrypt(p.getHeartRate()));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        }
        db.insert(TABLE, null, values);
        db.close();
    }

    public int checkTable(){ //use this
        db = this.getReadableDatabase();
        String sql = "SELECT name FROM sqlite_master WHERE type='table'AND name='"+TABLE+"'";
        Cursor mCursor = db.rawQuery(sql, null);
        if (mCursor.getCount() > 0) {
            return mCursor.getCount();
        }
        mCursor.close();
        return mCursor.getCount();
    }

    public int checkSize(){
        db = this.getReadableDatabase();
        String sql =  "select *, sum(length(date_and_time)+length(name)+length(password)+length(age)+length(weight)+length(height)+length(sex)" +
                "+length(blood_pressure)+length(respiration_rate)+length(blood_oxygen)+length(heart_rate)) from "+ TABLE;
        Cursor cursor = db.rawQuery(sql, null);
        cursor.moveToLast();
        return cursor.getInt(0);
    }


    public String stringValue(String value) throws IllegalBlockSizeException, InvalidKeyException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException {
        db = this.getReadableDatabase();
        String query = "select "+value+" from "+TABLE;
        Cursor cursor = db.rawQuery(query, null);
        cursor.moveToFirst();
        byte[] toDecrypt = cursor.getBlob(cursor.getColumnIndex(value));
        return databaseDecrypt(toDecrypt);
    }

    /*public int intValue(String value){
        db = this.getReadableDatabase();
        String query = "select "+value+" from "+TABLE;
        Cursor cursor = db.rawQuery(query, null);
        cursor.moveToFirst();
        return cursor.getInt(cursor.getColumnIndex(value));
    }*/

    public Cursor getAll(){
        db = this.getReadableDatabase();
        String query = "select * from "+TABLE;
        Cursor cursor = db.rawQuery(query, null);
        return cursor;
    }


}

