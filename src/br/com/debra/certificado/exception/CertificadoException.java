/*
 * Decompiled with CFR 0_115.
 */
package br.com.debra.certificado.exception;

public class CertificadoException
extends Exception {
 	private static final long serialVersionUID = 1L;
	private String message;

    public CertificadoException(Throwable e) {
        super(e);
    }

    public CertificadoException(String message) {
        this((Throwable)null);
        this.message = message;
    }

    @Override
    public String getMessage() {
        return this.message;
    }
}

