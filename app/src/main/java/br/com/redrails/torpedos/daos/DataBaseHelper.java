package br.com.redrails.torpedos.daos;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


public class DataBaseHelper extends SQLiteOpenHelper{

    //The Android's default system path of your application database.
    public static String DB_PATH = "/data/data/br.com.redrails.torpedos/databases/";

    private static String DB_NAME = "database.sqlite";
    private static int DB_VERSION=26;//change to version of code
    public static boolean upgrading = false;


    private SQLiteDatabase myDataBase;

    private static DataBaseHelper instance;

    private final Context myContext;


    public DataBaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.myContext = context;
        //myDataBase = this.getReadableDatabase();
    }

    public static DataBaseHelper getInstance(Context context) {
        if(instance == null)
            instance = new DataBaseHelper(context);
        return instance;
    }

    public static int getDbVersion(){
        return DB_VERSION;
    }


    @Override
    public synchronized void close() {
        if(myDataBase != null)
            myDataBase.close();
        super.close();

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CategoriaDAO.SQL_CREATION);
        db.execSQL(MensagemDAO.SQL_CREATION);
        db.execSQL(MensagemCategoriaDAO.SQL_CREATION);
    }


    @Override
    public void onUpgrade(SQLiteDatabase oldDatabase, int oldVersion, int newVersion) {
        Log.w("RedRails","OLDVERSION "+oldVersion+" - NEW VERSION "+newVersion);
        if(oldVersion<newVersion){
            upgrading=true;
         }
    }

    public static void changeDatabaseFileName(String databaseFileName){
        DB_NAME=databaseFileName;
    }



}