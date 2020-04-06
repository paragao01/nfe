package br.com.debra.nfe.util;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class ArquivoParametro {

	public static String buscaparametro(String PARM)
	{
		String ret = "";
		String arquivo_parametro    = System.getProperty("user.dir")+"/nfe.conf";
	   	String arquivo_parametro_registro;
    	BufferedReader fr;
		try {
			fr = new BufferedReader(new FileReader(arquivo_parametro));
			
			while ((arquivo_parametro_registro = fr.readLine()) != null) {
				if (arquivo_parametro_registro.trim().length() > 0) {
					if (!arquivo_parametro_registro.substring(0, 1).equals("*")) {
						if (arquivo_parametro_registro.substring(0, 9).trim().equals(PARM.trim())) {
							int lim = arquivo_parametro_registro.trim().length();
							ret = arquivo_parametro_registro.substring(10, lim).trim();
							break;
						}
					}
				}
			}
			fr.close();
		} catch (FileNotFoundException e) {
			System.out.println("Erro ao abrir o arquivo parametro: \n"+e.getMessage()+ "\nPasta atual"+System.getProperty("user.dir"));
		} catch (IOException e) {
			System.out.println("Erro ao ler o arquivo parametro: \n"+e.getMessage()+ "\nPasta atual"+System.getProperty("user.dir"));
		}
	    if (ret.trim().length() < 1) 	
	    	System.out.println("Erro parametro não encontrato "+ PARM+ " Pasta atual : "+System.getProperty("user.dir")); 
		return ret;
	}
	
	
	public static void main(String[] args) {
		
		
		System.out.println(buscaparametro("DRVBANCO"));
		
		
	  			    
	}
}
