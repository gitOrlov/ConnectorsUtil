package webclient;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class RocketUser implements Serializable {
    private String _id;
    private String createdAt;
    private List<Object> emails;
    private String type;
    private String status;
    private Boolean active;
    private String _updatedAt;
    private ArrayList<String> roles;
    private String name;
    private String lastLogin;
    private String statusConnection;
    private String username;
    private ArrayList<String> __rooms;
    private Integer utcOffset;
    private LinkedHashMap settings;
    private String language;

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public List<Object> getEmails() {
        return emails;
    }

    public void setEmails(List<Object> emails) {
        this.emails = emails;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public String get_updatedAt() {
        return _updatedAt;
    }

    public void set_updatedAt(String _updatedAt) {
        this._updatedAt = _updatedAt;
    }

    public ArrayList<String> getRoles() {
        return roles;
    }

    public void setRoles(ArrayList<String> roles) {
        this.roles = roles;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(String lastLogin) {
        this.lastLogin = lastLogin;
    }

    public String getStatusConnection() {
        return statusConnection;
    }

    public void setStatusConnection(String statusConnection) {
        this.statusConnection = statusConnection;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public ArrayList<String> get__rooms() {
        return __rooms;
    }

    public void set__rooms(ArrayList<String> __rooms) {
        this.__rooms = __rooms;
    }

    public Integer getUtcOffset() {
        return utcOffset;
    }

    public void setUtcOffset(Integer utcOffset) {
        this.utcOffset = utcOffset;
    }

    public LinkedHashMap getSettings() {
        return settings;
    }

    public void setSettings(LinkedHashMap settings) {
        this.settings = settings;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    @Override
    public String toString() {
        return "RocketUser{" +
                "_id='" + _id + '\'' +
                ", createdAt='" + createdAt + '\'' +
                ", emails=" + emails +
                ", type='" + type + '\'' +
                ", status='" + status + '\'' +
                ", active=" + active +
                ", _updatedAt='" + _updatedAt + '\'' +
                ", roles=" + roles +
                ", name='" + name + '\'' +
                ", lastLogin='" + lastLogin + '\'' +
                ", statusConnection='" + statusConnection + '\'' +
                ", username='" + username + '\'' +
                ", __rooms=" + __rooms +
                ", utcOffset=" + utcOffset +
                ", settings=" + settings +
                ", language='" + language + '\'' +
                '}';
    }

    public RocketUser() {
        super();
    }
}
