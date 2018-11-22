package cz.nkp.urnnbn.shared.dto;

import java.io.Serializable;

public class UserDTO implements Serializable {

    private static final long serialVersionUID = 6798729665351325224L;

    public enum ROLE {
        SUPER_ADMIN, ADMIN, USER;
    }

    private Long id;
    private String login;
    private String email;
    private String password;
    private ROLE role;
    private String created;
    private String modified;


    public UserDTO() {
    }

    public UserDTO(UserDTO copyFrom) {
        this.id = copyFrom.id;
        this.login = copyFrom.login;
        this.email = copyFrom.email;
        this.password = copyFrom.password;
        this.role = copyFrom.role;
        this.created = copyFrom.created;
        this.modified = copyFrom.modified;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getModified() {
        return modified;
    }

    public void setModified(String modified) {
        this.modified = modified;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public ROLE getRole() {
        return role;
    }

    public void setRole(ROLE role) {
        this.role = role;
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

    public boolean isSuperAdmin() {
        return getRole() != null && getRole() == ROLE.SUPER_ADMIN;
    }

    public boolean isInstitutionAdmin() {
        return getRole() != null && getRole() == ROLE.ADMIN;
    }

    public boolean isLoggedUser() {
        return getRole() != null && getRole() == ROLE.SUPER_ADMIN || getRole() == ROLE.ADMIN;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        UserDTO other = (UserDTO) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "UserDTO [id=" + id + ", login=" + login + ", email=" + email + ", role=" + role + ", created=" + created + ", modified=" + modified
                + "]";
    }

}
