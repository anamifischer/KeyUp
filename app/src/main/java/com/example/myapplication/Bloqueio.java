package com.example.myapplication;

import android.os.SystemClock;

// pede o login de novo se o app ficar muito tempo em segundo plano
public class Bloqueio {

    private static final long LIMITE = 60 * 1000; // 1 minuto

    private static int telasAbertas = 0;
    private static long saiuEm = 0;

    // chamar no onStart das telas internas
    // devolve true se passou do tempo e tem que pedir login
    public static boolean entrou() {
        telasAbertas++;
        boolean expirou = telasAbertas == 1 && saiuEm > 0
                && SystemClock.elapsedRealtime() - saiuEm > LIMITE;
        saiuEm = 0;
        return expirou;
    }

    // chamar no onStop das telas internas
    public static void saiu() {
        if (telasAbertas > 0) {
            telasAbertas--;
        }
        // nenhuma tela do app aparecendo = app foi pro segundo plano
        if (telasAbertas == 0 && Sessao.contaId != -1) {
            saiuEm = SystemClock.elapsedRealtime();
        }
    }
}
