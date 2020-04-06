package br.com.debra.nfe.log;

import java.util.Calendar;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

import br.com.debra.nfe.util.GlobalConstants;

import java.text.SimpleDateFormat;

/**
 * @author Samuel Oliveira - samuk.exe@hotmail.com Data: 03/03/2019 - 11:34
 */
public class LoggerUtil {
	
	private static FileWriter writer;

	public static void log(Class<?> classe, String msg) {
		Date agora = new Date();
		boolean debug = false;
		String protocolo = GlobalConstants.protocolo;
		String logPrefix = "./log/NFe";
		SimpleDateFormat amd = new SimpleDateFormat("'" + logPrefix	+ "'yyyyMMdd");
		String logName = amd.format(agora);			

		String registro = "";
		try {	
			writer = new FileWriter(logName, true);
			
			// create a java calendar instance
			Calendar calendar = Calendar.getInstance();
			
			// get a java.util.Date from the calendar instance.
			// this date will represent the current instant, or "now".
			java.util.Date now = calendar.getTime();
			
			// a java current time (now) instance
			java.sql.Timestamp currentTimestamp = new java.sql.Timestamp(now.getTime());

			// Date agora = new Date();
			// String logTime = hms.format(agora);
			// String registro = currentTimestamp + " " + logTime + " " + msg +
			// "\n";
			
			//if(msg==null){msg="";}
			registro = currentTimestamp.toString()+" "+protocolo+" "+classe.getName()+" ["+msg+"]\n";		
			writer.write(registro);
			writer.flush();			
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (debug)
			System.out.print(registro);
	}

	public void close() throws Exception {
		writer.close();
	}

}
