/**
 *
 */
package br.com.debra.nfe.util;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.InvalidParameterException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Base64;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.StringJoiner;
import java.util.zip.GZIPInputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;
import javax.xml.transform.stream.StreamSource;

import br.com.debra.nfe.exception.NfeException;
import br.com.debra.nfe.log.LoggerUtil;
import br.com.debra.nfe.schema.consCad.TConsCad;
import br.com.debra.nfe.schema.distdfeint.DistDFeInt;
import br.com.debra.nfe.schema.envEventoCancNFe.TEnvEvento;
import br.com.debra.nfe.schema.envEventoCancNFe.TRetEnvEvento;
import br.com.debra.nfe.schema_4.consReciNFe.TConsReciNFe;
import br.com.debra.nfe.schema_4.consSitNFe.TConsSitNFe;
import br.com.debra.nfe.schema_4.consStatServ.TConsStatServ;
import br.com.debra.nfe.schema_4.enviNFe.TEnviNFe;
import br.com.debra.nfe.schema_4.enviNFe.TNFe;
import br.com.debra.nfe.schema_4.enviNFe.TNfeProc;
import br.com.debra.nfe.schema_4.enviNFe.TProtNFe;
import br.com.debra.nfe.schema_4.enviNFe.TRetEnviNFe;
import br.com.debra.nfe.schema_4.inutNFe.TInutNFe;
import br.com.debra.nfe.schema_4.inutNFe.TProcInutNFe;
import br.com.debra.nfe.schema_4.inutNFe.TRetInutNFe;
import br.com.debra.nfe.schema_4.retConsSitNFe.TRetConsSitNFe;

/**
 * Classe Responsavel por Metodos referentes ao XML
 *
 * @author Samuel Oliveira
 */
public class XmlNfeUtil {

    private static final String STATUS = "TConsStatServ";
    private static final String SITUACAO_NFE = "TConsSitNFe";
    private static final String ENVIO_NFE = "TEnviNFe";
    private static final String DIST_DFE = "DistDFeInt";
    private static final String INUTILIZACAO = "TInutNFe";
    private static final String NFEPROC = "TNfeProc";
    private static final String NFE = "TNFe";
    private static final String EVENTO = "TEnvEvento";
    private static final String TPROCEVENTO = "TProcEvento";
    private static final String TCONSRECINFE = "TConsReciNFe";
    private static final String TCONS_CAD = "TConsCad";
    private static final String TPROCINUT = "TProcInutNFe";
    private static final String RETORNO_ENVIO = "TRetEnviNFe";
    private static final String SITUACAO_NFE_RET = "TRetConsSitNFe";
    private static final String RET_RECIBO_NFE = "TRetConsReciNFe";
    private static final String RET_STATUS_SERVICO = "TRetConsStatServ";

    private static final String RET_ENV_EVENTO = "TRetEnvEvento";

    private static final String RET_INUT_NFE = "TRetInutNFe";

    private static final String TPROCCANCELAR = "br.com.debra.nfe.schema.envEventoCancNFe.TProcEvento";
    private static final String TPROCCANCELARSUBST = "br.com.debra.nfe.schema.envEventoCancSubst.TProcEvento";
    private static final String TPROCCCE = "br.com.debra.nfe.schema.envcce.TProcEvento";
    private static final String TPROCEPEC = "br.com.debra.nfe.schema.envEpec.TProcEvento";
    private static final String TPROCMAN = "br.com.debra.nfe.schema.envConfRecebto.TProcEvento";

    private static final String TProtNFe = "TProtNFe";
    private static final String TProtEnvi = "br.com.debra.nfe.schema_4.enviNFe.TProtNFe";
    private static final String TProtCons = "br.com.debra.nfe.schema_4.retConsSitNFe.TProtNFe";
    private static final String TProtReci = "br.com.debra.nfe.schema_4.retConsReciNFe.TProtNFe";

    private static final String CANCELAR = "br.com.debra.nfe.schema.envEventoCancNFe.TEnvEvento";
    private static final String CANCELAR_SUBSTITUICAO = "br.com.debra.nfe.schema.envEventoCancSubst.TEnvEvento";
    private static final String CCE = "br.com.debra.nfe.schema.envcce.TEnvEvento";
    private static final String EPEC = "br.com.debra.nfe.schema.envEpec.TEnvEvento";
    private static final String MANIFESTAR = "br.com.debra.nfe.schema.envConfRecebto.TEnvEvento";

