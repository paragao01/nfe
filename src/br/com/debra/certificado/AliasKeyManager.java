/*
 * Decompiled with CFR 0_115.
 */
package br.com.debra.certificado;

import java.io.PrintStream;
import java.net.Socket;
import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.Principal;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import javax.net.ssl.X509KeyManager;

public class AliasKeyManager
implements X509KeyManager {
    private KeyStore ks;
    private String alias;
    private String password;

    AliasKeyManager(KeyStore ks, String alias, String password) {
        this.ks = ks;
        this.alias = alias;
        this.password = password;
    }

    @Override
    public String chooseClientAlias(String[] str, Principal[] principal, Socket socket) {
        return this.alias;
    }

    @Override
    public String chooseServerAlias(String str, Principal[] principal, Socket socket) {
        return this.alias;
    }

    @Override
    public String[] getClientAliases(String str, Principal[] principal) {
        return new String[]{this.alias};
    }

    @Override
    public String[] getServerAliases(String str, Principal[] principal) {
        return new String[]{this.alias};
    }

    @Override
    public X509Certificate[] getCertificateChain(String alias) {
        try {
            Certificate[] certificates = this.ks.getCertificateChain(alias);
            X509Certificate[] x509Certificates = new X509Certificate[certificates.length];
            System.arraycopy(certificates, 0, x509Certificates, 0, certificates.length);
            return x509Certificates;
        }
        catch (KeyStoreException e) {
            System.out.println("N\u00e3o foi poss\u00edvel carregar o keystore para o alias:" + alias);
            return null;
        }
    }

    @Override
    public PrivateKey getPrivateKey(String alias) {
        try {
            return (PrivateKey)this.ks.getKey(alias, this.password == null ? null : this.password.toCharArray());
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }
}

