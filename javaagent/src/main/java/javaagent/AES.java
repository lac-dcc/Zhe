package com.mscufmg.javaagent;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
 
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

class Base16 {

    private final static char[] HEX = new char[]{
        'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N',
        'O', 'P', 'A', 'B', 'C', 'D', 'E', 'F' };

    /**
     * Convert bytes to a base16 string.
     */
    public static String encode(byte[] byteArray) {
        StringBuffer hexBuffer = new StringBuffer(byteArray.length * 2);
        for (int i = 0; i < byteArray.length; i++)
            for (int j = 1; j >= 0; j--)
                hexBuffer.append(HEX[(byteArray[i] >> (j * 4)) & 0xF]);
        return hexBuffer.toString();
    }

    /**
     * Convert a base16 string into a byte array.
     */
    public static byte[] decode(String s) {
        int len = s.length();
        byte[] r = new byte[len / 2];
        for (int i = 0; i < r.length; i++) {
            int digit1 = s.charAt(i * 2), digit2 = s.charAt(i * 2 + 1);
            if (digit1 >= 'G' && digit1 <= 'P')
                digit1 -= 'G';
            else if (digit1 >= 'A' && digit1 <= 'F')
                digit1 -= 'A' - 10;
            if (digit2 >= 'G' && digit2 <= 'P')
                digit2 -= 'G';
            else if (digit2 >= 'A' && digit2 <= 'F')
                digit2 -= 'A' - 10;

            r[i] = (byte) ((digit1 << 4) + digit2);
        }
        return r;
    }
}

public class AES {
 
    private static SecretKeySpec secretKey;
    private static byte[] key;
 
    public static void setKey(String myKey)
    {
        MessageDigest sha = null;
        try {
            key = myKey.getBytes("UTF-8");
            sha = MessageDigest.getInstance("SHA-1");
            key = sha.digest(key);
            key = Arrays.copyOf(key, 16);
            secretKey = new SecretKeySpec(key, "AES");
        }
        catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
 
    public static String encrypt(String strToEncrypt, String secret)
    {
        try
        {
            setKey(secret);
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            return Base16.encode(cipher.doFinal(strToEncrypt.getBytes("UTF-8")));
        }
        catch (Exception e)
        {
            System.out.println("Error while encrypting: " + e.toString());
        }
        return null;
    }
 
    public static String decrypt(String strToDecrypt, String secret)
    {
        try
        {
            setKey(secret);
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            return new String(cipher.doFinal(Base16.decode(strToDecrypt)));
        }
        catch (Exception e)
        {
            System.out.println("Error while decrypting: " + e.toString());
        }
        return null;
    }
}
