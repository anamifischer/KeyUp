package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class TelaCriarConta extends AppCompatActivity {

    EditText etEmail, etSenha, etConfirmarSenha;
    Repositorio repo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_tela_criar_conta);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        repo = new Repositorio(this);

        etEmail = findViewById(R.id.etEmail);
        etSenha = findViewById(R.id.etSenha);
        etConfirmarSenha = findViewById(R.id.etConfirmarSenha);
    }

    public void criarConta(View view) {
        String email = etEmail.getText().toString().trim();
        String senha = etSenha.getText().toString();
        String confirmarSenha = etConfirmarSenha.getText().toString();

        if (email.isEmpty() || senha.isEmpty() || confirmarSenha.isEmpty()) {
            Toast.makeText(this, R.string.msg_preencha_campos, Toast.LENGTH_SHORT).show();
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, R.string.msg_email_invalido, Toast.LENGTH_SHORT).show();
        } else if (!senhaForte(senha)) {
            Toast.makeText(this, R.string.msg_senha_fraca, Toast.LENGTH_LONG).show();
        } else if (!senha.equals(confirmarSenha)) {
            Toast.makeText(this, R.string.msg_senhas_diferentes, Toast.LENGTH_SHORT).show();
        } else if (repo.emailExiste(email)) {
            Toast.makeText(this, R.string.msg_email_existente, Toast.LENGTH_SHORT).show();
        } else {
            long contaId = repo.criarConta(email, senha);

            if (contaId == -1) {
                Toast.makeText(this, R.string.msg_erro_conta, Toast.LENGTH_SHORT).show();
            } else {
                // conta criada, ja entra direto na tela principal
                Sessao.contaId = contaId;
                Intent intent = new Intent(TelaCriarConta.this, TelaPrincipalActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        }
    }

    // pelo menos 8 caracteres, com letra e numero
    boolean senhaForte(String senha) {
        return senha.length() >= 8
                && senha.matches(".*[a-zA-Z].*")
                && senha.matches(".*[0-9].*");
    }
}
