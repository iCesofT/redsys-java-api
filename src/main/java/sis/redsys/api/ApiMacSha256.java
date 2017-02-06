package sis.redsys.api;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import org.json.JSONObject;

public class ApiMacSha256 {

    /** Array de DatosEntrada */
    private JSONObject jsonObj = new JSONObject();

    /** Set parameter */
    public void setParameter(final String key, final String value) {
        jsonObj.put(key, value);
    }

    /** Get parameter */
    public String getParameter(final String key) {
        return jsonObj.has(key) ? jsonObj.getString(key) : null;
    }

    //////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////
    //////////// FUNCIONES PARA LA GENERACIÓN DEL FORMULARIO DE PAGO: ////////////
    //////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////
    public String getOrder() {
        if (getParameter("DS_MERCHANT_ORDER") == null || getParameter("DS_MERCHANT_ORDER").equals("")) return getParameter("Ds_Merchant_Order");
        else
            return getParameter("DS_MERCHANT_ORDER");
    }

    public String createMerchantParameters() throws UnsupportedEncodingException {
        String jsonString = jsonObj.toString();
        return Utils.encodeB64String(jsonString.getBytes("UTF-8"));
    }

    public String createMerchantSignature(final String claveComercio)
            throws UnsupportedEncodingException, InvalidKeyException, NoSuchAlgorithmException, IllegalStateException,
            NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
        return Utils.encode(claveComercio, getOrder(), createMerchantParameters());
    }

    //////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////
    //////////// FUNCIONES PARA LA RECEPCIÓN DE DATOS DE PAGO (Notif, URLOK y URLKO): ////////////
    //////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////

    public String getOrderNotif() {
        if (getParameter("Ds_Order") == null || getParameter("Ds_Order").equals("")) return getParameter("DS_ORDER");
        else
            return getParameter("Ds_Order");
    }

    public String getOrderNotifSOAP(final String datos) {
        int posPedidoIni = datos.indexOf("<Ds_Order>");
        int tamPedidoIni = "<Ds_Order>".length();
        int posPedidoFin = datos.indexOf("</Ds_Order>");
        return datos.substring(posPedidoIni + tamPedidoIni, posPedidoFin);
    }

    public String getRequestNotifSOAP(final String datos) {
        int posReqIni = datos.indexOf("<Request");
        int posReqFin = datos.indexOf("</Request>");
        int tamReqFin = "</Request>".length();
        return datos.substring(posReqIni, posReqFin + tamReqFin);
    }

    public String getResponseNotifSOAP(final String datos) {
        int posResIni = datos.indexOf("<Response");
        int posResFin = datos.indexOf("</Response>");
        int tamResFin = "</Response>".length();
        return datos.substring(posResIni, posResFin + tamResFin);
    }

    public String decodeMerchantParameters(final String datos) throws UnsupportedEncodingException {
        byte[] res = Utils.decodeB64UrlSafe(datos.getBytes("UTF-8"));
        String params = new String(res, "UTF-8");
        jsonObj = new JSONObject(params);
        return params;
    }

    public String createMerchantSignatureNotif(final String claveComercio, final String merchantParams)
            throws UnsupportedEncodingException, InvalidKeyException, NoSuchAlgorithmException, IllegalStateException,
            NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
        return Utils.encode(claveComercio, getOrderNotif(), merchantParams, true);
    }

    /******
     * Notificaciones SOAP ENTRADA *****
     *
     * @throws UnsupportedEncodingException
     * @throws IllegalStateException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     * @throws BadPaddingException
     * @throws IllegalBlockSizeException
     * @throws InvalidAlgorithmParameterException
     * @throws NoSuchPaddingException
     */
    public String createMerchantSignatureNotifSOAPRequest(final String claveComercio, final String request)
            throws InvalidKeyException, UnsupportedEncodingException, NoSuchAlgorithmException, NoSuchPaddingException,
            InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
        return Utils.encode(claveComercio, getOrderNotifSOAP(request), getRequestNotifSOAP(request));
    }

    /******
     * Notificaciones SOAP SALIDA *****
     *
     * @throws UnsupportedEncodingException
     * @throws IllegalStateException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     * @throws BadPaddingException
     * @throws IllegalBlockSizeException
     * @throws InvalidAlgorithmParameterException
     * @throws NoSuchPaddingException
     */
    public String createMerchantSignatureNotifSOAPResponse(final String claveComercio, final String response,
            final String numPedido) throws InvalidKeyException, UnsupportedEncodingException, NoSuchAlgorithmException,
            NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
        return Utils.encode(claveComercio, numPedido, getResponseNotifSOAP(response));
    }

}
