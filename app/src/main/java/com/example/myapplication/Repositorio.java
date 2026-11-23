package com.example.myapplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.security.MessageDigest;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;

// tudo que le e grava no banco passa por aqui
public class Repositorio {

    private final BancoHelper banco;

    public Repositorio(Context context) {
        banco = BancoHelper.getInstance(context);
    }

    // ---------- conta ----------

    public boolean emailExiste(String email) {
        Cursor c = banco.getReadableDatabase().rawQuery(
                "SELECT id FROM conta WHERE email = ?",
                new String[]{email.trim().toLowerCase()});
        try {
            return c.moveToFirst();
        } finally {
            c.close();
        }
    }

    // devolve o id da conta criada ou -1 se der erro
    public long criarConta(String email, String senha) {
        String sal = Seguranca.gerarSal();

        ContentValues valores = new ContentValues();
        valores.put("email", email.trim().toLowerCase());
        valores.put("sal", sal);
        valores.put("senha_hash", Seguranca.gerarHash(senha, sal));

        return banco.getWritableDatabase().insert("conta", null, valores);
    }

    // devolve o id da conta se e-mail e senha baterem, senao -1
    public long autenticar(String email, String senha) {
        Cursor c = banco.getReadableDatabase().rawQuery(
                "SELECT id, sal, senha_hash FROM conta WHERE email = ?",
                new String[]{email.trim().toLowerCase()});
        try {
            if (c.moveToFirst()) {
                String hashDigitado = Seguranca.gerarHash(senha, c.getString(1));
                if (MessageDigest.isEqual(hashDigitado.getBytes(), c.getString(2).getBytes())) {
                    return c.getLong(0);
                }
            }
            return -1;
        } finally {
            c.close();
        }
    }

    // ---------- categorias (so as da conta que esta logada) ----------

    public List<Categoria> listarCategorias() {
        List<Categoria> lista = new ArrayList<>();
        Cursor c = banco.getReadableDatabase().rawQuery(
                "SELECT id, nome FROM categoria WHERE conta_id = ? ORDER BY id",
                new String[]{String.valueOf(Sessao.contaId)});
        try {
            while (c.moveToNext()) {
                lista.add(new Categoria(c.getLong(0), c.getString(1)));
            }
        } finally {
            c.close();
        }
        return lista;
    }

    public void adicionarCategoria(String nome) {
        ContentValues valores = new ContentValues();
        valores.put("nome", nome);
        valores.put("conta_id", Sessao.contaId);
        banco.getWritableDatabase().insert("categoria", null, valores);
    }

    public void renomearCategoria(long id, String nome) {
        ContentValues valores = new ContentValues();
        valores.put("nome", nome);
        banco.getWritableDatabase().update("categoria", valores, "id = ?",
                new String[]{String.valueOf(id)});
    }

    // apaga a categoria junto com todos os acessos dela
    public void excluirCategoria(long id) {
        String[] args = {String.valueOf(id)};
        SQLiteDatabase db = banco.getWritableDatabase();
        db.beginTransaction();
        try {
            db.delete("acesso", "categoria_id = ?", args);
            db.delete("categoria", "id = ?", args);
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    // compara sem acento e sem ligar pra maiuscula
    // ignorarId = a propria categoria, quando esta renomeando
    public boolean categoriaExiste(String nome, long ignorarId) {
        String procurado = normalizar(nome);
        for (Categoria c : listarCategorias()) {
            if (c.getId() != ignorarId && normalizar(c.getNome()).equals(procurado)) {
                return true;
            }
        }
        return false;
    }

    private String normalizar(String texto) {
        return Normalizer.normalize(texto.trim(), Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toLowerCase();
    }

    public int contarAcessos(long categoriaId) {
        Cursor c = banco.getReadableDatabase().rawQuery(
                "SELECT COUNT(*) FROM acesso WHERE categoria_id = ?",
                new String[]{String.valueOf(categoriaId)});
        try {
            c.moveToFirst();
            return c.getInt(0);
        } finally {
            c.close();
        }
    }

    // ---------- acessos ----------

    // colunas na ordem: id, plataforma, categoria_id, acesso, senha
    private Acesso lerAcesso(Cursor c) {
        return new Acesso(
                c.getLong(0),
                c.getString(1),
                c.getLong(2),
                c.getString(3),
                Seguranca.descriptografar(c.getString(4)));
    }

    // todos os acessos da conta logada (usado na pesquisa)
    public List<Acesso> listarAcessos() {
        List<Acesso> lista = new ArrayList<>();
        Cursor c = banco.getReadableDatabase().rawQuery(
                "SELECT a.id, a.plataforma, a.categoria_id, a.acesso, a.senha "
                        + "FROM acesso a JOIN categoria c ON c.id = a.categoria_id "
                        + "WHERE c.conta_id = ? ORDER BY a.id",
                new String[]{String.valueOf(Sessao.contaId)});
        try {
            while (c.moveToNext()) {
                lista.add(lerAcesso(c));
            }
        } finally {
            c.close();
        }
        return lista;
    }

    public List<Acesso> listarAcessosDaCategoria(long categoriaId) {
        List<Acesso> lista = new ArrayList<>();
        Cursor c = banco.getReadableDatabase().rawQuery(
                "SELECT id, plataforma, categoria_id, acesso, senha "
                        + "FROM acesso WHERE categoria_id = ? ORDER BY id",
                new String[]{String.valueOf(categoriaId)});
        try {
            while (c.moveToNext()) {
                lista.add(lerAcesso(c));
            }
        } finally {
            c.close();
        }
        return lista;
    }

    public void adicionarAcesso(String plataforma, long categoriaId, String acesso, String senha) {
        ContentValues valores = new ContentValues();
        valores.put("plataforma", plataforma);
        valores.put("categoria_id", categoriaId);
        valores.put("acesso", acesso);
        valores.put("senha", Seguranca.criptografar(senha));
        banco.getWritableDatabase().insert("acesso", null, valores);
    }

    public void excluirAcesso(long id) {
        banco.getWritableDatabase().delete("acesso", "id = ?",
                new String[]{String.valueOf(id)});
    }

    public void atualizarAcesso(Acesso a) {
        ContentValues valores = new ContentValues();
        valores.put("plataforma", a.getPlataforma());
        valores.put("categoria_id", a.getCategoriaId());
        valores.put("acesso", a.getAcesso());
        valores.put("senha", Seguranca.criptografar(a.getSenha()));
        banco.getWritableDatabase().update("acesso", valores, "id = ?",
                new String[]{String.valueOf(a.getId())});
    }
}
