package com.example.myapplication;

import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class TelaCategoriaActivity extends AppCompatActivity {

    TextView tvTituloCategoria;
    RecyclerView rvAcessos;
    AcessoAdapter adapter;
    Repositorio repo;
    List<Acesso> acessos = new ArrayList<>();
    Categoria categoria;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Sessao.contaId == -1) {
            // o app foi reiniciado e perdeu o login, volta pro comeco
            Sessao.sair(this, MainActivity.class);
            return;
        }

        // nao deixa tirar print nem mostrar a tela nos apps recentes
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE);

        EdgeToEdge.enable(this);
        setContentView(R.layout.tela_categoria);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        repo = new Repositorio(this);

        tvTituloCategoria = findViewById(R.id.tvTituloCategoria);
        rvAcessos = findViewById(R.id.rvAcessos);

        // categoria que veio da tela principal
        categoria = new Categoria(
                getIntent().getLongExtra("id_categoria", -1),
                getIntent().getStringExtra("nome_categoria"));
        tvTituloCategoria.setText(categoria.getNome());

        acessos.addAll(repo.listarAcessosDaCategoria(categoria.getId()));

        adapter = new AcessoAdapter(acessos, this::atualizarLista);
        rvAcessos.setLayoutManager(new LinearLayoutManager(this));
        rvAcessos.setAdapter(adapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (repo == null) {
            return;
        }
        if (Bloqueio.entrou()) {
            Toast.makeText(this, R.string.msg_sessao_bloqueada, Toast.LENGTH_SHORT).show();
            Sessao.sair(this, TelaLogin.class);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (repo != null) {
            Bloqueio.saiu();
        }
    }

    // recarrega a lista depois de salvar, editar ou excluir um acesso
    void atualizarLista() {
        acessos.clear();
        acessos.addAll(repo.listarAcessosDaCategoria(categoria.getId()));
        adapter.notifyDataSetChanged();
    }

    public void voltar(View view) {
        finish();
    }

    public void novoAcesso(View view) {
        JanelaNovo.mostrar(this, null, categoria.getId(), this::atualizarLista);
    }

    // tocar no titulo abre a janela de renomear/excluir a categoria
    public void editarCategoria(View view) {
        JanelaCategoria.mostrar(this, categoria,
                () -> tvTituloCategoria.setText(categoria.getNome()),
                this::finish);
    }
}
