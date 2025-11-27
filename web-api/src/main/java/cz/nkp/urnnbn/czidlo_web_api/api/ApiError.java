package cz.nkp.urnnbn.czidlo_web_api.api;

import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "error")
public class ApiError {
    public String message;

    public ApiError() {
    }

    public ApiError(String message) {
        this.message = message;
    }
}
