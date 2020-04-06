package br.com.debra.nfe;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Random;

import javax.xml.bind.JAXBException;

import br.com.debra.certificado.Certificado;
import br.com.debra.certificado.CertificadoService;
import br.com.debra.certificado.exception.CertificadoException;
import br.com.debra.nfe.dom.ConfiguracoesNfe;
import br.com.debra.nfe.dom.Evento;
import br.com.debra.nfe.dom.enuns.AmbienteEnum;
import br.com.debra.nfe.dom.enuns.DocumentoEnum;
import br.com.debra.nfe.dom.enuns.EstadosEnum;
import br.com.debra.nfe.dom.enuns.PessoaEnum;
import br.com.debra.nfe.dom.enuns.StatusEnum;
import br.com.debra.nfe.exception.NfeException;
import br.com.debra.nfe.log.LoggerUtil;
import br.com.debra.nfe.schema.envEventoCancNFe.TEnvEvento;
import br.com.debra.nfe.schema.envEventoCancNFe.TRetEnvEvento;
import br.com.debra.nfe.schema.retConsCad.TRetConsCad;
import br.com.debra.nfe.schema_4.enviNFe.TEnviNFe;
import br.com.debra.nfe.schema_4.enviNFe.TNFe;
import br.com.debra.nfe.schema_4.enviNFe.TRetEnviNFe;
import br.com.debra.nfe.schema_4.inutNFe.TInutNFe;
import br.com.debra.nfe.schema_4.inutNFe.TRetInutNFe;
import br.com.debra.nfe.schema_4.retConsReciNFe.TRetConsReciNFe;
import br.com.debra.nfe.schema_4.retConsSitNFe.TRetConsSitNFe;
import br.com.debra.nfe.schema_4.retConsStatServ.TRetConsStatServ;
import br.com.debra.nfe.util.CancelamentoUtil;
import br.com.debra.nfe.util.ChaveUtil;
import br.com.debra.nfe.util.ConstantesUtil;
import br.com.debra.nfe.util.GlobalConstants;
import br.com.debra.nfe.util.InutilizacaoUtil;
import br.com.debra.nfe.util.RetornoUtil;
import br.com.debra.nfe.util.XmlNfeUtil;

public class Main {

	// Inicia As Configurações
	private static String entrada = "";
	private static String saida = "";
	private static String[] parametros = new String[3];
	private static ConfiguracoesNfe config;
	
