package br.com.redrails.torpedos;

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

import br.com.redrails.torpedos.categoria.CategoriaDAO;


public class DataBaseHelper extends SQLiteOpenHelper{
	 
    //The Android's default system path of your application database.
    private static String DB_PATH = "/data/data/br.com.redrails.torpedos/databases/";
 
    private static String DB_NAME = "database.sqlite";
    
    private static int DB_VERSION=21;//change to version of code
 
    private SQLiteDatabase myDataBase;

    private static DataBaseHelper instance;
 
    private final Context myContext;

    private String SQL_CREATION_AUX_TABLES = "CREATE TABLE log (\n" +
            "  _id INTEGER PRIMARY KEY,\n" +
            "  tipo INTEGER,\n" +
            "  mensagem_slug TEXT(16),\n" +
            "  mensagem TEXT,\n" +
            "  user TEXT(64)\n" +
            ");\n" +
            "CREATE TABLE mensagem_categorias (\n" +
            "  _id INTEGER PRIMARY KEY,\n" +
            "  mensagem_id INTEGER,\n" +
            "  categoria_id INTEGER\n" +
            ");";
 

    public DataBaseHelper(Context context) {
    	super(context, DB_NAME, null, DB_VERSION);
        this.myContext = context;
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
	public void onCreate(SQLiteDatabase database) {
		// TODO Auto-generated method stub
        database.execSQL(SQL_CREATION_AUX_TABLES);
        database.execSQL(CategoriaDAO.SQL_CREATION);
        database.execSQL(MensagemDAO.SQL_CREATION);
	}

	@Override
	public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
		Log.w("Droido","OLDVERSION "+oldVersion+" - NEW VERSION "+newVersion);
		if(oldVersion<newVersion){
    		Log.w("Droido","OnUpgrading...");
            if(oldVersion<20){
                dropTables(database);
            }

		}
	}

    private void dropTables(SQLiteDatabase database) {
        database.execSQL("DROP TABLE IF EXISTS textos");
        database.execSQL("DROP TABLE IF EXISTS categorias");
    }


}