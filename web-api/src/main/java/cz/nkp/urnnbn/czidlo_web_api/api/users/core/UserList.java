package cz.nkp.urnnbn.czidlo_web_api.api.users.core;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

import java.util.List;

@XmlRootElement(name = "users")
public class UserList {
    @XmlElement(name = "user")
    public List<UserDetails> items;

    public UserList() {
    }

    public UserList(List<UserDetails> items) {
        this.items = items;
    }
}
