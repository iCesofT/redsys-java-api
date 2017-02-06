package sis.redsys.api;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.Mac;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.DESedeKeySpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;

public class Utils {

    /** Numero de bytes para obtener cadenas multiplos de 8 */
    private static final short OCHO = 8;

    /** Constante de array de inicializaci�n */
    private static final byte[] IV = { 0, 0, 0, 0, 0, 0, 0, 0 };

    public static byte[] encrypt_3DES(final String claveHex, final String datos) throws InvalidKeyException,
            NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException,
            UnsupportedEncodingException, IllegalBlockSizeException, BadPaddingException {

        // Crea la clave
        DESedeKeySpec desKeySpec = new DESedeKeySpec(toByteArray(claveHex));
        SecretKey desKey = new SecretKeySpec(desKeySpec.getKey(), "DESede");
        // Crea un cifrador
        Cipher desCipher = Cipher.getInstance("DESede/CBC/NoPadding");

        // Inicializa el cifrador para encriptar
        desCipher.init(Cipher.ENCRYPT_MODE, desKey, new IvParameterSpec(IV));

        // Se añaden los 0 en bytes necesarios para que sea un múltiplo de 8
        int numeroCerosNecesarios = OCHO - datos.length() % OCHO;
        if (numeroCerosNecesarios == OCHO) {
            numeroCerosNecesarios = 0;
        }
        ByteArrayOutputStream array = new ByteArrayOutputStream();
        array.write(datos.getBytes("UTF-8"), 0, datos.length());
        for (int i = 0; i < numeroCerosNecesarios; i++) {
            array.write(0);
        }
        byte[] cleartext = array.toByteArray();
        // Encripta el texto
        return desCipher.doFinal(cleartext);
    }

    /**
     * Base64 y HEX Functions
     *
     * @throws UnsupportedEncodingException
     */
    public static String encodeB64String(final byte[] data) throws UnsupportedEncodingException {
        return new String(encodeB64(data), "UTF-8");
    }

    public static byte[] encodeB64(final byte[] data) {
        return Base64.getEncoder().encode(data);
    }

    public static byte[] encodeB64UrlSafe(final byte[] data) {
        byte[] encode = encodeB64(data);
        for (int i = 0; i < encode.length; i++) {
            if (encode[i] == '+') {
                encode[i] = '-';
            } else if (encode[i] == '/') {
                encode[i] = '_';
            }
        }
        return encode;
    }

    public static String decodeB64String(final byte[] data) throws UnsupportedEncodingException {
        return new String(decodeB64(data), "UTF-8");
    }

    public static byte[] decodeB64(final byte[] data) {
        return Base64.getDecoder().decode(data);
    }

    public static byte[] decodeB64UrlSafe(final byte[] data) {
        byte[] encode = Arrays.copyOf(data, data.length);
        for (int i = 0; i < encode.length; i++) {
            if (encode[i] == '-') {
                encode[i] = '+';
            } else if (encode[i] == '_') {
                encode[i] = '/';
            }
        }
        return decodeB64(encode);
    }

    public static String toHexadecimal(byte[] datos, int numBytes) {
        return DatatypeConverter.printBase64Binary(datos);
    }

    public static byte[] toByteArray(String cadena) {
        return DatatypeConverter.parseBase64Binary(cadena);
    }

    /**
     * MAC Function
     *
     * @throws UnsupportedEncodingException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     */
    public static byte[] mac256(final String dsMerchantParameters, final byte[] secretKo)
            throws UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeyException {
        Mac sha256HMAC = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKey = new SecretKeySpec(secretKo, "HmacSHA256");
        sha256HMAC.init(secretKey);
        return sha256HMAC.doFinal(dsMerchantParameters.getBytes("UTF-8"));
    }

    public static String encode(final String claveComercio, final String datos, final String params)
            throws UnsupportedEncodingException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException,
            InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
        return encode(claveComercio, datos, params, false);
    }

    public static String encode(final String claveComercio, final String datos, final String params,
            final Boolean urlSafe) throws UnsupportedEncodingException, InvalidKeyException, NoSuchAlgorithmException,
            NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
        byte[] clave = Utils.decodeB64(claveComercio.getBytes("UTF-8"));
        String secretKc = Utils.toHexadecimal(clave, clave.length);
        byte[] secretKo = Utils.encrypt_3DES(secretKc, datos);

        // Se hace el MAC con la clave de la operación "Ko" y se codifica en BASE64
        byte[] hash = Utils.mac256(params, secretKo);
        byte[] res = urlSafe ? Utils.encodeB64UrlSafe(hash) : Utils.encodeB64(hash);
        return new String(res, "UTF-8");
    }

}
