package cz.nkp.urnnbn.api.v4.jaxb;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class RegistrarBean {
    public String code;
    public String name;

    public RegistrarBean() {
    }

    public RegistrarBean(String code, String name) {
        this.code = code;
        this.name = name;
    }

}
