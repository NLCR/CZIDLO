package cz.nkp.urnnbn.czidlo_web_api.api.processes.core;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

import java.util.List;

@XmlRootElement(name = "processes")
public class ProcessList {
    @XmlElement(name = "process")
    public List<cz.nkp.urnnbn.czidlo_web_api.api.processes.core.Process> items;

    public ProcessList() {
    }

    public ProcessList(List<Process> items) {
        this.items = items;
    }
}
