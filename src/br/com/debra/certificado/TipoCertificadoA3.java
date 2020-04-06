/*
 * Decompiled with CFR 0_115.
 */
package br.com.debra.certificado;

public enum TipoCertificadoA3 {
    LEITOR_SCR3310("SafeWeb", "c:/windows/system32/cmp11.dll"),
    TOKEN_ALADDIN("eToken", "c:/windows/system32/eTpkcs11.dll"),
    LEITOR_GEMPC_PERTO("SmartCard", "c:/windows/system32/aetpkss1.dll"),
    OBERTHUR("Oberthur", "c:/windows/system32/OcsCryptoki.dll");
    
    private final String marca;
    private final String dll;

    private TipoCertificadoA3(String marca, String dll) {
        this.marca = marca;
        this.dll = dll;
    }

    public String getMarca() {
        return this.marca;
    }

    public String getDll() {
        return this.dll;
    }
}

