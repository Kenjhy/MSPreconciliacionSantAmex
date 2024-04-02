
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
    "transaccValorZero",
    "transaccValorUno",
    "transaccValorDos",
    "transaccValorTres",
    "transaccValorCuatro",
    "transaccValorCinco"
})
@XmlRootElement(name = "transaccionesCargosAutomaticos")
public class TransaccionesCargosAutomaticos {

    @XmlElement(name="in0",required = true, nillable = true)
    protected String transaccValorZero;
    @XmlElement(name="in2",required = true, nillable = true)
    protected String transaccValorUno;
    @XmlElement(name="in3",required = true, nillable = true)
    protected String transaccValorDos;
    @XmlElement(name="in4",required = true, nillable = true)
    protected String transaccValorTres;
    @XmlElement(name="in5",required = true, nillable = true)
    protected String transaccValorCuatro;
    @XmlElement(name="in6",required = true, nillable = true)
    protected String transaccValorCinco;
	public String getTransaccValorZero() {
		return transaccValorZero;
	}
	public void setTransaccValorZero(String transaccValorZero) {
		this.transaccValorZero = transaccValorZero;
	}
	public String getTransaccValorUno() {
		return transaccValorUno;
	}
	public void setTransaccValorUno(String transaccValorUno) {
		this.transaccValorUno = transaccValorUno;
	}
	public String getTransaccValorDos() {
		return transaccValorDos;
	}
	public void setTransaccValorDos(String transaccValorDos) {
		this.transaccValorDos = transaccValorDos;
	}
	public String getTransaccValorTres() {
		return transaccValorTres;
	}
	public void setTransaccValorTres(String transaccValorTres) {
		this.transaccValorTres = transaccValorTres;
	}
	public String getTransaccValorCuatro() {
		return transaccValorCuatro;
	}
	public void setTransaccValorCuatro(String transaccValorCuatro) {
		this.transaccValorCuatro = transaccValorCuatro;
	}
	public String getTransaccValorCinco() {
		return transaccValorCinco;
	}
	public void setTransaccValorCinco(String transaccValorCinco) {
		this.transaccValorCinco = transaccValorCinco;
	}

  

}
