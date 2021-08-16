/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package inv.stamina.modulobase.commons;

import java.io.File;
import java.util.Date; 

/**
 *
 * @author alepaco.maton
 */ 
public class FileTool {

    public static File nameFile(String direccionCarpetDestido, File file) throws Exception {
        File ficheroDestino = new File(direccionCarpetDestido + file.getName());

        if (ficheroDestino.exists()) {
            String extencion = getExtention(ficheroDestino.getName());
            ficheroDestino = new File(direccionCarpetDestido
                    + killExtention(ficheroDestino.getName())
                    + "_" + (new Date().getTime()) + extencion);
        }

        file.renameTo(ficheroDestino);

//        Files.copy(Paths.get(archivo.getAbsolutePath()), Paths.get(ficheroDestino.getAbsolutePath()), StandardCopyOption.ATOMIC_MOVE);
//        if (archivo.exists()) {
//            archivo.delete();
//        }
        return ficheroDestino;
    }

    public static String killExtention(String file) {
        final int size = file.length();

        boolean flag = false;

        StringBuilder temp = new StringBuilder();

        for (int i = size - 1; i >= 0; i--) {
            if (flag) {
                temp.insert(0, file.charAt(i));

            } else if (file.charAt(i) == '.') {
                flag = true;
            }
        }

        return temp.toString();
    }

    public static String getExtention(String file) {
        final int size = file.length();

        StringBuilder extencion = new StringBuilder();

        for (int i = size - 1; i >= 0; i--) {
            extencion.insert(0, file.charAt(i));
            if (file.charAt(i) == '.') {
                break;
            }
        }

        return extencion.toString();
    }

}
