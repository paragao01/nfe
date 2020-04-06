/*
 * Created on 03/11/2000
 *
 */
package br.com.debra.nfe.util;

import java.math.BigInteger;
import java.sql.Date;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * @author Eduardo
 * 
 */
public class Formatador {

	/**
	 * Recebe uma data string DDMMAAAA
	 * 
	 * @param String
	 *            data
	 * @return String data DD/MM/AAAA
	 */
	public static String formataData(String data) {
		if(Util.Empty(data)){return data.trim();}
		String dataPos = "";
		if (!Util.IsZero(data)) {
			dataPos += data.substring(0, 2);
			dataPos += "/";
			dataPos += data.substring(2, 4);
			dataPos += "/";
			dataPos += data.substring(4, 8);
		} else {
			dataPos = data;
		}
		return dataPos;
	}

	/* Gravar no banco PostGre*/
	public static Date parseDate(String dt) {
		Date resp = null;
		String dtNova = "";
		try {
			dt = dt.replace("/", "");
			dtNova += dt.substring(4, 8);
			dtNova += "-";
			dtNova += dt.substring(2, 4);
			dtNova += "-";			
			dtNova += dt.substring(0, 2);
			resp = java.sql.Date.valueOf(dtNova);
		} catch (Exception exc) {}

		return resp;
	}

	public static Date formataData(String dia, String mes, String ano) {
		int iDia = Integer.parseInt(dia);
		int iMes = Integer.parseInt(mes);
		int iAno = Integer.parseInt(ano);
		GregorianCalendar gre = new GregorianCalendar(iAno, iMes, iDia);
		return new Date(gre.getTimeInMillis());
	}

	/**
	 * Retorna a data no formato DD/MM/AAAA
	 * 
	 * @param java
	 *            .sql.Date dataSQL
	 * @return String data
	 */
	public static String formatarData(Date dataSQL) {
		java.util.Date data = new java.util.Date(dataSQL.getTime());

		Locale brasil = new Locale("pt", "BR");
		DateFormat df = DateFormat.getDateInstance(DateFormat.MEDIUM, brasil);

		return df.format(data);
	}

	/**
	 * Retorma a data no formato DD/MM/AAAA
	 * 
	 * @param java
	 *            .util.Date dataSQL
	 * @return
	 */
	public static String formatarData(java.util.Date dataSQL) {
		Date data = new Date(dataSQL.getTime());
		Locale brasil = new Locale("pt", "BR");
		DateFormat df = DateFormat.getDateInstance(DateFormat.MEDIUM, brasil);

		return df.format(data);
	}

	/**
	 * Retorma a data com o mes escrito por extenso
	 * 
	 * @param Date
	 *            dataSQL
	 * @return String data
	 */
	public static String getDataPorExtenso(Date dataSQL) {
		Date data = new Date(dataSQL.getTime());
		Locale brasil = new Locale("pt", "BR");
		DateFormat df = DateFormat.getDateInstance(DateFormat.LONG, brasil);
		return df.format(data);
	}

    /**
     * Metodo retorna a hora, ex: 15:15:44
     * @return String contendo a hora
     */
    public static String getHoraMinutoSegundo(){
                SimpleDateFormat sf = new SimpleDateFormat("HH:mm:ss");
                return sf.format(new Date(System.currentTimeMillis()));
    }

	/**
	 * Retorma a data com o mes escrito por extenso
	 * 
	 * @param SLQTimeSta
	 *            dataSQL
	 * @return String data
	 */
	public static String getDataPorExtenso(Timestamp dataSQL) {
		return getDataPorExtenso(new java.sql.Date(dataSQL.getTime()));
	}

	/**
	 * Retorma um valor numerico formatado
	 * 
	 * @param float valor
	 * @return String data
	 */
	public static String getFormatarValor(double valor) {
		DecimalFormat formatador = new DecimalFormat();
		formatador.applyPattern("R$ ###,##0.00;R$ (###,##0.00)");

		String strValor = formatador.format(valor);
		strValor.replaceAll(",", "@");
		strValor.replaceAll(",", ".");
		strValor.replaceAll("@", ".");

		return strValor;
	}

