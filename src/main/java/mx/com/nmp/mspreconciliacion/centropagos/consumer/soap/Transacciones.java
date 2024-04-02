
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
    "datoFijo",
    "requestEncriptado",
    "posicionDos",
    "posicionTres",
    "posicionCuatro",
    "posicionCinco"
})
@XmlRootElement(name = "transacciones")
public class Transacciones {

    @XmlElement(name="in0", required = true, nillable = true)
    protected String datoFijo;
    @XmlElement(name="in1", required = true, nillable = true)
    protected String requestEncriptado;
    @XmlElement(name="in2", required = true, nillable = true)
    protected String posicionDos;
    @XmlElement(name="in3", required = true, nillable = true)
    protected String posicionTres;
    @XmlElement(name="in4", required = true, nillable = true)
    protected String posicionCuatro;
    @XmlElement(name="in5", required = true, nillable = true)
    protected String posicionCinco;
	public String getDatoFijo() {
		return datoFijo;
	}
	public void setDatoFijo(String datoFijo) {
		this.datoFijo = datoFijo;
	}
	public String getRequestEncriptado() {
		return requestEncriptado;
	}
	public void setRequestEncriptado(String requestEncriptado) {
		this.requestEncriptado = requestEncriptado;
	}
	public String getPosicionDos() {
		return posicionDos;
	}
	public void setPosicionDos(String posicionDos) {
		this.posicionDos = posicionDos;
	}
	public String getPosicionTres() {
		return posicionTres;
	}
	public void setPosicionTres(String posicionTres) {
		this.posicionTres = posicionTres;
	}
	public String getPosicionCuatro() {
		return posicionCuatro;
	}
	public void setPosicionCuatro(String posicionCuatro) {
		this.posicionCuatro = posicionCuatro;
	}
	public String getPosicionCinco() {
		return posicionCinco;
	}
	public void setPosicionCinco(String posicionCinco) {
		this.posicionCinco = posicionCinco;
	}

 

}
