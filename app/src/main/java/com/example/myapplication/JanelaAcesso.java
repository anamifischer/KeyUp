package com.example.myapplication;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Build;
import android.os.PersistableBundle;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

// janela do acesso (imagem 5)
public class JanelaAcesso {

    public static void mostrar(Context context, Acesso acesso, Runnable aoAtualizar) {
        Repositorio repo = new Repositorio(context);
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_acesso, null);

        TextView tvPlataforma = view.findViewById(R.id.tvPlataformaAcesso);
        EditText etAcesso = view.findViewById(R.id.etAcessoVer);
        EditText etSenha = view.findViewById(R.id.etSenhaVer);
        ImageButton btnEditar = view.findViewById(R.id.btnEditarAcesso);
        ImageButton btnExcluir = view.findViewById(R.id.btnExcluirAcesso);
        ImageButton btnCopiar = view.findViewById(R.id.btnCopiarSenha);

        tvPlataforma.setText(acesso.getPlataforma());
        etAcesso.setText(acesso.getAcesso());
        etSenha.setText(acesso.getSenha());

        AlertDialog dialog = new AlertDialog.Builder(context)
                .setView(view)
                .create();
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_SECURE);

        // tocando na senha ela aparece ou some
        boolean[] senhaVisivel = {false};
        etSenha.setOnClickListener(v -> {
            senhaVisivel[0] = !senhaVisivel[0];
            if (senhaVisivel[0]) {
                etSenha.setTransformationMethod(null);
            } else {
                etSenha.setTransformationMethod(PasswordTransformationMethod.getInstance());
            }
        });

        btnCopiar.setOnClickListener(v -> {
            ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText(acesso.getPlataforma(), acesso.getSenha());

            // marca como sensivel pro sistema nao mostrar a senha na previa
            PersistableBundle extras = new PersistableBundle();
            extras.putBoolean("android.content.extra.IS_SENSITIVE", true);
            clip.getDescription().setExtras(extras);
            clipboard.setPrimaryClip(clip);

            // do android 13 pra cima o proprio sistema ja avisa que copiou
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
                Toast.makeText(context, R.string.msg_senha_copiada, Toast.LENGTH_SHORT).show();
            }
        });

        // lapis abre a janela de editar (imagem 6)
        btnEditar.setOnClickListener(v -> {
            dialog.dismiss();
            JanelaNovo.mostrar(context, acesso, acesso.getCategoriaId(), aoAtualizar);
        });

        btnExcluir.setOnClickListener(v ->
                JanelaConfirmar.mostrar(context, R.string.titulo_excluir_acesso,
                        R.string.msg_excluir_acesso, () -> {
                            repo.excluirAcesso(acesso.getId());
                            dialog.dismiss();
                            aoAtualizar.run();
                        }));

        dialog.show();
    }
}
