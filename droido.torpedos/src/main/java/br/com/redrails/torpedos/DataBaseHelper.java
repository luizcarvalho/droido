package br.com.redrails.torpedos;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.Context;
import android.database.DatabaseUtils;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


import br.com.redrails.torpedos.util.DataBaseUpgrade;


public class DataBaseHelper extends SQLiteOpenHelper{

    //The Android's default system path of your application database.
    public static String DB_PATH = "/data/data/br.com.redrails.torpedos/databases/";

    private static String DB_NAME = "database.sqlite";
    public static String TEMP_DB_NAME = "database_temp.sqlite";
    private static int DB_VERSION=22;//change to version of code
    public static boolean upgrading = false;



    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
    }



    private SQLiteDatabase myDataBase;
    private SQLiteDatabase tempDatabase;

    private static DataBaseHelper instance;

    private final Context myContext;

    /**
     * Constructor
     * Takes and keeps a reference of the passed mainContext in order to access to the application assets and resources.
     * @param context
     */
    public DataBaseHelper(Context context) {

        super(context, DB_NAME, null, DB_VERSION);
        this.myContext = context;
    }

    public static DataBaseHelper getInstance(Context context) {
        if(instance == null)
            instance = new DataBaseHelper(context);
        try {
            instance.createDataBase();
        } catch (IOException e) {
            Log.e("RedRails", e.getMessage());
        }

        return instance;
    }

    public static int getDbVersion(){
        return DB_VERSION;
    }

    /**
     * Creates a empty database on the system and rewrites it with your own database.
     * */
    public void createDataBase() throws IOException{

        boolean dbExist = checkDataBase();

        if(dbExist){
            Log.w("RedRails","nothing here- database already exist");
            //listDBfolder();
            this.getReadableDatabase();
        }else{

            //By calling this method and empty database will be created into the default system path
            //of your application so we are gonna be able to overwrite that database with our database.
            this.getReadableDatabase();

            try {
                copyDataBase();
            } catch (IOException e) {
                throw new Error("Error copying database");
            }
        }

    }


    /**
     * Check if the database already exist to avoid re-copying the file each time you open the application.
     * @return true if it exists, false if it doesn't
     */
    private boolean checkDataBase(){

        SQLiteDatabase checkDB = null;
        try{
            String myPath = DB_PATH + DB_NAME;
            checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);

        }catch(SQLiteException e){

            //database does't exist yet.

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
     * */
    private void copyDataBase() throws IOException{
        Log.w("RedRails","OMG the database ("+DB_NAME+") is coping...");
        //Open your local db as the input stream
        InputStream myInput = myContext.getAssets().open(DB_NAME);

        // Path to the just created empty db
        String outFileName = DB_PATH + DB_NAME;

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

    private void createTempFile(boolean rollback) throws IOException{
        Log.w("RedRails","OMG the database ("+DB_NAME+") backuping... YEP!!");
        //Open your local db as the input stream
        File inFileName = new File(DB_PATH+DB_NAME);
        InputStream myInput = new FileInputStream(inFileName);

        // Path to the just created empty db
        String outFileName = DB_PATH + TEMP_DB_NAME;

        //Open the empty db as the output stream
        OutputStream myOutput = new FileOutputStream(outFileName);

        //transfer bytes from the inputfile to the outputfile
        byte[] buffer = new byte[1024];
        int length;
        while ((length = myInput.read(buffer))>0){
            myOutput.write(buffer, 0, length);
        }


        try{
            Log.i("RedRails", "deleting jounal file");
            File jounalingFile = new File(DB_PATH+DB_NAME+"-journal");
            jounalingFile.delete();
        }catch(Exception e){
            Log.e("RedRails", "Erro ao deletar Journal");
        }


        //Close the streams
        myOutput.flush();
        myOutput.close();
        myInput.close();
    }


    public SQLiteDatabase getNewDataBase() throws SQLException{
        String myPath = DB_PATH + DB_NAME;
        return SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READWRITE);

    }

    public SQLiteDatabase getTempDataBases() throws SQLException{
        String tempDatabasePath = DB_PATH + TEMP_DB_NAME;
        return SQLiteDatabase.openDatabase(tempDatabasePath, null, SQLiteDatabase.OPEN_READONLY);
    }

    public long countRows(String table) {
        return DatabaseUtils.queryNumEntries(myDataBase,table);
    }

    @Override
    public synchronized void close() {
        if(myDataBase != null)
            myDataBase.close();
        super.close();

    }

    @Override
    public void onCreate(SQLiteDatabase arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        Log.w("RedRails","OLDVERSION "+oldVersion+" - NEW VERSION "+newVersion);
        String tempDadtabaseName = "database_temp.sqlite";
        if(oldVersion<newVersion){
            try {
                upgrading=true;
                Log.w("RedRails","OnUpgrading...");
                Log.w("RedRails","Deleting Database result => "+myContext.deleteDatabase(TEMP_DB_NAME));
                //listDBfolder();
                createTempFile(false);
                //listDBfolder();
                copyDataBase();
                //listDBfolder();
                //DataBaseUpgrade.importUserData(DB_PATH, TEMP_DB_NAME, DB_NAME);

            } catch (IOException e) {
                throw new Error("Error copying database");
            }
        }
    }

    public boolean forceUpdate(){
        try {
            upgrading=true;
            Log.w("RedRails","OnUpgrading...");
            //Log.w("RedRails","Deleting Database result => "+myContext.deleteDatabase(TEMP_DB_NAME));
            createTempFile(false);
            copyDataBase();
        } catch (IOException e) {
            Log.e("RedRails","Error on forcing Update!");
            return false;
        }

        return true;
    }


    public boolean rollback(){
        try {
            upgrading=true;
            Log.w("RedRails","Rollbacking...");
            //Log.w("RedRails","Deleting Database result => "+myContext.deleteDatabase(TEMP_DB_NAME));
            createTempFile(true);//return database to original
            //copyDataBase();
        } catch (IOException e) {
            Log.e("RedRails","Error on Rollbaking!");
            return false;
        }

        return true;
    }



    static public void listDBfolder(){
        // Directory path here

        String files;
        File folder = new File(DB_PATH);
        File[] listOfFiles = folder.listFiles();
        Log.w("RedRails","------- Listing Database Path -------");


        for (int i = 0; i < listOfFiles.length; i++)
        {

            if (listOfFiles[i].isFile())
            {
                files = listOfFiles[i].getName();
                Log.w("RedRails",files);
            }
        }
        Log.w("RedRails","------------------------");
    }

    // Add your public helper methods to access and get content from the database.
    // You could return cursors by doing "return myDataBase.query(....)" so it'd be easy
    // to you to create adapters for your views.

}