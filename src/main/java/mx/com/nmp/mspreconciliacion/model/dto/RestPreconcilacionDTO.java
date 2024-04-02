package mx.com.nmp.mspreconciliacion.model.dto;

import org.springframework.retry.RetryContext;

public class RestPreconcilacionDTO implements RetryContext {

    private String code;
    private String message;
    private Boolean object;

    /**
     * Constructor de la clase
     */
    public RestPreconcilacionDTO() {
        super();
    }

    /**
     * Constructor de la clase
     *
     * @param code Codigo de respuesta http
     * @param message Mensaje de la respuesta
     * @param object Cuerpo de la respuesta
     */
    public RestPreconcilacionDTO(String code, String message, Boolean object) {
        super();

        this.code = code;
        this.message = message;
        this.object = object;
    }

    public Boolean getObject() {
        return object;
    }

    public void setObject(Boolean object) {
        this.object = object;
    }
    
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
    
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

	@Override
	public void setAttribute(String name, Object value) {
		this.setAttribute(name, value);
	}

	@Override
	public Object getAttribute(String name) {
		return this.getAttribute(name);
	}

	@Override
	public Object removeAttribute(String name) {
		return this.removeAttribute(name);
	}

	@Override
	public boolean hasAttribute(String name) {
		return this.hasAttribute(name);
	}

	@Override
	public String[] attributeNames() {
		return this.attributeNames();
	}

	@Override
	public void setExhaustedOnly() {
		this.setExhaustedOnly();
	}

	@Override
	public boolean isExhaustedOnly() {
		return this.isExhaustedOnly();
	}

	@Override
	public RetryContext getParent() {
		return this.getParent();
	}

	@Override
	public int getRetryCount() {
		return 0;
	}

	@Override
	public Throwable getLastThrowable() {
		return this.getLastThrowable();
	}    
}
