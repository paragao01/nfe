package br.com.debra.nfe.util;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.axis2.context.MessageContext;

import br.com.debra.nfe.log.LoggerUtil;

public class Util extends Formatador{	
	
	/**
	 * Verifica se a string esta branco
	 * @param str
	 * @return boolean
	 */
	public static boolean Empty(String str){
		if(str!=null)
			if(str.trim().length()==0) return true;
		return false;
	}
	
	/**
	 * Verifica se str1 esta contida em str
	 * @param str
	 * @param str1
	 * @return boolean
	 */
	public static boolean strstr(String str, String str1){
		if(str.indexOf(str1)>=0)return true;
		return false;
	}
	
	
	public static String copyStr (String src, String trg, int pos){
		int tamSrc    = src.length();
		int tamTrg    = trg.length();
		String trgAnt = trg.substring(0, pos);
		int tamAnt    = trgAnt.length();
		String trgPos = trg.substring(tamAnt+tamSrc,tamTrg);
		
		return (trgAnt+src+trgPos);
	}
	
	public static boolean isnumeric(String str){
		boolean valor = false;
		int i;		        
		int tam;
		
		tam = str.length();
		if (tam == 0)
			return false;

		str.replace(",", "."); //Converte a virgula em ponto.

		for (i=0;i < tam; i++) {
			if(strstr(String.valueOf(str.charAt(i)), ".")){ 
				if (valor==false)
					return false;
			}
			else
				if (isdigit(String.valueOf(str.charAt(i))))
					valor=true;
				else
					return true;
		}
		return valor;
	}
	
	public static boolean isdigit(String val){
		try {  
			int num = Integer.parseInt(val);
			if(num==0){} //So para tirar o warn;
			return true;
		} catch( NumberFormatException ex ) {
			return false;
		} catch( Exception exc ){
			return false;
		}
	}
	
	public static boolean IsZero (String str) {
		if(str==null)
			return true;
		str = str.trim();
		for (int i = 0; i < str.length(); i++) {
			if(!strstr(String.valueOf(str.charAt(i)), "0"))
                return false;
			
		}
		return true;
	}
	
	public static int val (String str, int inicio, int tam){
        String valor = "";
        String c;
        int inteiro;
        int i;
        
		try {
	        zeroEsquerda(valor, tam);

	        for (i=0;i < tam;i++) {
	        	c = String.valueOf(str.charAt(inicio+i));
	        	if (!isdigit(c))
	        		break;

	        	valor += c; // Obtem os numeros.
	        }
			
			inteiro = Integer.parseInt(valor);
			return inteiro;
		} catch( NumberFormatException ex ) {
			return 0;
		} catch( Exception exc ){
			return 0;
		}        
	}

	public static boolean IsCPF (String CPF){
		int i;int j;int d;

		if (CPF.length() == 0) return false;

		if (IsZero(CPF)) return false;

		unformatString(CPF);

		if (!isnumeric(CPF)) return false;

		if (CPF.length() > 11) return false;

		Util.zeroEsquerda(CPF, 11);

		for (i = 10; i <= 11; i++) {
			d = 0;
			for (j = i; j >= 2; j--)
				d += val(CPF, i-j, 1) * j;

			d = (d %= 11) < 2 ? 0 : 11 - d;
			if (val(CPF, i-1, 1) != d)
				return false;
		}

		return true;
	}
	
