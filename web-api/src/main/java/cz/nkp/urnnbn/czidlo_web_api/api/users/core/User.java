package cz.nkp.urnnbn.czidlo_web_api.api.users.core;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@XmlRootElement(name = "user")
@XmlAccessorType(XmlAccessType.FIELD)
public class User {
    private Long id;
    private String login;
    private String email;
    private String password;
    private boolean isAdmin;
    private Date created;
    private Date modified;
    private List<String> registrarsRights = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Date getModified() {
        return modified;
    }

    public void setModified(Date modified) {
        this.modified = modified;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public List<String> getRegistrarsRights() {
        return registrarsRights;
    }

    public void setRegistrarsRights(List<String> registrarsRights) {
        this.registrarsRights = registrarsRights;
    }

    public void addRegistrarRight(String registrarCode) {
        this.registrarsRights.add(registrarCode);
    }

    public void removeRegistrarRight(String registrarCode) {
        this.registrarsRights.remove(registrarCode);
    }
}
