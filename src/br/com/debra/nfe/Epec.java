package br.com.debra.nfe;

import br.com.debra.nfe.dom.ConfiguracoesNfe;
import br.com.debra.nfe.dom.enuns.DocumentoEnum;
import br.com.debra.nfe.dom.enuns.ServicosEnum;
import br.com.debra.nfe.exception.NfeException;
import br.com.debra.nfe.schema.envEpec.TEnvEvento;
import br.com.debra.nfe.schema.envEpec.TRetEnvEvento;
import br.com.debra.nfe.util.XmlNfeUtil;

import javax.xml.bind.JAXBException;

/**
 * @author Samuel Oliveira - samuel@swconsultoria.com.br
 * Data: 28/09/2017 - 11:11
 */
class Epec {

    static TRetEnvEvento eventoEpec(ConfiguracoesNfe config, TEnvEvento enviEvento, boolean valida) throws NfeException {

        try {

            String xml = XmlNfeUtil.objectToXml(enviEvento);
            xml = xml.replaceAll(" xmlns:ns2=\"http://www.w3.org/2000/09/xmldsig#\"", "");
            xml = xml.replaceAll("<evento v", "<evento xmlns=\"http://www.portalfiscal.inf.br/nfe\" v");

            xml = Eventos.enviarEvento(config, xml, ServicosEnum.EPEC, valida, DocumentoEnum.NFE);

            return XmlNfeUtil.xmlToObject(xml, TRetEnvEvento.class);

        } catch (JAXBException e) {
            throw new NfeException(e.getMessage());
        }

    }

}
