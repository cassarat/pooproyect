package com.example.demo.modelo;

import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class Usuario {
    private final String nombreUsuario;
    private final String nombre;
    private final byte[] sal = new byte[16];
    private final byte[] clave;

    public Usuario(String nombreUsuario, String nombre, String contrasena) {
        this.nombreUsuario = nombreUsuario;
        this.nombre = nombre;
        new SecureRandom().nextBytes(sal);
        this.clave = calcularClave(contrasena);
    }

    // Se guarda una clave derivada, nunca la contraseña original.
    private byte[] calcularClave(String contrasena) {
        PBEKeySpec datos = new PBEKeySpec(contrasena.toCharArray(), sal, 600000, 256);
        try {
            return SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
                    .generateSecret(datos).getEncoded();
        } catch (GeneralSecurityException error) {
            throw new IllegalStateException("No se pudo verificar la contraseña.", error);
        } finally {
            datos.clearPassword();
        }
    }

    public boolean autenticarse(String contrasena) {
        return contrasena != null && MessageDigest.isEqual(clave, calcularClave(contrasena));
    }

    public String getNombreUsuario() { return nombreUsuario; }
    public String getNombre() { return nombre; }
}
