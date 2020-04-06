/*
 * Decompiled with CFR 0_115.
 * 
 * Could not load the following classes:
 *  sun.security.pkcs11.SunPKCS11
 *  sun.security.pkcs11.wrapper.CK_C_INITIALIZE_ARGS
 *  sun.security.pkcs11.wrapper.CK_TOKEN_INFO
 *  sun.security.pkcs11.wrapper.PKCS11
 *  sun.security.pkcs11.wrapper.PKCS11Exception
 */
package br.com.debra.certificado;

import br.com.debra.certificado.Certificado;
import br.com.debra.certificado.CertificadoProperties;
import br.com.debra.certificado.SocketFactoryDinamico;
import br.com.debra.certificado.TipoCertificadoEnum;
import br.com.debra.certificado.exception.CertificadoException;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Paths;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Provider;
import java.security.Security;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Optional;
import org.apache.commons.httpclient.protocol.Protocol;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.DERIA5String;
import org.bouncycastle.asn1.DERObject;
import org.bouncycastle.asn1.DERObjectIdentifier;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERPrintableString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.DERUTF8String;
import sun.security.pkcs11.SunPKCS11;
import sun.security.pkcs11.wrapper.CK_C_INITIALIZE_ARGS;
import sun.security.pkcs11.wrapper.CK_TOKEN_INFO;
import sun.security.pkcs11.wrapper.PKCS11;
import sun.security.pkcs11.wrapper.PKCS11Exception;

public class CertificadoService {
    private static final DERObjectIdentifier CNPJ = new DERObjectIdentifier("2.16.76.1.3.3");
    private static final DERObjectIdentifier CPF = new DERObjectIdentifier("2.16.76.1.3.1");

    public static void inicializaCertificado(Certificado certificado, InputStream cacert) throws CertificadoException {
        Optional.ofNullable(certificado).orElseThrow(() -> new IllegalArgumentException("Certificado n\u00e3o pode ser nulo."));
        Optional.ofNullable(cacert).orElseThrow(() -> new IllegalArgumentException("Cacert n\u00e3o pode ser nulo."));
        try {
            KeyStore keyStore = CertificadoService.getKeyStore(certificado);
            if (certificado.isAtivarProperties()) {
                CertificadoProperties.inicia(certificado, cacert);
            } else {
                SocketFactoryDinamico socketFactory = new SocketFactoryDinamico(keyStore, certificado.getNome(), certificado.getSenha(), cacert, certificado.getSslProtocol());
                Protocol protocol = new Protocol("https", socketFactory, 443);
                Protocol.registerProtocol("https", protocol);
            }
        }
        catch (IOException | KeyManagementException | KeyStoreException | NoSuchAlgorithmException | CertificateException e) {
            throw new CertificadoException(e.getMessage());
        }
    }

    public static Certificado certificadoPfxBytes(byte[] certificadoBytes, String senha) throws CertificadoException {
        Optional.ofNullable(certificadoBytes).orElseThrow(() -> new IllegalArgumentException("Certificado n\u00e3o pode ser nulo."));
        Optional.ofNullable(senha).orElseThrow(() -> new IllegalArgumentException("Senha n\u00e3o pode ser nula."));
        Certificado certificado = new Certificado();
        try {
            certificado.setArquivoBytes(certificadoBytes);
            certificado.setSenha(senha);
            certificado.setTipoCertificado(TipoCertificadoEnum.ARQUIVO_BYTES);
            CertificadoService.setDadosCertificado(certificado);
        }
        catch (KeyStoreException e) {
            throw new CertificadoException("Erro ao carregar informa\u00e7\u00f5es do certificado:" + e.getMessage());
        }
        return certificado;
    }

