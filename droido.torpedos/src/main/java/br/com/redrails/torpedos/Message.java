package br.com.redrails.torpedos;

import java.io.IOException;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;


public class Message {
	protected static SQLiteDatabase database;
	public Boolean more=true;
	public String categoriaId="";
	public Cursor cursor = null;
	Integer limit = 20;
	Integer offset;
	String order = " ORDER BY score DESC ";
	long total = 0;
	Integer readed = 0;
	
	
	public Message(int currentPage,Context context,String categoria){
		offset = currentPage*limit-20;
		categoriaId = categoria;
		

		DataBaseHelper myDbHelper = new DataBaseHelper(context);
		try {
			myDbHelper.createDataBase();
			
		} catch (IOException ioe) {
			throw new Error("Unable to create database");
		}
		try {
			database = myDbHelper.openDataBase();
			total = myDbHelper.countRows("textos");
		} catch (SQLException sqle) {
			throw sqle;
		}	
		
	}
	
public Cursor readTexts(){	
	String categoriaClausula = getCategoriaClausula(categoriaId);
	//Toast.makeText(this, "SELECT texto FROM textos LIMIT "+limit+" OFFSET "+offset+"AND MORE: "+more, Toast.LENGTH_SHORT).show();
	Cursor cursor = database.rawQuery(
			       "SELECT texto FROM textos "+categoriaClausula+" "+order+" LIMIT "+limit+" OFFSET "+offset,null);
	Log.i("Droido","SELECT texto FROM textos "+categoriaClausula+" "+order+" LIMIT "+limit+" OFFSET "+offset,null);	
	return cursor;
}	
//Verifica se há mais Mensagens
	public Boolean hasMore(){
		Log.w("Droido", "HAS MORE: "+offset+"+"+limit+"<"+total);
       return (offset+limit)<total;
    }

	
	
	private String getCategoriaClausula(String categoriaId){
		String clausula = "";
	
		if(categoriaId!=""){
			clausula = "WHERE categoria_id="+categoriaId;
			Log.i("Droido", "CATEGORIA OK: "+categoriaId);
		}else{
			clausula="";
		}	
		return clausula;
		
	}	
	
	
	static void finish(){
		database.close();
	}
	
}


