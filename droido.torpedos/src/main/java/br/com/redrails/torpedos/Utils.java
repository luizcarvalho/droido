package br.com.redrails.torpedos;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Criado por luiz em 11/16/13.
 * Todos os direitos reservados para RedRails
 */
public class Utils {
    private static String COLUNA_TEXTO = "texto";
    public static List<String> GetColumns(SQLiteDatabase db, String tableName) {
        List<String> ar = null;
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("select texto from " + tableName, null);
            if (cursor.moveToFirst()) {
                do {
                    int indexTexto = cursor.getColumnIndex(COLUNA_TEXTO);
                    ar.add(cursor.getString(indexTexto));
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.v(tableName, e.getMessage(), e);
            e.printStackTrace();
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return ar;
    }

    public static String join(List<String> list, String delim) {
        StringBuilder buf = new StringBuilder();
        int num = list.size();
        for (int i = 0; i < num; i++) {
            if (i != 0)
                buf.append(delim);
            buf.append((String) list.get(i));
        }
        return buf.toString();
    }
}
