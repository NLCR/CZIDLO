package cz.nkp.urnnbn.czidlo_web_api.api.archivers.core;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

import java.util.List;

@XmlRootElement(name = "archivers")
public class ArchiverList {
    @XmlElement(name = "archiver")
    public List<Archiver> items;

    public ArchiverList() {
    }

    public ArchiverList(List<Archiver> items) {
        this.items = items;
    }
}
