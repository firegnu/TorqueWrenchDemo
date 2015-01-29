package com.example.firegnu.torquewrenchdemo;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class DataBaseHelper extends SQLiteOpenHelper{

    private SQLiteDatabase myDataBase;

    private final Context myContext;

    private final String mydbname;

    /**
     * Constructor
     * Takes and keeps a reference of the passed context in order to access to the application assets and resources.
     * @param context
     */
    public DataBaseHelper(Context context,String dbname) {
        super(context, dbname, null, 1);
        this.myContext = context;
        this.mydbname=dbname;
    }

    /**
     * Creates a empty database on the system and rewrites it with your own database.
     * */
    public SQLiteDatabase openOrCreateDataBase(String src) throws IOException{
        boolean dbExist = checkDataBase();
        if(dbExist){
        }else{
            //By calling this method and empty database will be created into the default system path
            //of your application so we are gonna be able to overwrite that database with our database.
            this.getReadableDatabase();
            try {
                copyDataBase(src);
            } catch (IOException e) {
                throw new Error("Error copying database");
            }
        }
        this.myDataBase=myContext.openOrCreateDatabase(mydbname, Context.MODE_PRIVATE, null);
        return this.myDataBase;
    }

    @Override
    public synchronized void close() {
        if(myDataBase != null)
            myDataBase.close();
        super.close();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    /**
     * Check if the database already exist to avoid re-copying the file each time you open the application.
     * @return true if it exists, false if it doesn't
     */

    private boolean checkDataBase(){

        SQLiteDatabase checkDB = null;
        try{
            String myPath =this.myContext.getDatabasePath(mydbname).getPath();
            Log.i("DataBase", myPath);
            checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
        }catch(SQLiteException e){
        }
        if(checkDB != null){
            checkDB.close();
        }
        return checkDB != null ? true : false;
    }
    /**
     * Copies your database from your local assets-folder to the just created empty database in the
     * system folder, from where it can be accessed and handled.
     * This is done by transfering bytestream.
     */
    @SuppressLint("SdCardPath")
    private void copyDataBase(String src) throws IOException{
        //Open your local db as the input stream
        InputStream myInput =null;
        if (TextUtils.isEmpty(src)){
            myInput = myContext.getAssets().open(mydbname);
        } else	{
            myInput =new FileInputStream(new File(src));
        }
        // Path to the just created empty db
        String outFileName = "/data/data/".concat(this.myContext.getPackageName())
                .concat("/databases/").concat(mydbname);
        //Open the empty db as the output stream
        OutputStream myOutput = new FileOutputStream(outFileName);
        //transfer bytes from the inputfile to the outputfile
        byte[] buffer = new byte[1024];
        int length;
        while ((length = myInput.read(buffer))>0){
            myOutput.write(buffer, 0, length);
        }
        //Close the streams
        myOutput.flush();
        myOutput.close();
        myInput.close();
    }

    public Cursor rawQuery (String sql, String[] selectionArgs) {
        return myDataBase.rawQuery(sql,selectionArgs);
    }

    /*

    public void openDataBase() throws SQLException{

    	//Open the database
        String myPath = DB_PATH + mydbname;
    	myDataBase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
    }
        // Add your public helper methods to access and get content from the database.
       // You could return cursors by doing "return myDataBase.query(....)" so it'd be easy
       // to you to create adapters for your views.
 */
}
