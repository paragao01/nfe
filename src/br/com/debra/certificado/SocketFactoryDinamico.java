/*
 * Decompiled with CFR 0_115.
 */
package br.com.debra.certificado;

import br.com.debra.certificado.AliasKeyManager;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import org.apache.commons.httpclient.params.HttpConnectionParams;
import org.apache.commons.httpclient.protocol.ProtocolSocketFactory;

class SocketFactoryDinamico
implements ProtocolSocketFactory {
    private final KeyStore keyStore;
    private final String alias;
    private final String senha;
    private final InputStream fileCacerts;
    private SSLContext ssl;

    SocketFactoryDinamico(KeyStore keyStore, String alias, String senha, InputStream fileCacerts, String sslProtocol) throws KeyManagementException, CertificateException, NoSuchAlgorithmException, KeyStoreException, IOException {
        this.keyStore = keyStore;
        this.alias = alias;
        this.senha = senha;
        this.fileCacerts = fileCacerts;
        this.ssl = this.createSSLContext(sslProtocol);
    }

    @Override
    public Socket createSocket(String host, int port, InetAddress localAddress, int localPort, HttpConnectionParams params) throws IOException {
        Socket socket = this.ssl.getSocketFactory().createSocket();
        socket.bind(new InetSocketAddress(localAddress, localPort));
        socket.connect(new InetSocketAddress(host, port), 60000);
        return socket;
    }

    @Override
    public Socket createSocket(String host, int port, InetAddress clientHost, int clientPort) throws IOException {
        return this.ssl.getSocketFactory().createSocket(host, port, clientHost, clientPort);
    }

    @Override
    public Socket createSocket(String host, int port) throws IOException {
        return this.ssl.getSocketFactory().createSocket(host, port);
    }

    private SSLContext createSSLContext(String sslProtocol) throws CertificateException, NoSuchAlgorithmException, KeyStoreException, IOException, KeyManagementException {
        KeyManager[] keyManagers = this.createKeyManagers();
        TrustManager[] trustManagers = this.createTrustManagers();
        SSLContext sslContext = SSLContext.getInstance(sslProtocol);
        sslContext.init(keyManagers, trustManagers, null);
        return sslContext;
    }

    private KeyManager[] createKeyManagers() {
        return new KeyManager[]{new AliasKeyManager(this.keyStore, this.alias, this.senha)};
    }

    private TrustManager[] createTrustManagers() throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException {
        TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        KeyStore trustStore = KeyStore.getInstance("JKS");
        trustStore.load(this.fileCacerts, "changeit".toCharArray());
        trustManagerFactory.init(trustStore);
        return trustManagerFactory.getTrustManagers();
    }
}

