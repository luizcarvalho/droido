package com.thilek.android.jassy.database.adapter;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.os.StatFs;
import android.widget.Toast;
import com.thilek.android.jassy.utilities.Logs;


import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public abstract class AbstractInternalDatabaseAdapter extends SQLiteOpenHelper {

    protected boolean logging = true;   

    private String mDatabaseName;

    private String mDatabasePath;

    private Context mContext;

    AbstractInternalDatabaseAdapter(Context context, String databasePath, String databaseName) {
        super(context, databaseName, null, 1);
        this.mContext = context;
        this.mDatabasePath = databasePath;
        this.mDatabaseName = databaseName;
    }
    
    abstract SQLiteDatabase getDB();    

    protected SQLiteDatabase open() throws SQLException {
        return this.getWritableDatabase();      
    }
    
    public void checkDatabase() {
        checkDataBaseAndCopy(mDatabasePath, mDatabaseName);
    }

    protected void close(SQLiteDatabase database) {
        
        if (database != null && database.isOpen())
            database.close();
        database = null;
    }

    public boolean deleteDatabase() {
        close();
        return mContext.deleteDatabase(mDatabaseName);
    }

    public void copyDatabase() {
        try {
            Toast.makeText(mContext, "memory " + FreeMemory(), Toast.LENGTH_LONG).show();
            
            Logs.d("copyDataBase", "begin copyDataBase");
            SQLiteDatabase tempDatabase = this.getReadableDatabase();
            tempDatabase.close();

            String copyDatabaseName = mDatabaseName.replaceFirst("sqlite3", "jet");
            InputStream myInput = mContext.getAssets().open(copyDatabaseName);
            OutputStream myOutput = new FileOutputStream(mDatabasePath + mDatabaseName);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = myInput.read(buffer)) > 0) {
                myOutput.write(buffer, 0, length);
            }
            myOutput.flush();
            myOutput.close();
            myInput.close();
            
        } catch (IOException e) {
            Logs.e("copyDataBase", e.getMessage() + " " + e.getStackTrace().toString());
            
        } catch (Exception e) {
            Logs.e("copyDataBase", e.getMessage() + " " + e.getStackTrace().toString());
            Toast.makeText(mContext, "copyDataBase", Toast.LENGTH_LONG).show();
        }
    }

    public void checkDataBaseAndCopy(String databasePath, String databaseName) {
        try {
            long start = System.currentTimeMillis();
            String path = databasePath + databaseName;
            SQLiteDatabase checkDB = SQLiteDatabase.openDatabase(path, null, 0);
            if (checkDB != null) {
                checkDB.close();
            }
            System.out.println("open checkDatabase> " + (System.currentTimeMillis() - start) + " ms");
        } catch (SQLException e) {
            Logs.e("AbstractDatabaseAdapter - copyDatabase", e.toString());
            copyDatabase(); 
        }
    }

    @Override
    public void onCreate(SQLiteDatabase arg0) {
    }

    @Override
    public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
    }
    
    /*************************************************************************************************
    Returns size in MegaBytes.

    If you need calculate external memory, change this: 
        StatFs statFs = new StatFs(Environment.getRootDirectory().getAbsolutePath());
    to this: 
        StatFs statFs = new StatFs(Environment.getExternalStorageDirectory().getAbsolutePath());        
    **************************************************************************************************/
        public int TotalMemory()
        {
            StatFs statFs = new StatFs(Environment.getRootDirectory().getAbsolutePath());   
            int Total = (statFs.getBlockCount() * statFs.getBlockSize()) / 1048576;
            return Total;
        }

        public int FreeMemory()
        {
            StatFs statFs = new StatFs(Environment.getRootDirectory().getAbsolutePath());
            int Free  = (statFs.getAvailableBlocks() * statFs.getBlockSize()) / 1048576;
            return Free;
        }

        public int BusyMemory()
        {
            StatFs statFs = new StatFs(Environment.getRootDirectory().getAbsolutePath());   
            int Total = (statFs.getBlockCount() * statFs.getBlockSize()) / 1048576;
            int Free  = (statFs.getAvailableBlocks() * statFs.getBlockSize()) / 1048576;
            int Busy  = Total - Free;
            return Busy;
        }

}