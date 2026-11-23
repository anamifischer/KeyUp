package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class TelaLogin extends AppCompatActivity {

    EditText etEmail, etSenha;
    Repositorio repo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_tela_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        repo = new Repositorio(this);

        etEmail = findViewById(R.id.etEmail);
        etSenha = findViewById(R.id.etSenha);
    }

    public void entrar(View view) {
        String email = etEmail.getText().toString().trim();
        String senha = etSenha.getText().toString();

        if (email.isEmpty() || senha.isEmpty()) {
            Toast.makeText(this, R.string.msg_preencha_campos, Toast.LENGTH_SHORT).show();
            return;
        }

        long contaId = repo.autenticar(email, senha);

        if (contaId == -1) {
            Toast.makeText(this, R.string.msg_login_invalido, Toast.LENGTH_SHORT).show();
        } else {
            Sessao.contaId = contaId;
            Intent intent = new Intent(TelaLogin.this, TelaPrincipalActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
    }

    public void irCadastro(View view) {
        Intent intent = new Intent(TelaLogin.this, TelaCriarConta.class);
        startActivity(intent);
    }

    public void irRedefinirSenha(View view) {
        Intent intent = new Intent(TelaLogin.this, TelaRedefinirSenha.class);
        startActivity(intent);
    }
}
