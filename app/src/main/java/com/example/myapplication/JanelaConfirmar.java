package com.example.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

// janelinha de "tem certeza?" antes de apagar alguma coisa
public class JanelaConfirmar {

    public static void mostrar(Context context, int titulo, int mensagem, Runnable aoConfirmar) {
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_confirmar, null);

        TextView tvTitulo = view.findViewById(R.id.tvTituloConfirmar);
        TextView tvMensagem = view.findViewById(R.id.tvMensagemConfirmar);
        TextView tvCancelar = view.findViewById(R.id.tvCancelar);
        Button btnConfirmar = view.findViewById(R.id.btnConfirmar);

        tvTitulo.setText(titulo);
        tvMensagem.setText(mensagem);

        AlertDialog dialog = new AlertDialog.Builder(context)
                .setView(view)
                .create();
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_SECURE);

        tvCancelar.setOnClickListener(v -> dialog.dismiss());
        btnConfirmar.setOnClickListener(v -> {
            dialog.dismiss();
            aoConfirmar.run();
        });

        dialog.show();
    }
}