	public static boolean IsCNPJ (String str){
		int i;int j;int d;

		if (str.length() == 0) return false;

		if (IsZero(str)) return false;

		unformatString(str);

		if (!isnumeric(str)) return false;

		if (str.length() != 14)	return false;

		for (i = 13; i <= 14; i++) {
			d = 0;
			for (j = i; j >= 2; j--)
				d += val(str, i-j, 1) * ((j-2) % 8 + 2);

			d = (d %= 11) < 2 ? 0 : 11 - d;
			if (val(str, i-1, 1) != d)
				return false;
		}
		
		return true;
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
	
	/*
	 * Valida email retorna true se for valido e false se for invalido
	 * 	
	 */
    public static boolean isEmailValid(String email) {
        if ((email == null) || (email.trim().length() == 0))
            return false;

        String emailPattern = "\\b(^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@([A-Za-z0-9-])+(\\.[A-Za-z0-9-]+)*((\\.[A-Za-z0-9]{2,})|(\\.[A-Za-z0-9]{2,}\\.[A-Za-z0-9]{2,}))$)\\b";
        Pattern pattern = Pattern.compile(emailPattern, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
    
    public static boolean isDateValid(String dateString, String pattern) {   
        try{
            SimpleDateFormat sdf = new SimpleDateFormat(pattern);
            if (sdf.format(sdf.parse(dateString)).equals(dateString))
                return true;
        }catch (ParseException pe) {}
        
        return false;
    }

	
	/*Uma outra maneira com o spring para obter MD5 - obter a lib spring-core-4.3.14.RELEASE.jar
	 * 
	 *String password = org.springframework.util.DigestUtils.md5DigestAsHex("112233".getBytes());
	 *System.out.println(password);
	 **/
	public static String converteMD5(String source, String protocolo, LoggerUtil log ) {
		String hashtext = "";
        MessageDigest md;
		try {
			md = MessageDigest.getInstance("MD5");
	        byte[] messageDigest = md.digest(source.getBytes());
	        BigInteger number = new BigInteger(1, messageDigest);
	        hashtext = number.toString(16);
		} catch (NoSuchAlgorithmException e) {
			LoggerUtil.log(Util.class, "[ERRO]"+ e.getMessage());
		}
		return hashtext.toUpperCase();
	}
	
	public static String lerTag(String tag, String xml) {
		String str = "";
		String tagFin = "";
		int posIni = 0;
		int posFin = 0;
		posIni = xml.indexOf(tag);
		if(posIni > 0) {
			tagFin = "</"+tag.substring(1, tag.length());
			posFin = xml.indexOf(tagFin);
			if(posFin > 0) {
				str = xml.substring(posIni+tag.length(), posFin);
			}
		}
		return str;
	}
	
	public static String gravaTag(String tag, String valorTag, String xml) {
		String tagFin = "";
		String str = "";
		int posIni = 0;
		int posFin = 0;
		posIni = xml.indexOf(tag);
		if(posIni > 0) {
			tagFin = "</"+tag.substring(1, tag.length());
			posFin = xml.indexOf(tagFin);
			if(posFin > 0) {
				str = tag+xml.substring(posIni+tag.length(), posFin)+tagFin;
				valorTag = tag+valorTag+tagFin;
				xml = xml.replace(str, valorTag);
			}
		}
		return xml;
	}
	
	public static String limpaXML(String xml) {
		String str = "";
		int pos = 0;
		pos = xml.indexOf("<VERSAO>");
		if(pos > 0) {
			str = xml.substring(0, pos);
			xml = xml.replace(str, "<SPCA-XML>");
		}
		return xml;
	}

	public boolean validateDate(String date) {
	    SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyy");
	    try {
	        sdf.parse(date);
	        return true;
	    }
	    catch(ParseException ex) {
	        return false;
	    }
	}
	
	public static String lerAllBytesJava7(String filePath) {
		String content = "";

		try {
			content = new String(Files.readAllBytes(Paths.get(filePath)));
		} catch (IOException e) {
			e.printStackTrace();
		}

		return content;
	}

	public static String ipRemoto() {
		String ip = "127.0.0.1";
		if(MessageContext.getCurrentMessageContext()!=null) {
			ip = (String) (MessageContext.getCurrentMessageContext()).getProperty(MessageContext.REMOTE_ADDR);
		}
		//Caso seja localhost substituir
		if(ip.equals("0:0:0:0:0:0:0:1")) {
			ip = "127.0.0.1";
		}
		return ip;
	}
	
}