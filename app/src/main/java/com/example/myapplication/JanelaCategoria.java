package com.example.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

// janela pra renomear ou excluir uma categoria
public class JanelaCategoria {

    public static void mostrar(Context context, Categoria categoria, Runnable aoRenomear, Runnable aoExcluir) {
        Repositorio repo = new Repositorio(context);
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_categoria, null);

        EditText etNome = view.findViewById(R.id.etNomeEditar);
        Button btnSalvar = view.findViewById(R.id.btnSalvarCategoria);
        TextView tvExcluir = view.findViewById(R.id.tvExcluirCategoria);

        etNome.setText(categoria.getNome());

        AlertDialog dialog = new AlertDialog.Builder(context)
                .setView(view)
                .create();
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_SECURE);

        btnSalvar.setOnClickListener(v -> {
            String nome = etNome.getText().toString().trim();

            if (nome.isEmpty()) {
                Toast.makeText(context, R.string.msg_preencha_campos, Toast.LENGTH_SHORT).show();
                return;
            }
            if (repo.categoriaExiste(nome, categoria.getId())) {
                Toast.makeText(context, R.string.msg_categoria_existente, Toast.LENGTH_SHORT).show();
                return;
            }

            repo.renomearCategoria(categoria.getId(), nome);
            categoria.setNome(nome);
            dialog.dismiss();
            aoRenomear.run();
        });

        tvExcluir.setOnClickListener(v ->
                JanelaConfirmar.mostrar(context, R.string.titulo_excluir_categoria,
                        R.string.msg_excluir_categoria, () -> {
                            repo.excluirCategoria(categoria.getId());
                            dialog.dismiss();
                            aoExcluir.run();
                        }));

        dialog.show();
    }
}