	public static void main(String[] args) {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		try {
			entrada = br.readLine();
			
			GlobalConstants.protocolo = geraProtocolo();
			// Gravo a String de entrada
			LoggerUtil.log(Main.class, "[XML-ENTRADA]: " + entrada);
			
			parametros = entrada.split(";");
			
			if (parametros.length > 0 && !parametros[0].equals("")) {				
				config = iniciaConfiguracoes();				

				if (config != null) {
					boolean param3 = false;
					boolean param4 = false;					
					if (parametros.length >= 2) {
						//Verifica se o parametro 2 foi informado para p servico inutilizacaoNFe
						if(parametros.length==3 && parametros[1].equals("6")) {
							param3 = true;
						}
						//Verifica se o parametro 3 foi informado para p servico cancelarNFe
						if(parametros.length==4 && parametros[1].equals("5")) {
							param4 = true;
						}						
						// Reaproveito a variavel de entrada que passa a ser o XML da nota;
						entrada = parametros[0];

						if (parametros[1].equals("1")) {
							status(config);
						} else if (parametros[1].equals("2")) {
							situacaoNfe(config);
						} else if (parametros[1].equals("3")) {
							envioNFe(config);
						} else if (parametros[1].equals("4")) {
							consultaCadastroNFe(config);
						} else if (parametros[1].equals("5") && param4) {
							cancelarNFe(config);
						} else if (parametros[1].equals("6") && param3) {
							inutilizacaoNFe(config);
						} else {
							saida = "# Erro: Servico inexistente ou falta parametro para o servico solicitado";
						}
					} else {
						saida = "# Erro: Quantidade insuficiente de parametros informados";
					}
				}
			} else {
				saida = "# Erro: Paremetros de entrada nao informado";
			}
			
			// Gravo a String de saida
			LoggerUtil.log(Main.class, "[XML-SAIDA]: " + saida);
			
			OutputStreamWriter wr = new OutputStreamWriter(System.out);
			wr.write(saida, 0, saida.length());
			wr.write(13); // cr
			wr.write(10); // lf
			wr.flush();
			wr.close();
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/*
	 * # Status: 102 - Inutilizacao de numero homologado
	 * # Protocolo: 333200000050960
	 * # ProcInutilizacao : <?xml version="1.0" encoding="UTF-8"?><procInutNFe versao="4.00" xmlns="http://www.portalfiscal.inf.br/nfe"><inutNFe versao="4.00"><infInut Id="ID33200907926200016855001000001432000001432"><tpAmb>2</tpAmb><xServ>INUTILIZAR</xServ><cUF>33</cUF><ano>20</ano><CNPJ>09079262000168</CNPJ><mod>55</mod><serie>1</serie><nNFIni>1432</nNFIni><nNFFin>1432</nNFFin><xJust>Teste de Inutilização</xJust></infInut><Signature xmlns="http://www.w3.org/2000/09/xmldsig#"><SignedInfo><CanonicalizationMethod Algorithm="http://www.w3.org/TR/2001/REC-xml-c14n-20010315"/><SignatureMethod Algorithm="http://www.w3.org/2000/09/xmldsig#rsa-sha1"/><Reference URI="#ID33200907926200016855001000001432000001432"><Transforms><Transform Algorithm="http://www.w3.org/2000/09/xmldsig#enveloped-signature"/><Transform Algorithm="http://www.w3.org/TR/2001/REC-xml-c14n-20010315"/></Transforms><DigestMethod Algorithm="http://www.w3.org/2000/09/xmldsig#sha1"/><DigestValue>nM8M3pCzO7S2vppoWGiypkWv0oY=</DigestValue></Reference></SignedInfo><SignatureValue>L8A/+FCgsmQ/EI6DrhqJEdMTfPJdFnEGGQYsgXIMXMx5i1l3HMBIohjccrNZFChMsyggYlpt5mRUAuLYu1n04YH+ufS+ns1zcOtGELKXAUy6aMvolcY+OB0NYIECIBG7Mt094YeQ4tX69ZpND939+JQHIBGoSWOC94G3Ay9IGJsC92CIZwHkBzHJNHuot3z3kWZcbn0XmraR3cIWbac7UILdeGex77qH/RuSWsWhVo7fP7uuAloTGoVW9qXF4dG7F7zAB1hRgd3c7KvfzrbQe15MzruXAthBWYroaULttBxWuYOfUkFvlNkEs4i7lcBdj3fGaqjK0XMNAp9351Tw3w==</SignatureValue><KeyInfo><X509Data><X509Certificate>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</X509Certificate></X509Data></KeyInfo></Signature></inutNFe><retInutNFe versao="4.00"><infInut><tpAmb>2</tpAmb><verAplic>SVRS201905151442</verAplic><cStat>102</cStat><xMotivo>Inutilizacao de numero homologado</xMotivo><cUF>33</cUF><ano>20</ano><CNPJ>09079262000168</CNPJ><mod>55</mod><serie>1</serie><nNFIni>1432</nNFIni><nNFFin>1432</nNFFin><dhRecbto>2020-02-06T10:20:31-03:00</dhRecbto><nProt>333200000050960</nProt></infInut></retInutNFe></procInutNFe>
	 */
	public static void inutilizacaoNFe(ConfiguracoesNfe config) {
        try {

            //Informe o CNPJ do emitente
            String cnpj = XmlNfeUtil.TagXML(XmlNfeUtil.TagXML(entrada, "emit"), "CNPJ");
            //Informe a serie
            int serie = Integer.parseInt(XmlNfeUtil.TagXML(XmlNfeUtil.TagXML(entrada, "ide"), "serie"));
            //Informe a numeracao Inicial
            int numeroInicial = Integer.parseInt(XmlNfeUtil.TagXML(XmlNfeUtil.TagXML(entrada, "ide"), "nNF"));
            //Informe a numeracao Final
            int numeroFinal = Integer.parseInt(XmlNfeUtil.TagXML(XmlNfeUtil.TagXML(entrada, "ide"), "nNF"));
            //Informe a Justificativa da Inutilizacao
            String justificativa = parametros[2];
            //Informe a data do Cancelamento
            LocalDateTime dataCancelamento = LocalDateTime.now();

            //MOnta Inutilização
            TInutNFe inutNFe = InutilizacaoUtil.montaInutilizacao(DocumentoEnum.NFE,cnpj,serie,numeroInicial,numeroFinal,justificativa,dataCancelamento,config);

            //Envia Inutilização
            TRetInutNFe retorno = Nfe.inutilizacao(config,inutNFe, DocumentoEnum.NFE,true);

            //Valida o Retorno da Inutilização
            RetornoUtil.validaInutilizacao(retorno);

            //Resultado
            saida = "# Status: " + retorno.getInfInut().getCStat()+" - "+retorno.getInfInut().getXMotivo()
            	  + " # Protocolo: " + retorno.getInfInut().getNProt();

            //Cria ProcEvento da Inutilização
            String proc = InutilizacaoUtil.criaProcInutilizacao(config,inutNFe, retorno);
            System.out.println();
            saida += " # ProcInutilizacao : " + proc;

        } catch (Exception e) {
        	saida = "# Erro: " + e.getMessage();
        }		
	}
	
	public static void cancelarNFe(ConfiguracoesNfe config) {
		try {
			// Agora o evento pode aceitar uma lista de cancelaemntos para envio em Lote.
			// Para isso Foi criado o Objeto Cancela
			Evento cancela = new Evento();
			// Informe a chave da Nota a ser Cancelada
			cancela.setChave(calculaChaveAcesso(entrada));
			// Informe o protocolo da Nota a ser Cancelada
			cancela.setProtocolo(parametros[3]);
			// Informe o CNPJ do emitente
			cancela.setCnpj(XmlNfeUtil.TagXML(XmlNfeUtil.TagXML(entrada, "emit"), "CNPJ"));
			// Informe o Motivo do Cancelamento
			cancela.setMotivo(parametros[2]);
			// Informe a data do Cancelamento
			cancela.setDataEvento(LocalDateTime.now());
			
			// Monta o Evento de Cancelamento
			TEnvEvento enviEvento = CancelamentoUtil.montaCancelamento(cancela, config);

			// Envia o Evento de Cancelamento
			TRetEnvEvento retorno = Nfe.cancelarNfe(config, enviEvento, true, DocumentoEnum.NFE);

			// Valida o Retorno do Cancelamento
			RetornoUtil.validaCancelamento(retorno);

			// Resultado
			retorno.getRetEvento().forEach(resultado -> {
				saida = "# Status: " + resultado.getInfEvento().getCStat()+" - "+resultado.getInfEvento().getXMotivo()
					  + " # Chave: " + resultado.getInfEvento().getChNFe()
				      + " # Protocolo : " + resultado.getInfEvento().getNProt();
			});

			// Cria ProcEvento de Cacnelamento
			String proc = CancelamentoUtil.criaProcEventoCancelamento(config, enviEvento, retorno.getRetEvento().get(0));
			saida += " # ProcEvento : " + proc;
		} catch (Exception e) {
			saida = "# Erro: " + e.getMessage().replace("\n", "");
		}
	}

	public static void envioNFe(ConfiguracoesNfe config) {

		try {
			TNFe nfe = new TNFe();
				nfe = XmlNfeUtil.xmlToObject(entrada, TNFe.class);

			// Monta EnviNfe
			TEnviNFe enviNFe = new TEnviNFe();
			enviNFe.setVersao(ConstantesUtil.VERSAO.NFE);
			enviNFe.setIdLote("1");
			enviNFe.setIndSinc("1");
			enviNFe.getNFe().add(nfe);

			// Monta e Assina o XML
			enviNFe = Nfe.montaNfe(config, enviNFe, config.isValidacaoDocumento());

			// Envia a Nfe para a Sefaz
			TRetEnviNFe retorno = Nfe.enviarNfe(config, enviNFe, DocumentoEnum.NFE);

			// Valida se o Retorno é Assincrono
			if (RetornoUtil.isRetornoAssincrono(retorno)) {
				// Pega o Recibo
				String recibo = retorno.getInfRec().getNRec();
				int tentativa = 0;
				TRetConsReciNFe retornoNfe = null;

				// Define Numero de tentativas que ira tentar a Consulta
				while (tentativa < 15) {
					retornoNfe = Nfe.consultaRecibo(config, recibo, DocumentoEnum.NFE);
					if (retornoNfe.getCStat().equals(StatusEnum.LOTE_EM_PROCESSAMENTO.getCodigo())) {
						LoggerUtil.log(Main.class, "INFO: Lote Em Processamento, vai tentar novamente apos 1 Segundo.");
						Thread.sleep(1000);
						tentativa++;
					} else {
						break;
					}
				}

				RetornoUtil.validaAssincrono(retornoNfe);
				
				saida = "# Status: " + retornoNfe.getProtNFe().get(0).getInfProt().getCStat() + " - "
									 + retornoNfe.getProtNFe().get(0).getInfProt().getXMotivo()
					  + " # Protocolo: " + retornoNfe.getProtNFe().get(0).getInfProt().getNProt()
					  + " # XML Final: " + XmlNfeUtil.criaNfeProc(enviNFe, retornoNfe.getProtNFe().get(0));
			} else { // Se for else o Retorno é Sincrono
				
				// Valida Retorno Sincrono
				RetornoUtil.validaSincrono(retorno);
				
				saida = "# Status: " + retorno.getProtNFe().getInfProt().getCStat() + " - "
									 + retorno.getProtNFe().getInfProt().getXMotivo()
					  + " # Protocolo: " + retorno.getProtNFe().getInfProt().getNProt()
					  + " # Xml Final :" + XmlNfeUtil.criaNfeProc(enviNFe, retorno.getProtNFe());
			}
			} catch (JAXBException e) {
				saida = "# Erro: " + e.getCause().getMessage();
			} catch (NfeException e) {
				saida = "# Erro: " + e.getMessage();
			} catch (InterruptedException e) {
				saida = "# Erro: " + e.getMessage();
			}

	}
		
    public static void situacaoNfe(ConfiguracoesNfe config) {
        try {
        	
            //Informe a chave a ser Consultada
            String chave = calculaChaveAcesso(entrada);

            //Efetua a consulta
            TRetConsSitNFe retorno = Nfe.consultaXml(config, chave, DocumentoEnum.NFE);

            //Resultado
            saida = "# Status: " + retorno.getCStat() + " - " + retorno.getXMotivo();
        } catch (Exception e) {
        	saida = "# Erro: " + e.getMessage();
        }
    }
    
	public static void status(ConfiguracoesNfe config) {
		try {
			// Efetua Consulta
			TRetConsStatServ retorno = Nfe.statusServico(config, DocumentoEnum.NFE);

			// Resultado
			saida = "# Status: " + retorno.getCStat() + " - " + retorno.getXMotivo();
		} catch (Exception e) {
			saida = "# Erro: " + e.getMessage();
		}
	}

    public static void consultaCadastroNFe(ConfiguracoesNfe config) {
        try {
        	//Capturo o cnpj do XML da nota
        	String cnpj = XmlNfeUtil.TagXML(XmlNfeUtil.TagXML(entrada, "emit"), "CNPJ");
        	
            //Envia a Consulta
            TRetConsCad retorno = Nfe.consultaCadastro(config, PessoaEnum.JURIDICA, cnpj, config.getEstado());

            //Valida o Retorno da Consulta Cadastro
            RetornoUtil.validaConsultaCadastro(retorno);

            //Resultado
            saida = "# Status: " + retorno.getInfCons().getCStat() + " - " + retorno.getInfCons().getXMotivo();
            /*retorno.getInfCons().getInfCad().forEach( cadastro -> {
                saida += "\n# Razao Social: " + cadastro.getXNome();
                saida += "\n# Cnpj: " + cadastro.getCNPJ();
            	saida += "\n# Ie: " + cadastro.getIE();
            });*/

        } catch (Exception e) {
            saida = "# Erro: " + e.getMessage();
        }
    }
	
	public static ConfiguracoesNfe iniciaConfiguracoes() {
		ConfiguracoesNfe conf = null;
		Certificado certificado;
		String CertCli = "";
		String PassCli = "";

		try {
			//Leio o xml da nota no arquivo			
			//entrada = XmlNfeUtil.leXml("./xmlEnviNFe.xml");
			
			// Extraio do xml da NFe o CNPJ do Emitente
			String cnpj = XmlNfeUtil.TagXML(XmlNfeUtil.TagXML(entrada, "emit"), "CNPJ");
			
			if (cnpj.length() == 0) {
				saida = "# Erro: 101 - CNPJ nao localizado no XML";
				return conf;
			} else {				
				if (cnpj.length() == 14) {
					// Leio o arquivo de configuracao do cliente
					String ArqConfCli = GlobalConstants.ARQCERTIF + "/" + cnpj + ".xml";
					String xmlConfCli = XmlNfeUtil.leXml(ArqConfCli);
					CertCli = XmlNfeUtil.TagXML(xmlConfCli, "cert");
					PassCli = XmlNfeUtil.TagXML(xmlConfCli, "pass");
				} else {
					saida = "# Erro: 103 - CNPJ de tamanho incorreto";
					return conf;
				}
			}
			
			// Leio o certificado.
			certificado = CertificadoService.certificadoPfx(CertCli, PassCli);
			
			//Criar ambiente de configuracoes
			conf = ConfiguracoesNfe.criarConfiguracoes(EstadosEnum.getByCodigoIbge(XmlNfeUtil.TagXML(entrada, "cUF")), AmbienteEnum.getByCodigo(GlobalConstants.AMBIENTE), certificado, GlobalConstants.ARQSCHEMA);

			//Configuracao do ambiente
			conf.setRetry(GlobalConstants.TENTATIVA);				
			conf.setTimeout(GlobalConstants.TIMEOUTWS);
			conf.setArquivoWebService(GlobalConstants.ARQURLNFE);
			conf.setValidacaoDocumento(GlobalConstants.VALIDACAO);		
		} catch (IOException e) {
			saida = "# Erro: " + e.getMessage();
		} catch (CertificadoException e) {
			saida = "# Erro: " + e.getMessage();
		}
		return conf;
	}
	
	private static String calculaChaveAcesso(String xmlChave) {
    	String str = XmlNfeUtil.TagXML(XmlNfeUtil.TagXML(xmlChave, "ide"), "dhEmi").substring(0, 10);
    	str = str.replace("-", "");
    	
    	DateTimeFormatter dateformatter = DateTimeFormatter.ofPattern("yyyyMMdd");
    	LocalDate ld = LocalDate.parse(str, dateformatter);
    	LocalDateTime data = ld.atStartOfDay();
    	
    	ChaveUtil chaveUitl = new ChaveUtil(EstadosEnum.getByCodigoIbge(XmlNfeUtil.TagXML(xmlChave, "cUF")), 
    										XmlNfeUtil.TagXML(XmlNfeUtil.TagXML(xmlChave, "emit"), "CNPJ"), 
    										XmlNfeUtil.TagXML(XmlNfeUtil.TagXML(xmlChave, "ide"), "mod"), 
    										Integer.parseInt(XmlNfeUtil.TagXML(XmlNfeUtil.TagXML(xmlChave, "ide"), "serie")), 
    										Integer.parseInt(XmlNfeUtil.TagXML(XmlNfeUtil.TagXML(xmlChave, "ide"), "nNF")), 
    										XmlNfeUtil.TagXML(XmlNfeUtil.TagXML(xmlChave, "ide"), "tpNF"), 
    										XmlNfeUtil.TagXML(XmlNfeUtil.TagXML(xmlChave, "ide"), "cNF"), 
    										data);
    	        	
    	
        //Informe a chave a ser Consultada
		return chaveUitl.getChaveNF().replace("NFe", "");
	}
	
	public static String geraProtocolo(){
	    Calendar calendar = Calendar.getInstance();
	    java.util.Date now = calendar.getTime();
	    java.sql.Timestamp currentTimestamp = new java.sql.Timestamp(now.getTime());
	    String protocolo;

	    protocolo = currentTimestamp.toString();
	    protocolo = protocolo.replaceAll(" ","");
	    protocolo = protocolo.replaceAll("-","");
	    protocolo = protocolo.replaceAll(":","");

	    Random numRandon = new Random();
	    int numero = numRandon.nextInt(99999);

	    protocolo = protocolo + "." + Integer.toString(numero);

	    return protocolo;
	}
	

}
