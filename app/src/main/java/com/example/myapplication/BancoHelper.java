package com.example.myapplication;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class BancoHelper extends SQLiteOpenHelper {

    private static final String NOME_BANCO = "keyup.db";
    private static final int VERSAO = 1;

    private static BancoHelper instancia;

    // usa sempre o mesmo banco no app inteiro
    public static synchronized BancoHelper getInstance(Context context) {
        if (instancia == null) {
            instancia = new BancoHelper(context.getApplicationContext());
        }
        return instancia;
    }

    private BancoHelper(Context context) {
        super(context, NOME_BANCO, null, VERSAO);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE conta ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "email TEXT NOT NULL UNIQUE, "
                + "sal TEXT NOT NULL, "
                + "senha_hash TEXT NOT NULL)");

        db.execSQL("CREATE TABLE categoria ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "nome TEXT NOT NULL, "
                + "conta_id INTEGER NOT NULL, "
                + "FOREIGN KEY (conta_id) REFERENCES conta(id))");

        db.execSQL("CREATE TABLE acesso ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "plataforma TEXT NOT NULL, "
                + "acesso TEXT NOT NULL, "
                + "senha TEXT NOT NULL, "
                + "categoria_id INTEGER NOT NULL, "
                + "FOREIGN KEY (categoria_id) REFERENCES categoria(id))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int versaoAntiga, int versaoNova) {
        // por enquanto so existe a versao 1
    }
}
