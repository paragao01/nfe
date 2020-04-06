package br.com.debra.nfe.util;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *  Formatador e parser para tipos Timestamp, Long, String, Integer, Double
 *
  */
public class Formatter {

    private static DecimalFormat numFmt = null;

    private static SimpleDateFormat dateFmt = new SimpleDateFormat("dd/MM/yyyy");


    static {
        DecimalFormatSymbols decSymbols = new DecimalFormatSymbols();
        decSymbols.setGroupingSeparator('.');
        decSymbols.setDecimalSeparator(',');

        numFmt = new DecimalFormat();
        numFmt.setDecimalFormatSymbols(decSymbols);
    }


    /**
     *  Formata um objeto Date em String
     *
     * @return                     String formatada com o valor do objeto date
     * @param  date                Data a ser formatada
     * @param  mask                Mascara para formatacao
     * @exception  ParseException
     */
    public static String formatDate(Date date, String mask) throws ParseException {
        dateFmt.applyPattern(mask);

        if (date == null) {
            return "";
        } else {
            return dateFmt.format(date);
        }
    }


    /**
     *  Faz o parse de uma String para um objeto Timestamp
     *
     * @return                     Objeto Timestamp gerado a partir da String
     * @param  sdt                 String a ser parseada
     * @param  mask                Mascara para o parser
     * @exception  ParseException
     */
    public static java.sql.Timestamp parseTimestamp(String sdt, String mask) throws ParseException {
        Date dt = parseDate(sdt, mask);

        if (dt == null) {
            return null;
        }
        return new java.sql.Timestamp(dt.getTime());
    }


    /**
     *  Faz o parse de uma String para um objeto Date
     *
     * @return                     Objeto Date gerado a partir da String
     * @param  dt                  String a ser parseada
     * @param  mask                Mascara para o parser
     * @exception  ParseException
     */
    public static Date parseDate(String dt, String mask) throws ParseException {
        dateFmt.applyPattern(mask);

        if (dt == null || dt.trim().equals("")) {
            return null;
        } else {
            Date res = dateFmt.parse(dt);
            return res;
        }
    }


    /**
     *  Faz o parse de uma String para um objeto Long
     *
     * @return                     Objeto Long gerado a partir da String
     * @param  num                 String a ser parseada
     * @param  mask                Mascara para o parser
     * @exception  ParseException
     */
    public static Long parseLong(String num, String mask) throws ParseException {

        if (num == null || num.trim().equals("")) {
            return null;
        } else if (mask != null && !mask.trim().equals("")) {
            Number res;
            res = parseNumber(num, mask);
            
            if (res == null) {
                return null;
            } else {
                return new Long(res.longValue());
            }
        } else {
            return Long.valueOf(num.trim());
        }
    }

    /**
     *  Faz o parse de uma String para um objeto Integer
     *
     * @return                     Objeto Integer gerado a partir da String
     * @param  num                 String a ser parseada
     * @param  mask                Mascara para o parser
     * @exception  ParseException
     */
    public static Integer parseInt(String num, String mask) throws ParseException {

        if (num == null || num.trim().equals("")) {
            return null;
        } else if (mask != null && !mask.trim().equals("")) {
            Number res;
            res = parseNumber(num, mask);

            if (res == null) {
                return null;
            } else {
                return new Integer(res.intValue());
            }
        } else {
            return Integer.valueOf(num.trim());
        }
    }

    /**
     *  Faz o parse de uma String para um objeto Double
     *
     * @return                     Objeto Double gerado a partir da String
     * @param  num                 String a ser parseada
     * @param  mask                Mascara para o parser
     * @exception  ParseException
     */
    public static Double parseDouble(String num, String mask) throws ParseException {

        if (num == null || num.trim().equals("")) {
            return null;
        } else if (mask != null && !mask.trim().equals("")) {
            Number res;
            res = parseNumber(num, mask);

            if (res == null) {
                return null;
            } else {
                return new Double(res.doubleValue());
            }
        } else {
            return Double.valueOf(num.trim());
        }
    }

    /**
     *  Faz o parse de uma String para um objeto Number
     *
     * @return                     Objeto Number gerado a partir da String
     * @param  num                 String a ser parseada
     * @param  mask                Mascara para o parser
     * @exception  ParseException
     */
    public static Number parseNumber(String num, String mask) throws ParseException {
        if (num == null) {
            return null;
        } else {
            numFmt.applyPattern(mask);
            return numFmt.parse(num);
        }
    }

    /**
     *  Formata uma instancia da superclasse Number
     *
     * @return                     Instancia de Number com o valor da String passada
     * @param  num                 Number a ser formatado
     * @param  mask                Mascara para a formatacao
     * @exception  ParseException
     */
    public static String formatNumber(Number num, String mask) throws ParseException {

        if (num == null) {
            return "";
        } else if (mask != null) {
            numFmt.applyPattern(mask);
            return numFmt.format(num);
        } else {
            return num.toString();
        }
    }

    /**
     *  Retorna uma string ate o caracter passado como parametro
     */
    public static String substring(String str, String limite, int maxLength) {
        int length = str.indexOf(limite);
        length = (length > maxLength? maxLength : length);
        return str.substring(0, length);
    }

}
