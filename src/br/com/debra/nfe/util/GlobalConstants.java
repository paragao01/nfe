package br.com.debra.nfe.util;

public class GlobalConstants {
    /*Ambiente de Producao = 1 e Homologacao = 2*/
	public static String AMBIENTE = ArquivoParametro.buscaparametro("AMBIENTE");
	
	/*Arquivo com os as URL's dos WebServices da Receita Federal*/
	public static String ARQURLNFE = "."+ArquivoParametro.buscaparametro("ARQURLNFE");

	/*Arquivo com os schemas dos servicos*/
	public static String ARQSCHEMA = "."+ArquivoParametro.buscaparametro("ARQSCHEMA");

	/*Arquivo com os schemas dos servicos*/
	public static String ARQCERTIF = "."+ArquivoParametro.buscaparametro("ARQCERTIF");
	
	/*Diretorio do log*/
	public static String PASTALOG = System.getProperty("user.dir")+ArquivoParametro.buscaparametro("PASTALOG");
		
	/*Timeout de conexao milissegundos*/
	public static int TIMEOUTWS = Integer.parseInt(ArquivoParametro.buscaparametro("TIMEOUTWS"));

	/*Tentativa a cada milissegundos*/
	public static int TENTATIVA = Integer.parseInt(ArquivoParametro.buscaparametro("TENTATIVA"));
	
	/*Validacao dos XML's*/
	public static boolean VALIDACAO = (ArquivoParametro.buscaparametro("VALIDACAO").equals("TRUE")?true:false);

	/*Informar o IP remoto*/
	public static String IPREMOTO;
	
	public static String protocolo;
	
}