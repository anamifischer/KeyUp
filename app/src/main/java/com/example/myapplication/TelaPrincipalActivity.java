package com.example.myapplication;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;

public class TelaPrincipalActivity extends AppCompatActivity {

    EditText etPesquisar;
    TextView tvTituloCategorias, tvTituloAcessos, tvSemResultado;
    RecyclerView rvCategorias, rvResultadosAcessos;
    CategoriaAdapter adapter;
    AcessoAdapter adapterAcessos;
    Repositorio repo;

    // o que aparece na tela (muda conforme a pesquisa)
    List<Categoria> categoriasFiltradas = new ArrayList<>();
    List<Acesso> acessosFiltrados = new ArrayList<>();

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
        setContentView(R.layout.tela_principal);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        repo = new Repositorio(this);

        etPesquisar = findViewById(R.id.etPesquisar);
        tvTituloCategorias = findViewById(R.id.tvTituloCategorias);
        tvTituloAcessos = findViewById(R.id.tvTituloAcessos);
        tvSemResultado = findViewById(R.id.tvSemResultado);
        rvCategorias = findViewById(R.id.rvCategorias);
        rvResultadosAcessos = findViewById(R.id.rvResultadosAcessos);

        adapter = new CategoriaAdapter(categoriasFiltradas);
        rvCategorias.setLayoutManager(new LinearLayoutManager(this));
        rvCategorias.setAdapter(adapter);

        adapterAcessos = new AcessoAdapter(acessosFiltrados, this::atualizarTela);
        rvResultadosAcessos.setLayoutManager(new LinearLayoutManager(this));
        rvResultadosAcessos.setAdapter(adapterAcessos);

        // pesquisa enquanto o usuario digita
        etPesquisar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                pesquisar(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
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

    @Override
    protected void onResume() {
        super.onResume();
        if (repo == null || Sessao.contaId == -1) {
            return;
        }
        // quando volta da tela da categoria a quantidade de acessos pode ter mudado
        atualizarTela();
    }

    public void sair(View view) {
        Sessao.sair(this, MainActivity.class);
    }

    public void abrirNovo(View view) {
        JanelaNovo.mostrar(this, null, -1, this::atualizarTela);
    }

    // refaz a pesquisa com o que esta escrito no campo
    void atualizarTela() {
        pesquisar(etPesquisar.getText().toString());
    }

    // categorias: pelo nome / acessos: so pelo nome da plataforma
    void pesquisar(String texto) {
        String busca = semAcento(texto.trim());
        boolean pesquisando = !busca.isEmpty();

        categoriasFiltradas.clear();
        for (Categoria c : repo.listarCategorias()) {
            if (!pesquisando || semAcento(c.getNome()).contains(busca)) {
                categoriasFiltradas.add(c);
            }
        }

        acessosFiltrados.clear();
        if (pesquisando) {
            for (Acesso a : repo.listarAcessos()) {
                if (semAcento(a.getPlataforma()).contains(busca)) {
                    acessosFiltrados.add(a);
                }
            }
        }

        adapter.notifyDataSetChanged();
        adapterAcessos.notifyDataSetChanged();

        boolean temCategorias = !categoriasFiltradas.isEmpty();
        boolean temAcessos = !acessosFiltrados.isEmpty();
        boolean semNada = !temCategorias && !temAcessos;

        tvTituloCategorias.setVisibility(pesquisando && temCategorias ? View.VISIBLE : View.GONE);
        rvCategorias.setVisibility(temCategorias ? View.VISIBLE : View.GONE);
        tvTituloAcessos.setVisibility(temAcessos ? View.VISIBLE : View.GONE);
        rvResultadosAcessos.setVisibility(temAcessos ? View.VISIBLE : View.GONE);

        if (semNada) {
            // sem pesquisa = usuario ainda nao criou nenhuma categoria
            tvSemResultado.setText(pesquisando ? R.string.msg_sem_resultado : R.string.msg_sem_categorias);
        }
        tvSemResultado.setVisibility(semNada ? View.VISIBLE : View.GONE);
    }

    // tira acento e deixa minusculo, assim "academico" acha "Acadêmico"
    String semAcento(String texto) {
        String semMarcas = Normalizer.normalize(texto, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");
        return semMarcas.toLowerCase();
    }
}