	public static String getFormatMoeda(double valor) {
		DecimalFormat formatador = new DecimalFormat();
		formatador.applyPattern("###,##0.00;(###,##0.00)");

		String strValor = formatador.format(valor);
		strValor.replaceAll(",", "@");
		strValor.replaceAll(",", ".");
		strValor.replaceAll("@", ".");

		return strValor;
	}

	public static String getFormatFloat(double valor) {
		DecimalFormat formatador = new DecimalFormat();
		formatador.applyPattern("####.##0.00;(####.##0.00)");

		String strValor = formatador.format(valor);
		strValor.replaceAll(",", "@");
		strValor.replaceAll(",", ".");
		strValor.replaceAll("@", ".");

		return strValor;
	}

	/**
	 * Retorma um valor numerico formatado
	 * 
	 * @param float valor
	 * @return String data
	 */
	public static String getFormatarPercentual(float valor) {
		DecimalFormat formatador = new DecimalFormat();
		formatador.applyPattern("###,##0.0; (###,###.0)");

		String strValor = formatador.format(valor);
		strValor.replaceAll(",", "@");
		strValor.replaceAll(",", ".");
		strValor.replaceAll("@", ".");

		return strValor;
	}

	public static String getFormataDataSQL(Date data) {
		GregorianCalendar gre = new GregorianCalendar();
		gre.setTime(data);

		String dia = String.valueOf(gre.get(GregorianCalendar.DATE));
		String mes = String.valueOf(gre.get(GregorianCalendar.MONTH) + 1);
		String ano = String.valueOf(gre.get(GregorianCalendar.YEAR));

		if (dia.length() < 2)
			dia = "0" + dia;
		if (mes.length() < 2)
			mes = "0" + mes;

		return ano + "-" + mes + "-" + dia;
	}

	/**
	 * Metodo que retorna a data de atual
	 * 
	 * @return
	 */
	public static Date dataHoje() {
		GregorianCalendar gre = new GregorianCalendar();
		Date hoje = new Date(gre.getTimeInMillis());
		return hoje;
	}

	public static Date dataHojeSomandoDias(int i) {
		GregorianCalendar gre = new GregorianCalendar();
		gre.add(GregorianCalendar.DATE, i); // Soma i dias
		Date data = new Date(gre.getTimeInMillis());
		return data;
	}

	/**
	 * Metodo que formata um numero, preenchendo com zeros, ate ele ficar o
	 * tamanho indicado por qtde
	 * 
	 * @param numero
	 * @param qtde
	 * @return String
	 */
	public static String preencheZeros(int numero, int qtde) {
		String texto = String.valueOf(numero);
		while (texto.length() < qtde) {
			texto = "0" + texto;
		}
		return texto;
	}

	public static boolean hasSpecialChar(String input) {
		boolean flag = false;
		if ((input != null) && (input.length() > 0)) {
			char c;
			for (int i = 0; i < input.length(); i++) {
				c = input.charAt(i);
				switch (c) {
				case '<':
					flag = true;
					break;
				case '>':
					flag = true;
					break;
				case '"':
					flag = true;
					break;
				case '&':
					flag = true;
					break;
				}
			}
		}
		return flag;
	}

	public static String htmlFilter(String input) {
		// if (Formatador.hasSpecialChar(input)) {
		// return input;
		// }
		StringBuffer filtered = new StringBuffer(input.length());
		char c;
		for (int i = 0; i < input.length(); i++) {
			c = input.charAt(i);
			switch (c) {
			case '<':
				filtered.append("&lt;");
				break;
			case '>':
				filtered.append("&gt;");
				break;
			case '"':
				filtered.append("&quot;");
				break;
			case '&':
				filtered.append("&amp;");
				break;
			case ' ':
				filtered.append("&nbsp;");
				break;
			default:
				filtered.append(c);
			}
		}
		return (filtered.toString());
		// return "foi";
	}

	/**
	 * Recebe String e formata mascara do CPF
	 * 
	 * @param cpf
	 * @return cpf String
	 */
	public static String formataCPF(String cpf) {
		String cpfPos = "";
		if (!Util.IsZero(cpf)) {
			cpfPos += cpf.substring(0, 3);
			cpfPos += ".";
			cpfPos += cpf.substring(3, 6);
			cpfPos += ".";
			cpfPos += cpf.substring(6, 9);
			cpfPos += "-";
			cpfPos += cpf.substring(9, 11);
		} else {
			cpfPos = cpf;
		}
		return cpfPos;
	}

