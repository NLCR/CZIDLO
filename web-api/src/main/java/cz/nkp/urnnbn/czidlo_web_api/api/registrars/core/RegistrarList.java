package cz.nkp.urnnbn.czidlo_web_api.api.registrars.core;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

import java.util.List;

@XmlRootElement(name = "registrar")
public class RegistrarList {
    @XmlElement(name = "registrar")
    public List<Registrar> items;

    public RegistrarList() {
    }

    public RegistrarList(List<Registrar> items) {
        this.items = items;
    }
}