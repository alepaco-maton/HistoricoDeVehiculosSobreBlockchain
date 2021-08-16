package inv.stamina.modulobase.commons;

import java.io.Serializable;
import java.util.regex.Pattern;

public class Tools implements Serializable {

    private static final long serialVersionUID = 1L;

    //******LEFT TRIM inicio
    private final static Pattern LTRIM = Pattern.compile("^\\s+");

    public static String ltrim(String s) {
        return LTRIM.matcher(s).replaceAll("");
    }

    /**
     *
     * \t Insert a tab in the text at this point. \b Insert a backspace in the
     * text at this point. \n Insert a newline in the text at this point. \r
     * Insert a carriage return in the text at this point. \f Insert a formfeed
     * in the text at this point. \' Insert a single quote character in the text
     * at this point. \" Insert a double quote character in the text at this
     * point. \\ Insert a backslash character in the text at this point.
     *
     * @return
     */
    public static Character escapeCaracterEspecial(String texto) {
        if (texto.equals("\\t")) {
            return '\t';
        }
        if (texto.equals("\\b")) {
            return '\b';
        }
        if (texto.equals("\\n")) {
            return '\n';
        }
        if (texto.equals("\\r")) {
            return '\r';
        }
        if (texto.equals("\\f")) {
            return '\f';
        }
        if (texto.equals("\\'")) {
            return '\'';
        }
        if (texto.equals("\\\"")) {
            return '\"';
        }
        if (texto.equals("\\\\")) {
            return '\\';
        }

        return null;
    }

    public static int contadorSeparadores(String data, String separador) {
        int cont = 0;

        Character c = escapeCaracterEspecial(separador);
        
        if (c == null) {
            return -1;
        }
        
        for (int i = 0; i < data.length(); i++) {
            if (data.charAt(i) == c) {
                cont++;
            }
        }

        return cont;
    }

    //*****LEFT TRIM fin

    /* public static double round(double value, int decimalDigits) {
        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(decimalDigits, BigDecimal.ROUND_HALF_UP);
        return bd.doubleValue();
    }

    public static BigDecimal round(BigDecimal value, int decimalDigits) {
        value = value.setScale(decimalDigits, BigDecimal.ROUND_HALF_UP);
        return value;
    }

    public static String dateToString(Date date, String formato) {
        if (formato == null || formato.isEmpty()) {
            formato = "dd/MM/yyyy";
        }
        return date == null ? "" : new SimpleDateFormat(formato).format(date);
    }

    public static Date stringToDate(String date, String formato) throws ParseException {
        if (formato == null || formato.isEmpty()) {
            formato = "dd/MM/yyyy";
        }
        return date == null ? null : new SimpleDateFormat(formato).parse(date);
    }

    public static double stringToDouble(String valorString) throws Exception {
        DecimalFormat format = (DecimalFormat) DecimalFormat.getInstance();
        DecimalFormatSymbols symbols = format.getDecimalFormatSymbols();
        char sep = symbols.getDecimalSeparator();
        valorString = valorString.replaceAll(",", "");
        if (sep == ',') {
            valorString = valorString.replaceAll("\\.", sep + "");
        }
        return format.parse(valorString).doubleValue();
    }

    public static int diferenciaDias(Date fechaIni, Date fechaFin) {
        Calendar ci = Calendar.getInstance();
        ci.setTime(fechaIni);

        Calendar cf = Calendar.getInstance();
        cf.setTime(fechaFin);

        long diferencia = cf.getTimeInMillis() - ci.getTimeInMillis();

        return (int) Math.ceil((double) diferencia / (1000 * 60 * 60 * 24));
    }

    public static String getEHumanoActualUser() {
        HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        Object ehObject = request.getSession().getAttribute("TEMP$EH");
        if (ehObject == null) {
            return "";
        } else {
            return (String) ehObject;
        }
    }

    public static String getCIActualUser() {
        HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        Object ciObject = request.getSession().getAttribute("TEMP$USER_NAME");
        if (ciObject == null) {
            return "";
        } else {
            return (String) ciObject;
        }
    }

    public static String getLDAPActualUser() {
        HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        Object ciObject = request.getSession().getAttribute("TEMP$USER_NAME");
        if (ciObject == null) {
            return "";
        } else {
            return (String) ciObject;
        }
    }

    public Date getSoloFecha(Date date) throws ParseException {

        DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        Date todayWithZeroTime = null;
        todayWithZeroTime = formatter.parse(formatter.format(date));
        return todayWithZeroTime;
    }

    public static Date getRestarAnosFechaActual(int cantidadAnos) {
        Calendar a = Calendar.getInstance();
        a.setTime(new Date());
        int year = a.get(Calendar.YEAR);
        a.set(Calendar.YEAR, year - cantidadAnos);
        a.set(Calendar.MONTH, 0);
        a.set(Calendar.DATE, 1);

        return a.getTime();
    }

    public static BigDecimal setScale(BigDecimal num) {
        num = num.setScale(2, BigDecimal.ROUND_HALF_UP);
        return num;
    }

    public static boolean esCiudad(String ciudadNombre) {
        Set<String> VALUES = new HashSet<String>(Arrays.asList(
                new String[]{"LA PAZ", "SANTA  CRUZ", "SANTA CRUZ", "COCHABAMBA", "PANDO", "BENI", "CHUQUISACA", "ORURO", "POTOSI", "TARIJA"}
        ));
        if (VALUES.contains(ciudadNombre.toUpperCase())) {
            return true;
        }
        return false;
    }

    public static void retardarEnMilisegundos(long milisegundosRetardo) {
        try {
            log.info("Iniciando delay : "
                    + milisegundosRetardo
                    + " milisegundos");
            Thread.sleep(milisegundosRetardo); // En milliseconds
            log.info("Fin delay.");
        } catch (InterruptedException ex) {
            log.error("Excepcion al momento de ejecutar Thread.sleep: " + ex.getMessage(), ex);
            Thread.currentThread().interrupt();
        }
    }

    public static String getIpClientre(HttpServletRequest request) {
        String ip = request.getHeader("X-FORWARDED-FOR");
        if (ip != null && ip.length() > 0) {
            return ip.split(",")[0];
        }
        return request.getRemoteAddr();
    }

    public static void main(String[] arg) {
        AlgoritmoAES aes = new AlgoritmoAES();
        System.out.println(aes.encriptar("El campo %CAMPO% ha superado la longitud máxima de %LONGITUD% caracteres"));
    }*/
}
