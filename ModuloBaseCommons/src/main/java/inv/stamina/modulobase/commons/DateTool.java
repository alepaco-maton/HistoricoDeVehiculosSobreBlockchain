/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package inv.stamina.modulobase.commons;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 *
 * @author alepaco.com
 */
public class DateTool {

    public static Date parsearDate(String date, String formato) {
        SimpleDateFormat sdf = new SimpleDateFormat(formato);
        try {
            return sdf.parse(date);
        } catch (Exception ex) {
            return null;
        }
    }

    public static Date parsearDateWithException(String date, String formato) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat(formato);
        return sdf.parse(date);
    }

    public static String format(Date date, String formato) {
        SimpleDateFormat sdf = new SimpleDateFormat(formato);
        try {
            return sdf.format(date);
        } catch (Exception ex) {
            return "";
        }
    }

    public static Date minDate(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        return cal.getTime();
    }

    public static Date maxDate(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        return cal.getTime();
    }

    public static Date addAnos(Date date, int anos) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.YEAR, anos);
        return cal.getTime();
    }

    public static Date addDias(Date date, int dias) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DAY_OF_MONTH, dias);
        return cal.getTime();
    }

    public static Date addMinutos(Date date, int minutos) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.MINUTE, minutos);
        return cal.getTime();
    }

    public static Date addHoras(Date date, int horas) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.HOUR_OF_DAY, horas);
        return cal.getTime();
    }

    public static int obtenerGestion() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        return cal.get(Calendar.YEAR);
    }

    public static String convertirMesGregoriano(int mes) {
        if (mes == 0) {
            return "Enero";
        }
        if (mes == 1) {
            return "Febrero";
        }
        if (mes == 2) {
            return "Marzo";
        }
        if (mes == 3) {
            return "Abril";
        }
        if (mes == 4) {
            return "Mayo";
        }
        if (mes == 5) {
            return "Junio";
        }
        if (mes == 6) {
            return "Julio";
        }
        if (mes == 7) {
            return "Agosto";
        }
        if (mes == 8) {
            return "Septiembre";
        }
        if (mes == 9) {
            return "Octubre";
        }
        if (mes == 10) {
            return "Noviembre";
        }
        if (mes == 11) {
            return "Diciembre";
        }
        return "Invalido " + mes;
    }

    public static String formatoFechaReporte(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return (cal.get(Calendar.DAY_OF_MONTH) + " de "
                + convertirMesGregoriano(cal.get(Calendar.MONTH)) + " del "
                + cal.get(Calendar.YEAR) + " a las "
                + cal.get(Calendar.HOUR_OF_DAY) + ":" + cal.get(Calendar.MINUTE));
    }

}
