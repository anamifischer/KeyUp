package com.example.myapplication;

import android.app.Activity;
import android.content.Intent;

public class Sessao {

    // id da conta que fez login (-1 = ninguem entrou ainda)
    public static long contaId = -1;

    // encerra a sessao e vai pra outra tela, limpando as telas abertas
    public static void sair(Activity tela, Class<?> proximaTela) {
        contaId = -1;
        Intent intent = new Intent(tela, proximaTela);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        tela.startActivity(intent);
        tela.finish();
    }
}
