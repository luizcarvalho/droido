package br.com.redrails.torpedos.daos;

import android.database.sqlite.SQLiteDatabase;

/**
 * Criado por luiz em 19/04/14.
 * Todos os direitos reservados para RedRails
 */
public class BaseDAO {
    protected int BUSCA_COUNT = 1;
    protected int BUSCA_SELECT = 2;

    protected static SQLiteDatabase dataBase = null;

    public void fecharConexao() {
        if(dataBase != null && dataBase.isOpen())
            dataBase.close();
    }
}