    private static final String RET_CANCELAR = "br.com.debra.nfe.schema.envEventoCancNFe.TRetEnvEvento";
    private static final String RET_CANCELAR_SUBSTITUICAO = "br.com.debra.nfe.schema.envEventoCancSubst.TRetEnvEvento";
    private static final String RET_CCE = "br.com.debra.nfe.schema.envcce.TRetEnvEvento";
    private static final String RET_EPEC = "br.com.debra.nfe.schema.envEpec.TRetEnvEvento";
    private static final String RET_MANIFESTAR = "br.com.debra.nfe.schema.envConfRecebto.TRetEnvEvento";

    /**
     * Transforma o String do XML em Objeto
     *
     * @param xml
     * @param classe
     * @return T
     */
    public static <T> T xmlToObject(String xml, Class<T> classe) throws JAXBException {

        JAXBContext context = JAXBContext.newInstance(classe);
        Unmarshaller unmarshaller = context.createUnmarshaller();

        return unmarshaller.unmarshal(new StreamSource(new StringReader(xml)), classe).getValue();
    }

    /**
     * Transforma o Objeto em XML(String)
     *
     * @param obj
     * @return
     * @throws JAXBException
     * @throws NfeException
     */
    public static <T> String objectToXml(Object obj) throws JAXBException, NfeException {

        JAXBContext context;
        JAXBElement<?> element;

        switch (obj.getClass().getSimpleName()) {

            case STATUS:
                context = JAXBContext.newInstance(TConsStatServ.class);
                element = new br.com.debra.nfe.schema_4.consStatServ.ObjectFactory().createConsStatServ((TConsStatServ) obj);
                break;

            case ENVIO_NFE:
                context = JAXBContext.newInstance(TEnviNFe.class);
                element = new br.com.debra.nfe.schema_4.enviNFe.ObjectFactory().createEnviNFe((TEnviNFe) obj);
                break;

            case RETORNO_ENVIO:
                context = JAXBContext.newInstance(TRetEnviNFe.class);
                element = XsdUtil.enviNfe.createTRetEnviNFe((TRetEnviNFe) obj);
                break;

            case SITUACAO_NFE:
                context = JAXBContext.newInstance(TConsSitNFe.class);
                element = new br.com.debra.nfe.schema_4.consSitNFe.ObjectFactory().createConsSitNFe((TConsSitNFe) obj);
                break;

            case DIST_DFE:
                context = JAXBContext.newInstance(DistDFeInt.class);
                element = new br.com.debra.nfe.schema.distdfeint.ObjectFactory().createDistDFeInt((DistDFeInt) obj);
                break;

            case TCONSRECINFE:
                context = JAXBContext.newInstance(TConsReciNFe.class);
                element = new br.com.debra.nfe.schema_4.consReciNFe.ObjectFactory().createConsReciNFe((TConsReciNFe) obj);
                break;

            case TCONS_CAD:
                context = JAXBContext.newInstance(TConsCad.class);
                element = new br.com.debra.nfe.schema.consCad.ObjectFactory().createConsCad((TConsCad) obj);
                break;

            case INUTILIZACAO:
                context = JAXBContext.newInstance(TInutNFe.class);
                element = new br.com.debra.nfe.schema_4.inutNFe.ObjectFactory().createInutNFe((TInutNFe) obj);
                break;

            case RET_INUT_NFE:
                context = JAXBContext.newInstance(TRetInutNFe.class);
                element = XsdUtil.inutNfe.createTRetInutNfe((TRetInutNFe) obj);
                break;

            case SITUACAO_NFE_RET:
                context = JAXBContext.newInstance(TRetConsSitNFe.class);
                element = new br.com.debra.nfe.schema_4.retConsSitNFe.ObjectFactory().createRetConsSitNFe((TRetConsSitNFe) obj);
                break;

            case RET_RECIBO_NFE:
                context = JAXBContext.newInstance(br.com.debra.nfe.schema_4.retConsReciNFe.TRetConsReciNFe.class);
                element = new br.com.debra.nfe.schema_4.retConsReciNFe.ObjectFactory().createRetConsReciNFe((br.com.debra.nfe.schema_4.retConsReciNFe.TRetConsReciNFe) obj);
                break;

            case RET_STATUS_SERVICO:
                context = JAXBContext.newInstance(br.com.debra.nfe.schema_4.retConsStatServ.TRetConsStatServ.class);
                element = new br.com.debra.nfe.schema_4.retConsStatServ.ObjectFactory().createRetConsStatServ((br.com.debra.nfe.schema_4.retConsStatServ.TRetConsStatServ) obj);
                break;

            case TPROCEVENTO:
                switch (obj.getClass().getName()) {
                    case TPROCCANCELAR:
                        context = JAXBContext.newInstance(br.com.debra.nfe.schema.envEventoCancNFe.TProcEvento.class);
                        element = new br.com.debra.nfe.schema.envEventoCancNFe.ObjectFactory().createTProcEvento((br.com.debra.nfe.schema.envEventoCancNFe.TProcEvento) obj);
                        break;
                    case TPROCCANCELARSUBST:
                        context = JAXBContext.newInstance(br.com.debra.nfe.schema.envEventoCancSubst.TProcEvento.class);
                        element = new br.com.debra.nfe.schema.envEventoCancSubst.ObjectFactory().createTProcEvento((br.com.debra.nfe.schema.envEventoCancSubst.TProcEvento) obj);
                        break;
                    case TPROCCCE:
                        context = JAXBContext.newInstance(br.com.debra.nfe.schema.envcce.TProcEvento.class);
                        element = new br.com.debra.nfe.schema.envcce.ObjectFactory().createTProcEvento((br.com.debra.nfe.schema.envcce.TProcEvento) obj);
                        break;
                    case TPROCEPEC:
                        context = JAXBContext.newInstance(br.com.debra.nfe.schema.envEpec.TProcEvento.class);
                        element = XsdUtil.epec.createTProcEvento((br.com.debra.nfe.schema.envEpec.TProcEvento) obj);
                        break;
                    case TPROCMAN:
                        context = JAXBContext.newInstance(br.com.debra.nfe.schema.envEpec.TProcEvento.class);
                        element = XsdUtil.epec.createTProcEvento((br.com.debra.nfe.schema.envEpec.TProcEvento) obj);
                        break;
                    default:
                        throw new NfeException("Objeto não mapeado no XmlUtil:" + obj.getClass().getSimpleName());
                }

                break;

            case NFEPROC:
                context = JAXBContext.newInstance(TNfeProc.class);
                element = XsdUtil.enviNfe.createTNfeProc((TNfeProc) obj);
                break;

            case NFE:
                context = JAXBContext.newInstance(TNFe.class);
                element = new JAXBElement<>(new QName("http://www.portalfiscal.inf.br/nfe", "NFe"), TNFe.class, null, (br.com.debra.nfe.schema_4.enviNFe.TNFe) obj);
                break;

            case TPROCINUT:
                context = JAXBContext.newInstance(TProcInutNFe.class);
                element = XsdUtil.inutNfe.createTProcInutNFe((TProcInutNFe) obj);
                break;

            case EVENTO:
                switch (obj.getClass().getName()) {
                    case CANCELAR:
                        context = JAXBContext.newInstance(TEnvEvento.class);
                        element = new br.com.debra.nfe.schema.envEventoCancNFe.ObjectFactory().createEnvEvento((TEnvEvento) obj);
                        break;
                    case CANCELAR_SUBSTITUICAO:
                        context = JAXBContext.newInstance(br.com.debra.nfe.schema.envEventoCancSubst.TEnvEvento.class);
                        element = new br.com.debra.nfe.schema.envEventoCancSubst.ObjectFactory().createEnvEvento((br.com.debra.nfe.schema.envEventoCancSubst.TEnvEvento) obj);
                        break;
                    case CCE:
                        context = JAXBContext.newInstance(br.com.debra.nfe.schema.envcce.TEnvEvento.class);
                        element = new br.com.debra.nfe.schema.envcce.ObjectFactory().createEnvEvento((br.com.debra.nfe.schema.envcce.TEnvEvento) obj);
                        break;
                    case EPEC:
                        context = JAXBContext.newInstance(br.com.debra.nfe.schema.envEpec.TEnvEvento.class);
                        element = new br.com.debra.nfe.schema.envEpec.ObjectFactory().createEnvEvento((br.com.debra.nfe.schema.envEpec.TEnvEvento) obj);
                        break;
                    case MANIFESTAR:
                        context = JAXBContext.newInstance(br.com.debra.nfe.schema.envConfRecebto.TEnvEvento.class);
                        element = new br.com.debra.nfe.schema.envConfRecebto.ObjectFactory().createEnvEvento((br.com.debra.nfe.schema.envConfRecebto.TEnvEvento) obj);
                        break;
                    default:
                        throw new NfeException("Objeto não mapeado no XmlUtil:" + obj.getClass().getSimpleName());
                }
                break;

            case RET_ENV_EVENTO:
                switch (obj.getClass().getName()) {
                    case RET_CANCELAR:
                        context = JAXBContext.newInstance(TRetEnvEvento.class);
                        element = XsdUtil.retEnvEvento.createTRetEnvEvento((TRetEnvEvento) obj);
                        break;
                    case RET_CANCELAR_SUBSTITUICAO:
                        context = JAXBContext.newInstance(br.com.debra.nfe.schema.envEventoCancSubst.TRetEnvEvento.class);
                        element = XsdUtil.retEnvEvento.createTRetEnvEvento((br.com.debra.nfe.schema.envEventoCancSubst.TRetEnvEvento) obj);
                        break;
                    case RET_CCE:
                        context = JAXBContext.newInstance(br.com.debra.nfe.schema.envcce.TRetEnvEvento.class);
                        element = XsdUtil.retEnvEvento.createTRetEnvEvento((br.com.debra.nfe.schema.envcce.TRetEnvEvento) obj);
                        break;
                    case RET_EPEC:
                        context = JAXBContext.newInstance(br.com.debra.nfe.schema.envEpec.TRetEnvEvento.class);
                        element = XsdUtil.retEnvEvento.createTRetEnvEvento((br.com.debra.nfe.schema.envEpec.TRetEnvEvento) obj);
                        break;
                    case RET_MANIFESTAR:
                        context = JAXBContext.newInstance(br.com.debra.nfe.schema.envConfRecebto.TRetEnvEvento.class);
                        element = XsdUtil.retEnvEvento.createTRetEnvEvento((br.com.debra.nfe.schema.envConfRecebto.TRetEnvEvento) obj);
                        break;
                    default:
                        throw new NfeException("Objeto não mapeado no XmlUtil:" + obj.getClass().getSimpleName());
                }
                break;

            case TProtNFe:
                switch (obj.getClass().getName()) {
                    case TProtEnvi:
                        context = JAXBContext.newInstance(TProtNFe.class);
                        element = XsdUtil.enviNfe.createTProtNFe((br.com.debra.nfe.schema_4.enviNFe.TProtNFe) obj);
                        break;
                    case TProtCons:
                        context = JAXBContext.newInstance(br.com.debra.nfe.schema_4.retConsSitNFe.TProtNFe.class);
                        element = XsdUtil.retConsSitNfe.createTProtNFe((br.com.debra.nfe.schema_4.retConsSitNFe.TProtNFe) obj);
                        break;
                    case TProtReci:
                        context = JAXBContext.newInstance(br.com.debra.nfe.schema_4.retConsReciNFe.TProtNFe.class);
                        element = XsdUtil.retConsReciNfe.createTProtNFe((br.com.debra.nfe.schema_4.retConsReciNFe.TProtNFe) obj);
                        break;
                    default:
                        throw new NfeException("Objeto não mapeado no XmlUtil:" + obj.getClass().getSimpleName());
                }
                break;

            default:
                throw new NfeException("Objeto não mapeado no XmlUtil:" + obj.getClass().getSimpleName());
        }
        assert context != null;
        Marshaller marshaller = context.createMarshaller();

        marshaller.setProperty("jaxb.encoding", "Unicode");
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.FALSE);
        marshaller.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.TRUE);

        StringWriter sw = new StringWriter();

        marshaller.marshal(element, sw);
        StringBuilder xml = new StringBuilder();
        xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>").append(sw.toString());

        if ((obj.getClass().getSimpleName().equals(TPROCEVENTO))) {
            return replacesNfe(xml.toString().replaceAll("procEvento", "procEventoNFe"));
        } else {
            return replacesNfe(xml.toString());
        }

    }

    public static String gZipToXml(byte[] conteudo) throws IOException {
        if (conteudo == null || conteudo.length == 0) {
            return "";
        }
        GZIPInputStream gis;
        gis = new GZIPInputStream(new ByteArrayInputStream(conteudo));
        BufferedReader bf = new BufferedReader(new InputStreamReader(gis, "UTF-8"));
        StringBuilder outStr = new StringBuilder();
        String line;
        while ((line = bf.readLine()) != null) {
            outStr.append(line);
        }

        return outStr.toString();
    }

    public static String criaNfeProc(TEnviNFe enviNfe, Object retorno) throws JAXBException, NfeException {

        TNfeProc nfeProc = new TNfeProc();
        nfeProc.setVersao("4.00");
        nfeProc.setNFe(enviNfe.getNFe().get(0));
        String xml = XmlNfeUtil.objectToXml(retorno);
        nfeProc.setProtNFe(XmlNfeUtil.xmlToObject(xml, TProtNFe.class));

        return XmlNfeUtil.objectToXml(nfeProc);
    }

    private static String replacesNfe(String xml) {

        xml = xml.replaceAll("<!\\[CDATA\\[<!\\[CDATA\\[", "<!\\[CDATA\\[");
        xml = xml.replaceAll("\\]\\]>\\]\\]>", "\\]\\]>");
        xml = xml.replaceAll("ns2:", "");
        xml = xml.replaceAll("ns3:", "");
        xml = xml.replaceAll("&lt;", "<");
        xml = xml.replaceAll("&gt;", ">");
        xml = xml.replaceAll("<Signature>", "<Signature xmlns=\"http://www.w3.org/2000/09/xmldsig#\">");
        xml = xml.replaceAll(" xmlns:ns2=\"http://www.w3.org/2000/09/xmldsig#\"", "");
        xml = xml.replaceAll(" xmlns=\"\" xmlns:ns3=\"http://www.portalfiscal.inf.br/nfe\"", "");
        xml = xml.replaceAll("<NFe>", "<NFe xmlns=\"http://www.portalfiscal.inf.br/nfe\">");

        return xml;

    }

    /**
     * Le o Arquivo XML e retona String
     *
     * @return String
     * @throws NfeException
     */
    public static String leXml(String arquivo) throws IOException {

        ObjetoUtil.verifica(arquivo).orElseThrow(() -> new IllegalArgumentException("Arquivo xml não pode ser nulo/vazio."));
        if (!Files.exists(Paths.get(arquivo))) {
            throw new FileNotFoundException("Arquivo " + arquivo + " não encontrado.");
        }
        List<String> list = Files.readAllLines(Paths.get(arquivo));
        StringJoiner joiner = new StringJoiner("\n");
        list.forEach(joiner::add);

        return joiner.toString();
    }

    public static String dataNfe(LocalDateTime dataASerFormatada) {
        return dataNfe(dataASerFormatada, ZoneId.systemDefault());
    }

    public static String dataNfe(LocalDateTime dataASerFormatada, ZoneId zoneId) {
        try {
            GregorianCalendar calendar = GregorianCalendar.from(dataASerFormatada.atZone(ObjetoUtil.verifica(zoneId).orElse(ZoneId.of("Brazil/East"))));

            XMLGregorianCalendar xmlCalendar = DatatypeFactory.newInstance().newXMLGregorianCalendar(calendar);
            xmlCalendar.setMillisecond(DatatypeConstants.FIELD_UNDEFINED);
            return xmlCalendar.toString();

        } catch (DatatypeConfigurationException e) {
            LoggerUtil.log(XmlNfeUtil.class, e.getMessage());
        }
        return null;
    }

    public static byte[] geraHashCSRT(String chave, String csrt) throws NoSuchAlgorithmException {

        ObjetoUtil.verifica(chave).orElseThrow(() -> new InvalidParameterException("Chave não deve ser nula ou vazia"));
        ObjetoUtil.verifica(csrt).orElseThrow(() -> new InvalidParameterException("CSRT não deve ser nulo ou vazio"));
        if (chave.length() != 44) {
            throw new InvalidParameterException("Chave deve conter 44 caracteres.");
        }
        MessageDigest md = MessageDigest.getInstance("SHA-1");
        md.update((csrt + chave).getBytes());
        return Base64.getEncoder().encode(md.digest());
    }
    
	public static String TagXML(String XML, String tag) {
		String initag = "<" + tag + ">";
		String endtag = "</" + tag + ">";
		String subS = "";
		int i = initag.length();
		int startPosition = XML.indexOf(initag) + i;
		if (startPosition >= i) {
			int endPosition = XML.indexOf(endtag, startPosition);
			subS = XML.substring(startPosition, endPosition);
		}
		return subS;
	}

}
