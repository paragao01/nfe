/*
 * Decompiled with CFR 0_115.
 */
package br.com.debra.certificado;

import br.com.debra.certificado.Certificado;
import br.com.debra.certificado.TipoCertificadoEnum;
import br.com.debra.certificado.exception.CertificadoException;
import com.sun.net.ssl.internal.ssl.Provider;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.security.Security;

class CertificadoProperties {
    CertificadoProperties() {
    }

    static void inicia(Certificado certificado, InputStream iSCacert) throws CertificadoException, IOException {
        String cacert;
        System.setProperty("sun.security.ssl.allowUnsafeRenegotiation", "true");
        System.setProperty("java.protocol.handler.pkgs", "com.sun.net.ssl.internal.www.protocol");
        Security.addProvider(new Provider());
        System.clearProperty("javax.net.ssl.keyStore");
        System.clearProperty("javax.net.ssl.keyStorePassword");
        System.clearProperty("javax.net.ssl.trustStore");
        System.setProperty("jdk.tls.client.protocols", "TLSv1.2");
        switch (certificado.getTipoCertificado()) {
            case REPOSITORIO_WINDOWS: {
                System.setProperty("javax.net.ssl.keyStoreProvider", "SunMSCAPI");
                System.setProperty("javax.net.ssl.keyStoreType", "Windows-MY");
                System.setProperty("javax.net.ssl.keyStoreAlias", certificado.getNome());
                break;
            }
            case REPOSITORIO_MAC: {
                System.setProperty("javax.net.ssl.keyStoreType", "KeychainStore");
                System.setProperty("javax.net.ssl.keyStoreAlias", certificado.getNome());
                break;
            }
            case ARQUIVO: {
                System.setProperty("javax.net.ssl.keyStoreType", "PKCS12");
                System.setProperty("javax.net.ssl.keyStore", certificado.getArquivo());
                break;
            }
            case ARQUIVO_BYTES: {
                File cert = File.createTempFile("cert", ".pfx");
                Files.write(cert.toPath(), certificado.getArquivoBytes(), new OpenOption[0]);
                System.setProperty("javax.net.ssl.keyStoreType", "PKCS12");
                System.setProperty("javax.net.ssl.keyStore", cert.getAbsolutePath());
                break;
            }
            case TOKEN_A3: {
                throw new CertificadoException("Token A3 n\u00e3o pode utilizar Configura\u00e7\u00e3o atrav\u00e9s de Properties.");
            }
        }
        System.setProperty("javax.net.ssl.keyStorePassword", certificado.getSenha());
        System.setProperty("javax.net.ssl.trustStoreType", "JKS");
        try {
            int read;
            File file = File.createTempFile("tempfile", ".tmp");
            FileOutputStream out = new FileOutputStream(file);
            byte[] bytes = new byte[1024];
            while ((read = iSCacert.read(bytes)) != -1) {
                out.write(bytes, 0, read);
            }
            out.close();
            cacert = file.getAbsolutePath();
            file.deleteOnExit();
        }
        catch (IOException ex) {
            throw new CertificadoException(ex.getMessage());
        }
        System.setProperty("javax.net.ssl.trustStore", cacert);
    }

    public static void main(String[] args) throws IOException {
        File cert = File.createTempFile("cert", ".pfx");
        Files.write(cert.toPath(), Files.readAllBytes(new File("d:/certificado.pfx").toPath()), new OpenOption[0]);
        System.out.println(cert.getAbsolutePath());
    }

}

