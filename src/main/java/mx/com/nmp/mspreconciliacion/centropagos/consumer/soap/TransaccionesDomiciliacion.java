
package mx.com.nmp.mspreconciliacion.centropagos.consumer.soap;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para anonymous complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="in0" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="in1" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="in2" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="in3" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="in4" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="in5" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "domicilioValZero",
    "domicilioValUno",
    "domicilioValDos",
    "domicilioValTres",
    "domicilioValCuatro",
    "domicilioValCinco"
})
@XmlRootElement(name = "transaccionesDomiciliacion")
public class TransaccionesDomiciliacion {

    @XmlElement(name="in0",required = true, nillable = true)
    protected String domicilioValZero;
    @XmlElement(name="in1",required = true, nillable = true)
    protected String domicilioValUno;
    @XmlElement(name="in2",required = true, nillable = true)
    protected String domicilioValDos;
    @XmlElement(name="in3",required = true, nillable = true)
    protected String domicilioValTres;
    @XmlElement(name="in4",required = true, nillable = true)
    protected String domicilioValCuatro;
    @XmlElement(name="in5",required = true, nillable = true)
    protected String domicilioValCinco;
	public String getDomicilioValZero() {
		return domicilioValZero;
	}
	public void setDomicilioValZero(String domicilioValZero) {
		this.domicilioValZero = domicilioValZero;
	}
	public String getDomicilioValUno() {
		return domicilioValUno;
	}
	public void setDomicilioValUno(String domicilioValUno) {
		this.domicilioValUno = domicilioValUno;
	}
	public String getDomicilioValDos() {
		return domicilioValDos;
	}
	public void setDomicilioValDos(String domicilioValDos) {
		this.domicilioValDos = domicilioValDos;
	}
	public String getDomicilioValTres() {
		return domicilioValTres;
	}
	public void setDomicilioValTres(String domicilioValTres) {
		this.domicilioValTres = domicilioValTres;
	}
	public String getDomicilioValCuatro() {
		return domicilioValCuatro;
	}
	public void setDomicilioValCuatro(String domicilioValCuatro) {
		this.domicilioValCuatro = domicilioValCuatro;
	}
	public String getDomicilioValCinco() {
		return domicilioValCinco;
	}
	public void setDomicilioValCinco(String domicilioValCinco) {
		this.domicilioValCinco = domicilioValCinco;
	}

 
}
