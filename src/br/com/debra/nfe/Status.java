package br.com.debra.nfe;

import br.com.debra.nfe.dom.ConfiguracoesNfe;
import br.com.debra.nfe.dom.enuns.DocumentoEnum;
import br.com.debra.nfe.dom.enuns.ServicosEnum;
import br.com.debra.nfe.schema_4.consStatServ.TConsStatServ;
import br.com.debra.nfe.schema_4.retConsStatServ.TRetConsStatServ;
import br.com.debra.nfe.exception.NfeException;
import br.com.debra.nfe.util.ConstantesUtil;
import br.com.debra.nfe.log.LoggerUtil;
import br.com.debra.nfe.util.WebServiceUtil;
import br.com.debra.nfe.util.XmlNfeUtil;
import br.com.debra.nfe.wsdl.NFeStatusServico4.NFeStatusServico4Stub;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.util.AXIOMUtil;

import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;
import java.rmi.RemoteException;

/**
 * Classe responsável por fazer a Verificação do Status Do Webservice
 *
 * @author Samuel Oliveira
 */
class Status {

    /**
     * Metodo para Consulta de Status de Serviço
     * <p>
     * Cria um objeto do tipo TConsStatServ usando as propriedades passadas
     * pelo argumento <b>config</b>. Após, este objeto é convertido em um obejto
     * OMElement manipulável onde é passado para o atributo extraElement da
     * classe NFeStatusServico4Stub.NfeDadosMsg.
     * </p>
     *
     * <p>
     * O método statusServico então cria uma instância de NFeStatusServico4Stub
     * passando o argumento <b>tipo</b> e <b>config</b> em seu construtor, onde será montada a URL
     * de consulta do status do serviço dependendo das configuções
     * (ambiente, Estado, NF-e ou NFC-e)
     * </p>
     *
     * <p>
     * Então o método nfeStatusServicoNF efetuará a consulta e retornará o
     * resultado que será convertido em um objeto e enfim retornado por este
     * método.
     * </p>
     *
     * @param config        ConfiguracoesNfe, interface de configuração da NF-e ou NFC-e.
     * @param tipoDocumento ConstantesUtil.NFE ou ConstantesUtil.NFCE
     * @return TRetConsStatServ - objeto que contém o resultado da transmissão do XML.
     * @throws NfeException
     * @see ConfiguracoesNfe
     * @see ConstantesUtil
     * @see WebServiceUtil
     * @see XmlNfeUtil
     */
    static TRetConsStatServ statusServico(ConfiguracoesNfe config, DocumentoEnum tipoDocumento) throws NfeException {

        try {

            TConsStatServ consStatServ = new TConsStatServ();
            consStatServ.setTpAmb(config.getAmbiente().getCodigo());
            consStatServ.setCUF(config.getEstado().getCodigoUF());
            consStatServ.setVersao(ConstantesUtil.VERSAO.NFE);
            consStatServ.setXServ("STATUS");
            String xml = XmlNfeUtil.objectToXml(consStatServ);

            LoggerUtil.log(Status.class, "[XML-ENVIO]: " + xml);

            OMElement ome = AXIOMUtil.stringToOM(xml);

            NFeStatusServico4Stub.NfeDadosMsg dadosMsg = new NFeStatusServico4Stub.NfeDadosMsg();
            dadosMsg.setExtraElement(ome);

            NFeStatusServico4Stub stub = new NFeStatusServico4Stub(
                    WebServiceUtil.getUrl(config, tipoDocumento, ServicosEnum.STATUS_SERVICO));

            NFeStatusServico4Stub.NfeResultMsg result = stub.nfeStatusServicoNF(dadosMsg);

            LoggerUtil.log(Status.class, "[XML-RETORNO]: " + result.getExtraElement().toString());
            return XmlNfeUtil.xmlToObject(result.getExtraElement().toString(), TRetConsStatServ.class);

        } catch (RemoteException | XMLStreamException | JAXBException e) {
            throw new NfeException(e.getMessage());
        }
    }

}