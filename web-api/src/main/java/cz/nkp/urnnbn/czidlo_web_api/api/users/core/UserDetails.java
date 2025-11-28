package cz.nkp.urnnbn.czidlo_web_api.api.users.core;

import cz.nkp.urnnbn.core.dto.Registrar;
import cz.nkp.urnnbn.czidlo_web_api.api.Utils;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@XmlRootElement(name = "userDetail")
@XmlAccessorType(XmlAccessType.FIELD)
public class UserDetails {
    private Long id;
    private String login;
    private String email;
    private boolean isAdmin;
    private Date created;
    private Date modified;
    private List<String> registrarsRights = new ArrayList<>();

    public static UserDetails fromUser(User user) {
        if (user == null) {
            return null;
        }

        UserDetails userDetails = new UserDetails();
        userDetails.setId(user.getId());
        userDetails.setCreated(user.getCreated());
        userDetails.setModified(user.getModified());
        userDetails.setLogin(user.getLogin());
        userDetails.setEmail(user.getEmail());
        userDetails.setAdmin(user.isAdmin());
        userDetails.setRegistrarRights(user.getRegistrarsRights());

        return userDetails;
    }

    public static UserDetails fromUserDto(cz.nkp.urnnbn.core.dto.User userDto, List<Registrar> dtoRegistrars) {
        if (userDto == null) {
            return null;
        }

        UserDetails userDetails = new UserDetails();
        userDetails.setId(userDto.getId());
        userDetails.setCreated(Utils.dateTimeToDate(userDto.getCreated()));
        userDetails.setModified(Utils.dateTimeToDate(userDto.getModified()));
        userDetails.setLogin(userDto.getLogin());
        userDetails.setEmail(userDto.getEmail());
        userDetails.setAdmin(userDto.isAdmin());
        List<String> registrarCodes = new ArrayList<>();
        for (Registrar registrar : dtoRegistrars) {
            registrarCodes.add(registrar.getCode().toString());
        }
        userDetails.setRegistrarRights(registrarCodes);
        return userDetails;
    }

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

    public List<String> getRegistrarRights() {
        return registrarsRights;
    }

    public void setRegistrarRights(List<String> registrarRights) {
        this.registrarsRights = registrarRights;
    }

    public void addRegistrarRight(String registrarCode) {
        this.registrarsRights.add(registrarCode);
    }

    public void removeRegistrarRight(String registrarCode) {
        this.registrarsRights.remove(registrarCode);
    }
}