package sis.redsys.api;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class ApiWsMacSha256 {

    private static final String DS_MERCHANT_ORDER_START_TAG = "<DS_MERCHANT_ORDER>";
    private static final String DS_MERCHANT_ORDER_END_TAG = "</DS_MERCHANT_ORDER>";
    private static final Integer DS_MERCHANT_ORDER_START_TAG_LENGTH = DS_MERCHANT_ORDER_START_TAG.length();

    //////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////
    //////////// FUNCIONES PARA LA GENERACIÓN DE LA PETICIÓN DE PAGO: ////////////
    //////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////
    public String getOrder(final String datos) {
        int posPedidoIni = datos.indexOf(DS_MERCHANT_ORDER_START_TAG);
        int posPedidoFin = datos.indexOf(DS_MERCHANT_ORDER_END_TAG);
        return datos.substring(posPedidoIni + DS_MERCHANT_ORDER_START_TAG_LENGTH, posPedidoFin);
    }

    public String createMerchantSignatureHostToHost(final String claveComercio, final String datosEntrada)
            throws UnsupportedEncodingException, InvalidKeyException, IllegalStateException, NoSuchAlgorithmException,
            IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, NoSuchPaddingException {

        return Utils.encode(claveComercio, getOrder(datosEntrada), datosEntrada);
    }

    //////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////
    //////////// FUNCIONES PARA LA RECEPCI�N DE DATOS DE PAGO (Respuesta HOST to HOST): //////////
    //////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////

    public String createSignatureResponseHostToHost(final String claveComercio, final String datosEntrada,
            final String numPedido)
                    throws UnsupportedEncodingException, InvalidKeyException, IllegalStateException, NoSuchAlgorithmException,
                    IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, NoSuchPaddingException {

        return Utils.encode(claveComercio, numPedido, datosEntrada);
    }

}
