/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package inv.stamina.modulobase.commons;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.mail.MessagingException;

/**
 *
 * @author alepaco.com
 */
public class SymmetricEncoder {

    private static final String SECRET_KEY = "alepaco.maton 2021";
    private static final String SALT = "El exito solo se alcanza con esfuerzo";

    public static void main(String[] args) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, InvalidKeySpecException, MessagingException {
        String plainText = "aaaaaa";

//        // primera llave
//        String password = "baeldung";
//        String salt = "12345678";
        // esta es la segunda llave
        byte[] iv = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);

        //con esto generå®årå un codigo seguro ramdomicamente
        //IvParameterSpec ivParameterSpec = AESUtil.generateIv();
        SecretKey key = AESUtil.getKeyFromPassword(SECRET_KEY, SALT); //password salt

        String cipherText = AESUtil.encryptPasswordBased(plainText, key, ivParameterSpec);
        System.out.println(cipherText);
        String decryptedCipherText = AESUtil.decryptPasswordBased(
                cipherText, key, ivParameterSpec);
        System.out.println(decryptedCipherText);
        System.out.println(AESUtil.decryptPasswordBased(
                "2CSa7cRkND2juK7ddddmzeguDtSXwyNls9vEkPA0=", key, ivParameterSpec));
         
    }

    public static String AESEncode(String secretKey, String text) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, InvalidKeySpecException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        byte[] iv = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        IvParameterSpec ivParameterSpec = new IvParameterSpec(iv); 
        SecretKey key = AESUtil.getKeyFromPassword(secretKey, SALT); //password salt

        return AESUtil.encryptPasswordBased(text, key, ivParameterSpec);
    }

    public static String AESDecode(String secretKey, String cipherText) throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, InvalidAlgorithmParameterException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        byte[] iv = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        IvParameterSpec ivParameterSpec = new IvParameterSpec(iv); 
        SecretKey key = AESUtil.getKeyFromPassword(secretKey, SALT); //password salt
 
        return AESUtil.decryptPasswordBased(
                cipherText, key, ivParameterSpec); 
    }

}