	public static String formataHora(String hora) {
		String horaPos = "";
		if (!Util.IsZero(hora)) {
			horaPos += hora.substring(0, 2);
			horaPos += ":";
			horaPos += hora.substring(2, 4);
			//horaPos += ":";
			//horaPos += hora.substring(4, 6);
		} else {
			horaPos = hora;
		}
		return horaPos;
	}

	public static String formataCNPJ(String cnpj) {
		String cnpjPos = "";
		if (!Util.IsZero(cnpj)) {
			cnpjPos += cnpj.substring(0, 2);
			cnpjPos += ".";
			cnpjPos += cnpj.substring(2, 5);
			cnpjPos += ".";
			cnpjPos += cnpj.substring(5, 8);
			cnpjPos += "/";
			cnpjPos += cnpj.substring(8, 12);
			cnpjPos += "-";
			cnpjPos += cnpj.substring(12, 14);
		} else {
			cnpjPos = cnpj;
		}
		return cnpjPos;
	}

	public static String formataTelefone(String fone) {
		String fonePos = "";
		if (!Util.IsZero(fone)) {
			fonePos += fone.substring(0, 4);
			fonePos += "-";
			fonePos += fone.substring(4, 8);
		} else {
			fonePos = fone;
		}
		return fonePos;
	}

	public static String formataCEP(String cep) {
		String cepPos = "";
		if (!Util.IsZero(cep)) {
			cepPos += cep.substring(0, 5);
			cepPos += "-";
			cepPos += cep.substring(5, 8);
		} else {
			cepPos = cep;
		}
		return cepPos;
	}

	public static String formataCCorrente(String conta) {
		String contaPos = "";
		if (!Util.IsZero(conta)) {
			contaPos += conta.substring(0, 5);
			contaPos += "-";
			contaPos += conta.substring(5, 6);
		} else {
			contaPos = conta;
		}
		return contaPos;
	}

	public static String formataDesde(String desde) {
		String desdePos = "";
		if (!Util.IsZero(desde)) {
			desdePos += desde.substring(0, 2);
			desdePos += "/";
			desdePos += desde.substring(2, 6);
		} else {
			desdePos = desde;
		}
		return desdePos;
	}

	public static String formatDouble(Double num) throws ParseException {
		String mask;
		if (num == null)
			return "";
		try {
			ResourceBundle resource = ResourceBundle
					.getBundle("ApplicationResources");
			mask = resource.getString("double.mask");

			// System.out.println("formatDouble - DoubleMask OK!");
		} catch (Exception exc) {
			mask = "###,###,###,###,###,##0.00";
		}
		return Formatter.formatNumber(num, mask);
	}

	public static String zeroLeft(String origem, int tamanho) {
		StringBuffer buffer = new StringBuffer();

		if (origem == null || origem.length() == 0) {
			origem = "";
		}

		if (origem.length() < tamanho) {
			for (int i = 0; i < (tamanho - origem.length()); i++) {
				buffer.append('0');
			}
			buffer.append(origem);
		} else {
			buffer.append(origem);
		}
		return buffer.toString();
	}

	public static String spaceLeft(String origem, int tamanho) {
		StringBuffer buffer = new StringBuffer();

		if (origem == null) {
			origem = "";
		}
		if (origem.length() < tamanho) {
			for (int i = 0; i < (tamanho - origem.length()); i++) {
				buffer.append(' ');
			}
			buffer.append(origem);
		} else {
			buffer.append(origem);
		}
		return buffer.toString();
	}

	/**
	 * Remove uma mascara de formatacao, reescrevendo a string apenas com letras
	 * e numeros
	 * 
	 * @param String
	 * value - valor a ser des-formatado
	 */
	public static String unformatString(String value) {
		String formatted = "";

		// Remove a mascara e retorna o valor
		for (int i = 0; i < value.length(); i++) {
			if ((value.charAt(i) >= 65 && value.charAt(i) <= 90)
					|| (value.charAt(i) >= 97 && value.charAt(i) <= 122)
					|| (value.charAt(i) >= 48 && value.charAt(i) <= 57))
				formatted = formatted + value.charAt(i);
		}
		return formatted;
	}
	
