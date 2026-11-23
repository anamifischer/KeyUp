package com.example.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import java.util.ArrayList;
import java.util.List;

// janela de criar/editar (imagens 6 e 7)
// editando = null quando e um acesso novo
// categoriaPadraoId = categoria ja selecionada no seletor (-1 se nenhuma)
public class JanelaNovo {

    public static void mostrar(Context context, Acesso editando, long categoriaPadraoId, Runnable aoSalvar) {
        Repositorio repo = new Repositorio(context);
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_novo, null);

        TextView tvTitulo = view.findViewById(R.id.tvTituloNovo);
        TextView tvToggleAcesso = view.findViewById(R.id.tvToggleAcesso);
        TextView tvToggleCategoria = view.findViewById(R.id.tvToggleCategoria);
        View layoutAcesso = view.findViewById(R.id.layoutAcesso);
        View layoutCategoria = view.findViewById(R.id.layoutCategoria);
        EditText etPlataforma = view.findViewById(R.id.etPlataforma);
        Spinner spCategoria = view.findViewById(R.id.spCategoria);
        EditText etAcesso = view.findViewById(R.id.etAcessoNovo);
        EditText etSenha = view.findViewById(R.id.etSenhaNova);
        EditText etNomeCategoria = view.findViewById(R.id.etNomeCategoria);
        Button btnSalvar = view.findViewById(R.id.btnSalvar);

        // seletor de categoria
        List<Categoria> categorias = repo.listarCategorias();
        List<String> nomes = new ArrayList<>();
        for (Categoria c : categorias) {
            nomes.add(c.getNome());
        }
        ArrayAdapter<String> adapterSpinner = new ArrayAdapter<>(context, R.layout.item_spinner, nomes);
        adapterSpinner.setDropDownViewResource(R.layout.item_spinner_dropdown);
        spCategoria.setAdapter(adapterSpinner);

        long selecionadaId = categoriaPadraoId;
        if (editando != null) {
            selecionadaId = editando.getCategoriaId();
        }
        for (int i = 0; i < categorias.size(); i++) {
            if (categorias.get(i).getId() == selecionadaId) {
                spCategoria.setSelection(i);
            }
        }

        // true = aba Acesso, false = aba Categoria
        boolean[] modoAcesso = {true};

        if (editando != null) {
            // editando nao pode trocar entre acesso e categoria
            tvTitulo.setText(R.string.titulo_editar_acesso);
            etPlataforma.setText(editando.getPlataforma());
            etAcesso.setText(editando.getAcesso());
            etSenha.setText(editando.getSenha());
            tvToggleCategoria.setEnabled(false);
            tvToggleCategoria.setAlpha(0.4f);
        }

        AlertDialog dialog = new AlertDialog.Builder(context)
                .setView(view)
                .create();
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_SECURE);

        tvToggleAcesso.setOnClickListener(v -> {
            modoAcesso[0] = true;
            atualizarToggle(true, layoutAcesso, layoutCategoria, tvToggleAcesso, tvToggleCategoria);
        });
        tvToggleCategoria.setOnClickListener(v -> {
            modoAcesso[0] = false;
            atualizarToggle(false, layoutAcesso, layoutCategoria, tvToggleAcesso, tvToggleCategoria);
        });

        btnSalvar.setOnClickListener(v -> {
            if (modoAcesso[0]) {
                String plataforma = etPlataforma.getText().toString().trim();
                String usuario = etAcesso.getText().toString().trim();
                String senha = etSenha.getText().toString().trim();

                if (categorias.isEmpty()) {
                    Toast.makeText(context, R.string.msg_sem_categoria, Toast.LENGTH_SHORT).show();
                    return;
                }
                if (plataforma.isEmpty() || usuario.isEmpty() || senha.isEmpty()) {
                    Toast.makeText(context, R.string.msg_preencha_campos, Toast.LENGTH_SHORT).show();
                    return;
                }

                Categoria categoria = categorias.get(spCategoria.getSelectedItemPosition());

                if (editando != null) {
                    editando.setPlataforma(plataforma);
                    editando.setCategoriaId(categoria.getId());
                    editando.setAcesso(usuario);
                    editando.setSenha(senha);
                    repo.atualizarAcesso(editando);
                } else {
                    repo.adicionarAcesso(plataforma, categoria.getId(), usuario, senha);
                }
            } else {
                String nome = etNomeCategoria.getText().toString().trim();
                if (nome.isEmpty()) {
                    Toast.makeText(context, R.string.msg_preencha_campos, Toast.LENGTH_SHORT).show();
                    return;
                }
                if (repo.categoriaExiste(nome, -1)) {
                    Toast.makeText(context, R.string.msg_categoria_existente, Toast.LENGTH_SHORT).show();
                    return;
                }
                repo.adicionarCategoria(nome);
            }

            dialog.dismiss();
            if (aoSalvar != null) {
                aoSalvar.run();
            }
        });

        dialog.show();
    }

    private static void atualizarToggle(boolean acesso, View layoutAcesso, View layoutCategoria,
                                        TextView tvAcesso, TextView tvCategoria) {
        layoutAcesso.setVisibility(acesso ? View.VISIBLE : View.GONE);
        layoutCategoria.setVisibility(acesso ? View.GONE : View.VISIBLE);

        int escuro = tvAcesso.getContext().getColor(R.color.keyup_bg);
        int claro = tvAcesso.getContext().getColor(R.color.keyup_cream);

        tvAcesso.setBackgroundResource(acesso ? R.drawable.bg_toggle_on : R.drawable.bg_toggle_off);
        tvAcesso.setTextColor(acesso ? escuro : claro);
        tvCategoria.setBackgroundResource(acesso ? R.drawable.bg_toggle_off : R.drawable.bg_toggle_on);
        tvCategoria.setTextColor(acesso ? claro : escuro);
    }
}
