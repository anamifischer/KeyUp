package com.example.myapplication;

import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.util.Base64;
import android.util.Log;

import java.nio.charset.StandardCharsets;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.PBEKeySpec;

public class Seguranca {

    private static final String ALIAS = "keyup_chave";

    // ---------- senha mestra: guarda so o hash, nunca a senha ----------

    public static String gerarSal() {
        byte[] sal = new byte[16];
        new SecureRandom().nextBytes(sal);
        return Base64.encodeToString(sal, Base64.NO_WRAP);
    }

    public static String gerarHash(String senha, String salBase64) {
        try {
            byte[] sal = Base64.decode(salBase64, Base64.NO_WRAP);
            PBEKeySpec spec = new PBEKeySpec(senha.toCharArray(), sal, 20000, 256);
            SecretKeyFactory fabrica = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            byte[] hash = fabrica.generateSecret(spec).getEncoded();
            return Base64.encodeToString(hash, Base64.NO_WRAP);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // ---------- senhas dos acessos: AES com chave guardada no Keystore do Android ----------

    private static SecretKey pegarChave() throws Exception {
        KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
        keyStore.load(null);

        if (!keyStore.containsAlias(ALIAS)) {
            KeyGenerator gerador = KeyGenerator.getInstance(
                    KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore");
            gerador.init(new KeyGenParameterSpec.Builder(ALIAS,
                    KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                    .setKeySize(256)
                    .build());
            return gerador.generateKey();
        }
        return (SecretKey) keyStore.getKey(ALIAS, null);
    }

    // devolve iv + texto cifrado juntos, em Base64
    public static String criptografar(String texto) {
        try {
            Cipher cifra = Cipher.getInstance("AES/GCM/NoPadding");
            cifra.init(Cipher.ENCRYPT_MODE, pegarChave());
            byte[] iv = cifra.getIV();
            byte[] cifrado = cifra.doFinal(texto.getBytes(StandardCharsets.UTF_8));

            byte[] junto = new byte[iv.length + cifrado.length];
            System.arraycopy(iv, 0, junto, 0, iv.length);
            System.arraycopy(cifrado, 0, junto, iv.length, cifrado.length);
            return Base64.encodeToString(junto, Base64.NO_WRAP);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String descriptografar(String textoCifrado) {
        try {
            byte[] junto = Base64.decode(textoCifrado, Base64.NO_WRAP);
            byte[] iv = Arrays.copyOfRange(junto, 0, 12);
            byte[] cifrado = Arrays.copyOfRange(junto, 12, junto.length);

            Cipher cifra = Cipher.getInstance("AES/GCM/NoPadding");
            cifra.init(Cipher.DECRYPT_MODE, pegarChave(), new GCMParameterSpec(128, iv));
            return new String(cifra.doFinal(cifrado), StandardCharsets.UTF_8);
        } catch (Exception e) {
            Log.e("Seguranca", "nao deu pra descriptografar", e);
            return "";
        }
    }
}