	/**
	 * Formata valores sem casas decimais
	 * 
	 * @throws ParseException
	 * @throws NumberFormatException
	 */
	public static String MaskSemDecimal(String str) {
		try {
			double val = 0;
			str = retiraZerosEsquerda(str);
			val = Double.parseDouble(str);
			str = formatDouble(Double.valueOf(val));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return str;
	}

	/**
	 * Formata valores recebidas do AS400 999.999,99
	 * 
	 * @throws ParseException
	 * @throws NumberFormatException
	 */
	public static String MaskAS400(String str) {
		try {
			if(Util.Empty(str)){return str.trim();}
			double val = 0;
			str = retiraZerosEsquerda(str);
			val = Double.parseDouble(str);
			str = formatDouble(Double.valueOf(val / 100));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return str;
	}
	
	/**
	 * Formata valores recebidas do AS400 9.999,9
	 * 
	 * @throws ParseException
	 * @throws NumberFormatException
	 */
	public static String MaskAS400Decimal1(String str) {
		try {
			double val = 0;
			str = retiraZerosEsquerda(str);
			val = Double.parseDouble(str);
			str = formatDouble(Double.valueOf(val / 10));
			if(str.equals("0,00")){
				str = "0,0";
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return str;
	}

	/**
	 * Tira os zeros esquerda de uma string que representa um nmero inteiro.
	 * 
	 * @param str
	 *            O string da qual ser retirada os zeros esquerda.
	 * @return O string sem zeros esquerda. Aragao
	 */
	public static String retiraZerosEsquerda(String str) {
		if (str != null && str.trim().length() > 0) {
			BigInteger i = new BigInteger(str.trim());
			return i.toString();
		} else {
			return str;
		}
	}

	/**
	 * Formata valores para envio pro AS400 000099999999
	 * 
	 * @throws ParseException
	 * @throws NumberFormatException
	 */
	public static String UnMaskAS400(String str, int tam) {
		if (str == null || str.length() == 0) {
			str = "";
			for (int i = 0; i < tam; i++) {
				str += "0";
			}
		} else {
			str = zeroLeft(unformatString(str), tam);
		}
		return str;
	}

	/* Preencher com espacos a direita */
	public static String formataCampo(String campo, int tamanho) {
		String ret = "";
		int auxLen = 0;
		if (campo == null ||campo.equals("")) {
			for (int i = auxLen; i < tamanho; i++)
				ret += " ";
		} else {
			auxLen = campo.length();
			if (auxLen < tamanho) {
				ret += campo;
				for (int i = auxLen; i < tamanho; i++)
					ret += " ";
			} else {
				for (int i = 0; i < tamanho; i++)
					ret += campo.substring(i, i + 1);
			}
		}
		return ret;
	}

	/* Preencher zeros a esquerda */
	public static String zeroEsquerda(String campo, int tamanho) {
		String ret = "";
		int auxLen = 0;
		if (campo == null) {
			for (int i = auxLen; i < tamanho; i++)
				ret += "0";
		} else {
			auxLen = campo.length();
			if (auxLen < tamanho) {
				for (int i = auxLen; i < tamanho; i++)
					ret += "0";
				ret += campo;
			} else {
				for (int i = 0; i < tamanho; i++)
					ret += campo.substring(i, i + 1);
			}
		}
		return ret;
	}

	/**
	 * Formatacao de CPF e CNPJ
	 * @param tipoPessoa
	 * @param documento
	 * @return
	 */
	public static String formataDocumento(String tipoPessoa, String documento) {
		String docPos = "";
		if(Util.IsZero(documento)||Util.Empty(documento)){
			return documento;
		}
		if(tipoPessoa.equals("F") || tipoPessoa.equals("1")){
			if(documento.length()==11){
				docPos = documento;
			}else if(documento.length()==14){
				docPos = documento.substring(3, 14);
			}else if(documento.length()==15){
				docPos = documento.substring(4, 15);
			}
			docPos = formataCPF(docPos);	
		}else if(tipoPessoa.equals("J") || tipoPessoa.equals("2")){
			if(documento.length()==14){
				docPos = documento;
			}else if(documento.length()==15){
				docPos = documento.substring(1, 15);
			}
			docPos = formataCNPJ(docPos);
		}else{
			return documento;
		}					
		return docPos;
	}

}