    private static void setDadosCertificado(Certificado certificado) throws CertificadoException, KeyStoreException {
        KeyStore keyStore = CertificadoService.getKeyStore(certificado);
        Enumeration<String> aliasEnum = keyStore.aliases();
        String aliasKey = aliasEnum.nextElement();
        certificado.setNome(aliasKey);
        certificado.setCnpjCpf(CertificadoService.getDocumentoFromCertificado(certificado, keyStore));
        certificado.setVencimento(CertificadoService.dataValidade(certificado).toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
        certificado.setDiasRestantes(CertificadoService.diasRestantes(certificado));
        certificado.setValido(CertificadoService.valido(certificado));
    }

    public static Certificado certificadoPfx(String caminhoCertificado, String senha) throws CertificadoException, FileNotFoundException {
        Optional.ofNullable(caminhoCertificado).orElseThrow(() -> new IllegalArgumentException("Caminho do Certificado n\u00e3o pode ser nulo."));
        Optional.ofNullable(senha).orElseThrow(() -> new IllegalArgumentException("Senha n\u00e3o pode ser nula."));
        if (!Files.exists(Paths.get(caminhoCertificado, new String[0]), new LinkOption[0])) {
            throw new FileNotFoundException("Arquivo " + caminhoCertificado + " n\u00e3o existe");
        }
        Certificado certificado = new Certificado();
        try {
            certificado.setArquivo(caminhoCertificado);
            certificado.setSenha(senha);
            certificado.setTipoCertificado(TipoCertificadoEnum.ARQUIVO);
            CertificadoService.setDadosCertificado(certificado);
        }
        catch (KeyStoreException e) {
            throw new CertificadoException("Erro ao carregar informa\u00e7\u00f5es do certificado:" + e.getMessage());
        }
        return certificado;
    }

    public static Certificado certificadoA3(String marca, String dll, String senha) throws CertificadoException {
        return CertificadoService.certificadoA3(marca, dll, senha, null, null);
    }

    public static Certificado certificadoA3(String marca, String dll, String senha, String alias) throws CertificadoException {
        return CertificadoService.certificadoA3(marca, dll, senha, alias, null);
    }

    public static Certificado certificadoA3(String marca, String dll, String senha, String alias, String serialToken) throws CertificadoException {
        try {
            Certificado certificado = new Certificado();
            certificado.setMarcaA3(marca);
            certificado.setSenha(senha);
            certificado.setDllA3(dll);
            certificado.setTipoCertificado(TipoCertificadoEnum.TOKEN_A3);
            certificado.setSerialToken(serialToken);
            KeyStore keyStore = CertificadoService.getKeyStore(certificado);
            certificado.setNome(Optional.ofNullable(alias).orElse(keyStore.aliases().nextElement()));
            certificado.setCnpjCpf(CertificadoService.getDocumentoFromCertificado(certificado, keyStore));
            certificado.setVencimento(CertificadoService.dataValidade(certificado).toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
            certificado.setDiasRestantes(CertificadoService.diasRestantes(certificado));
            certificado.setValido(CertificadoService.valido(certificado));
            return certificado;
        }
        catch (Exception e) {
            throw new CertificadoException("Erro ao carregar informa\u00e7\u00f5es do certificado:" + e.getMessage());
        }
    }

    public static List<Certificado> listaCertificadosWindows() throws CertificadoException {
        ArrayList<Certificado> listaCert = new ArrayList<Certificado>();
        Certificado certificado = new Certificado();
        certificado.setTipoCertificado(TipoCertificadoEnum.REPOSITORIO_WINDOWS);
        try {
            KeyStore ks = CertificadoService.getKeyStore(certificado);
            Enumeration<String> aliasEnum = ks.aliases();
            while (aliasEnum.hasMoreElements()) {
                String aliasKey = aliasEnum.nextElement();
                if (aliasKey == null) continue;
                CertificadoService.setDadosCertificado(listaCert, ks, aliasKey, TipoCertificadoEnum.REPOSITORIO_WINDOWS);
            }
        }
        catch (KeyStoreException ex) {
            throw new CertificadoException("Erro ao Carregar Certificados:" + ex.getMessage());
        }
        return listaCert;
    }

    private static void setDadosCertificado(List<Certificado> listaCert, KeyStore ks, String aliasKey, TipoCertificadoEnum tipoCertificadoEnum) throws CertificadoException {
        Certificado cert = new Certificado();
        cert.setNome(aliasKey);
        cert.setCnpjCpf(CertificadoService.getDocumentoFromCertificado(cert, ks));
        cert.setTipoCertificado(tipoCertificadoEnum);
        cert.setSenha("");
        Date dataValidade = CertificadoService.dataValidade(cert);
        if (dataValidade == null) {
            cert.setNome("(INVALIDO)" + aliasKey);
            cert.setVencimento(LocalDate.of(2000, 1, 1));
            cert.setDiasRestantes(0);
            cert.setValido(false);
        } else {
            cert.setVencimento(dataValidade.toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
            cert.setDiasRestantes(CertificadoService.diasRestantes(cert));
            cert.setValido(CertificadoService.valido(cert));
        }
        listaCert.add(cert);
    }

    public static List<Certificado> listaCertificadosMac() throws CertificadoException {
        ArrayList<Certificado> listaCert = new ArrayList<Certificado>();
        Certificado certificado = new Certificado();
        certificado.setTipoCertificado(TipoCertificadoEnum.REPOSITORIO_MAC);
        try {
            KeyStore ks = CertificadoService.getKeyStore(certificado);
            Enumeration<String> aliasEnum = ks.aliases();
            while (aliasEnum.hasMoreElements()) {
                String aliasKey = aliasEnum.nextElement();
                if (aliasKey == null) continue;
                CertificadoService.setDadosCertificado(listaCert, ks, aliasKey, TipoCertificadoEnum.REPOSITORIO_MAC);
            }
        }
        catch (KeyStoreException ex) {
            throw new CertificadoException("Erro ao Carregar Certificados:" + ex.getMessage());
        }
        return listaCert;
    }

    public static List<String> listaAliasCertificadosA3(String marca, String dll, String senha) throws CertificadoException {
        try {
            ArrayList<String> listaCert = new ArrayList<String>(20);
            Certificado certificado = new Certificado();
            certificado.setTipoCertificado(TipoCertificadoEnum.TOKEN_A3);
            certificado.setMarcaA3(marca);
            certificado.setSenha(senha);
            certificado.setDllA3(dll);
            Enumeration<String> aliasEnum = CertificadoService.getKeyStore(certificado).aliases();
            while (aliasEnum.hasMoreElements()) {
                String aliasKey = aliasEnum.nextElement();
                if (aliasKey == null) continue;
                listaCert.add(aliasKey);
            }
            return listaCert;
        }
        catch (KeyStoreException ex) {
            throw new CertificadoException("Erro ao Carregar Certificados A3:" + ex.getMessage());
        }
    }

    private static Date dataValidade(Certificado certificado) throws CertificadoException {
        KeyStore keyStore = CertificadoService.getKeyStore(certificado);
        if (keyStore == null) {
            throw new CertificadoException("Erro Ao pegar Keytore, verifique o Certificado");
        }
        X509Certificate certificate = CertificadoService.getCertificate(certificado, keyStore);
        return certificate.getNotAfter();
    }

    private static int diasRestantes(Certificado certificado) {
        return (int) LocalDate.now().until(certificado.getVencimento(), ChronoUnit.DAYS);
    }

    private static boolean valido(Certificado certificado) {
        return LocalDate.now().isBefore(certificado.getVencimento());
    }

    public static KeyStore getKeyStore(Certificado certificado) throws CertificadoException {
        try {
            switch (certificado.getTipoCertificado()) {
                case REPOSITORIO_WINDOWS: {
                    KeyStore keyStore = KeyStore.getInstance("Windows-MY", "SunMSCAPI");
                    keyStore.load(null, null);
                    return keyStore;
                }
                case REPOSITORIO_MAC: {
                    KeyStore keyStore = KeyStore.getInstance("KeychainStore");
                    keyStore.load(null, null);
                    return keyStore;
                }
                case ARQUIVO: {
                    File file = new File(certificado.getArquivo());
                    if (!file.exists()) {
                        throw new CertificadoException("Certificado Digital n\u00e3o Encontrado");
                    }
                    KeyStore keyStore = KeyStore.getInstance("PKCS12");
                    ByteArrayInputStream bs = new ByteArrayInputStream(Files.readAllBytes(file.toPath()));
                    Throwable throwable = null;
                    try {
                        keyStore.load(bs, certificado.getSenha().toCharArray());
                    }
                    catch (Throwable var5_15) {
                        throwable = var5_15;
                        throw var5_15;
                    }
                    finally {
                        if (bs != null) {
                            if (throwable != null) {
                                try {
                                    bs.close();
                                }
                                catch (Throwable var5_14) {
                                    throwable.addSuppressed(var5_14);
                                }
                            } else {
                                bs.close();
                            }
                        }
                    }
                    return keyStore;
                }
                case ARQUIVO_BYTES: {
                    KeyStore keyStore = KeyStore.getInstance("PKCS12");
                    ByteArrayInputStream bs = new ByteArrayInputStream(certificado.getArquivoBytes());
                    Throwable throwable = null;
                    try {
                        keyStore.load(bs, certificado.getSenha().toCharArray());
                    }
                    catch (Throwable var5_17) {
                        throwable = var5_17;
                        throw var5_17;
                    }
                    finally {
                        if (bs != null) {
                            if (throwable != null) {
                                try {
                                    bs.close();
                                }
                                catch (Throwable var5_16) {
                                    throwable.addSuppressed(var5_16);
                                }
                            } else {
                                bs.close();
                            }
                        }
                    }
                    return keyStore;
                }
                case TOKEN_A3: {
                    KeyStore keyStore;
                    System.setProperty("sun.security.ssl.allowUnsafeRenegotiation", "true");
                    String slot = null;
                    if (certificado.getSerialToken() != null) {
                        slot = CertificadoService.getSlot(certificado.getDllA3(), certificado.getSerialToken());
                    }
                    InputStream conf = CertificadoService.configA3(certificado.getMarcaA3(), certificado.getDllA3(), slot);
                    Throwable throwable = null;
                    try {
                        SunPKCS11 p = new SunPKCS11(conf);
                        Security.addProvider((Provider)p);
                        keyStore = KeyStore.getInstance("PKCS11");
                        if (keyStore.getProvider() == null) {
                            keyStore = KeyStore.getInstance("PKCS11", (Provider)p);
                        }
                        keyStore.load(null, certificado.getSenha().toCharArray());
                    }
                    catch (Throwable p) {
                        throwable = p;
                        throw p;
                    }
                    finally {
                        if (conf != null) {
                            if (throwable != null) {
                                try {
                                    conf.close();
                                }
                                catch (Throwable p) {
                                    throwable.addSuppressed(p);
                                }
                            } else {
                                conf.close();
                            }
                        }
                    }
                    return keyStore;
                }
            }
            throw new CertificadoException("Tipo de certificado n\u00e3o Configurado: " + (Object)((Object)certificado.getTipoCertificado()));
        }
        catch (IOException | KeyStoreException | NoSuchAlgorithmException | NoSuchProviderException | CertificateException e) {
            if (Optional.ofNullable(e.getMessage()).orElse("").startsWith("keystore password was incorrect")) {
                throw new CertificadoException("Senha do Certificado inv\u00e1lida.");
            }
            throw new CertificadoException("Erro Ao pegar KeyStore: " + e.getMessage());
        }
    }

    public static X509Certificate getCertificate(Certificado certificado, KeyStore keystore) throws CertificadoException {
        try {
            return (X509Certificate)keystore.getCertificate(certificado.getNome());
        }
        catch (KeyStoreException e) {
            throw new CertificadoException("Erro Ao pegar X509Certificate: " + e.getMessage());
        }
    }

    private static InputStream configA3(String marca, String dll, String slot) throws UnsupportedEncodingException {
        String slotInfo = "";
        if (slot != null) {
            slotInfo = "\n\rslot = " + slot;
        }
        String conf = "name = " + marca + "\n\rlibrary = " + dll + slotInfo + "\n\rshowInfo = true";
        return new ByteArrayInputStream(conf.getBytes("UTF-8"));
    }

    private static String getSlot(String libraryPath, String serialNumber) throws IOException, CertificadoException {
        PKCS11 tmpPKCS11;
        String slotSelected;
        CK_C_INITIALIZE_ARGS initArgs = new CK_C_INITIALIZE_ARGS();
        String functionList = "C_GetFunctionList";
        initArgs.flags = 0;
        slotSelected = null;
        try {
            try {
                tmpPKCS11 = PKCS11.getInstance((String)libraryPath, (String)functionList, (CK_C_INITIALIZE_ARGS)initArgs, (boolean)false);
            }
            catch (IOException ex) {
                ex.printStackTrace();
                throw ex;
            }
        }
        catch (PKCS11Exception e) {
            try {
                tmpPKCS11 = PKCS11.getInstance((String)libraryPath, (String)functionList, (CK_C_INITIALIZE_ARGS)null, (boolean)true);
            }
            catch (Exception ex) {
                throw new CertificadoException("Erro ao pegar Slot A3: " + e.getMessage());
            }
        }
        try {
            long[] slotList;
            for (long slot : slotList = tmpPKCS11.C_GetSlotList(true)) {
                CK_TOKEN_INFO tokenInfo = tmpPKCS11.C_GetTokenInfo(slot);
                if (!serialNumber.equals(String.valueOf(tokenInfo.serialNumber))) continue;
                slotSelected = String.valueOf(slot);
            }
        }
        catch (Exception e) {
            throw new CertificadoException("Erro Ao pegar SlotA3: " + e.getMessage());
        }
        return slotSelected;
    }

    @Deprecated
    public static Certificado getCertificadoByCnpj(String cnpj) throws CertificadoException {
        return CertificadoService.getCertificadoByCnpjCpf(cnpj);
    }

    @Deprecated
    public static Certificado getCertificadoByCpf(String cnpj) throws CertificadoException {
        return CertificadoService.getCertificadoByCnpjCpf(cnpj);
    }

    public static Certificado getCertificadoByCnpjCpf(String cnpjCpf) throws CertificadoException {
        return CertificadoService.listaCertificadosWindows().stream().filter(cert -> cnpjCpf.equals(cert.getCnpjCpf())).findFirst().orElseThrow(() -> new CertificadoException("Certificado n\u00e3o encontrado com CNPJ/CPF : " + cnpjCpf));
    }

    private static String getDocumentoFromCertificado(Certificado certificado, KeyStore keyStore) throws CertificadoException {
        String[] cnpjCpf = new String[]{""};
        try {
            X509Certificate certificate = CertificadoService.getCertificate(certificado, keyStore);
            Optional.ofNullable(certificate.getSubjectAlternativeNames()).ifPresent(lista -> {
                lista.stream().filter(x -> x.get(0).equals(0)).forEach(a -> {
                    byte[] data = (byte[])a.get(1);
                    try {
                        ASN1InputStream is = new ASN1InputStream(data);
                        Throwable throwable = null;
                        try {
                            DERSequence derSequence = (DERSequence)is.readObject();
                            DERObjectIdentifier tipo = DERObjectIdentifier.getInstance(derSequence.getObjectAt(0));
                            if (CNPJ.equals(tipo) || CPF.equals(tipo)) {
                                DERObject objeto = ((DERTaggedObject)((DERTaggedObject)derSequence.getObjectAt(1)).getObject()).getObject();
                                if (objeto instanceof DEROctetString) {
                                    cnpjCpf[0] = new String(((DEROctetString)objeto).getOctets());
                                } else if (objeto instanceof DERPrintableString) {
                                    cnpjCpf[0] = ((DERPrintableString)objeto).getString();
                                } else if (objeto instanceof DERUTF8String) {
                                    cnpjCpf[0] = ((DERUTF8String)objeto).getString();
                                } else if (objeto instanceof DERIA5String) {
                                    cnpjCpf[0] = ((DERIA5String)objeto).getString();
                                }
                            }
                            if (CPF.equals(tipo) && cnpjCpf[0].length() > 25) {
                                cnpjCpf[0] = cnpjCpf[0].substring(8, 19);
                            }
                        }
                        catch (Throwable derSequence) {
                            throwable = derSequence;
                            throw derSequence;
                        }
                        finally {
                            if (is != null) {
                                if (throwable != null) {
                                    try {
                                        is.close();
                                    }
                                    catch (Throwable derSequence) {
                                        throwable.addSuppressed(derSequence);
                                    }
                                } else {
                                    is.close();
                                }
                            }
                        }
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                );
            }
            );
        }
        catch (Exception e) {
            throw new CertificadoException("Erro ao pegar Documento do Certificado: " + e.getMessage());
        }
        return cnpjCpf[0];
    }

}

