
package mx.com.nmp.mspreconciliacion.centropagos.consumer.soap;

import java.net.MalformedURLException;
import java.net.URL;
import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import javax.xml.ws.WebEndpoint;
import javax.xml.ws.WebServiceClient;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.WebServiceFeature;


/**
 * This class was generated by the JAX-WS RI.
 * JAX-WS RI 2.3.2
 * Generated source version: 2.2
 * 
 */
@WebServiceClient(name = "xmltransacciones", targetNamespace = "http://wstrans.cpagos", wsdlLocation = "https://vip.e-pago.com.mx/pgs/services/xmltransacciones?wsdl")
public class Xmltransacciones
    extends Service
{

	private static final String URLPAGOS= "http://wstrans.cpagos";
    private static final  URL XMLTRANSACCIONES_WSDL_LOCATION;
    private static final  WebServiceException XMLTRANSACCIONES_EXCEPTION;
    private static final  QName XMLTRANSACCIONES_QNAME = new QName(URLPAGOS, "xmltransacciones");

    static {
        URL url = null;
        WebServiceException e = null;
        try {
            url = new URL("https://vip.e-pago.com.mx/pgs/services/xmltransacciones?wsdl");
        } catch (MalformedURLException ex) {
            e = new WebServiceException(ex);
        }
        XMLTRANSACCIONES_WSDL_LOCATION = url;
        XMLTRANSACCIONES_EXCEPTION = e;
    }

    public Xmltransacciones() {
        super(getWsdlLocation(), XMLTRANSACCIONES_QNAME);
    }

    public Xmltransacciones(WebServiceFeature... features) {
        super(getWsdlLocation(), XMLTRANSACCIONES_QNAME, features);
    }

    public Xmltransacciones(URL wsdlLocation) {
        super(wsdlLocation, XMLTRANSACCIONES_QNAME);
    }

    public Xmltransacciones(URL wsdlLocation, WebServiceFeature... features) {
        super(wsdlLocation, XMLTRANSACCIONES_QNAME, features);
    }

    public Xmltransacciones(URL wsdlLocation, QName serviceName) {
        super(wsdlLocation, serviceName);
    }

    public Xmltransacciones(URL wsdlLocation, QName serviceName, WebServiceFeature... features) {
        super(wsdlLocation, serviceName, features);
    }

    /**
     * 
     * @return
     *     returns XmltransaccionesPortType
     */
    @WebEndpoint(name = "xmltransaccionesHttpPort")
    public XmltransaccionesPortType getXmltransaccionesHttpPort() {
        return super.getPort(new QName(URLPAGOS, "xmltransaccionesHttpPort"), XmltransaccionesPortType.class);
    }

    /**
     * 
     * @param features
     *     A list of {@link javax.xml.ws.WebServiceFeature} to configure on the proxy.  Supported features not in the <code>features</code> parameter will have their default values.
     * @return
     *     returns XmltransaccionesPortType
     */
    @WebEndpoint(name = "xmltransaccionesHttpPort")
    public XmltransaccionesPortType getXmltransaccionesHttpPort(WebServiceFeature... features) {
        return super.getPort(new QName(URLPAGOS, "xmltransaccionesHttpPort"), XmltransaccionesPortType.class, features);
    }

    private static URL getWsdlLocation() {
        if (XMLTRANSACCIONES_EXCEPTION!= null) {
            throw XMLTRANSACCIONES_EXCEPTION;
        }
        return XMLTRANSACCIONES_WSDL_LOCATION;
    }

}
