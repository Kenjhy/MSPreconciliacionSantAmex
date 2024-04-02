
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
    "cancelaValZero",
    "cancelaValUno",
    "cancelaValDos",
    "cancelaValTres",
    "cancelaValCuatro",
    "cancelaValCinco"
})
@XmlRootElement(name = "cancelacionesCargosAutomaticos")
public class CancelacionesCargosAutomaticos {

    @XmlElement(name="in0",required = true, nillable = true)
    protected String cancelaValZero;
    @XmlElement(name="in1",required = true, nillable = true)
    protected String cancelaValUno;
    @XmlElement(name="in2",required = true, nillable = true)
    protected String cancelaValDos;
    @XmlElement(name="in3",required = true, nillable = true)
    protected String cancelaValTres;
    @XmlElement(name="in4",required = true, nillable = true)
    protected String cancelaValCuatro;
    @XmlElement(name="in5",required = true, nillable = true)
    protected String cancelaValCinco;
	public String getCancelaValZero() {
		return cancelaValZero;
	}
	public void setCancelaValZero(String cancelaValZero) {
		this.cancelaValZero = cancelaValZero;
	}
	public String getCancelaValUno() {
		return cancelaValUno;
	}
	public void setCancelaValUno(String cancelaValUno) {
		this.cancelaValUno = cancelaValUno;
	}
	public String getCancelaValDos() {
		return cancelaValDos;
	}
	public void setCancelaValDos(String cancelaValDos) {
		this.cancelaValDos = cancelaValDos;
	}
	public String getCancelaValTres() {
		return cancelaValTres;
	}
	public void setCancelaValTres(String cancelaValTres) {
		this.cancelaValTres = cancelaValTres;
	}
	public String getCancelaValCuatro() {
		return cancelaValCuatro;
	}
	public void setCancelaValCuatro(String cancelaValCuatro) {
		this.cancelaValCuatro = cancelaValCuatro;
	}
	public String getCancelaValCinco() {
		return cancelaValCinco;
	}
	public void setCancelaValCinco(String cancelaValCinco) {
		this.cancelaValCinco = cancelaValCinco;
	}

 
}
