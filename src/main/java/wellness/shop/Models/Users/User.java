package wellness.shop.Models.Users;

import com.fasterxml.jackson.annotation.JsonInclude;
import wellness.shop.Models.Users.Enums.Role;

@JsonInclude(JsonInclude.Include.NON_NULL)
public abstract class User {

    private int ID;
    private String UUID;
    private String Username;
    private String Password;
    private Role role;

    public User() {
    }

    public int getID() {
        return ID;
    }

    public String getUUID() {
        return UUID;
    }

    public String getUsername() {
        return Username;
    }

    public String getPassword() {
        return Password;
    }

    public Role getRole() {
        return role;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public void setUUID(String UUID) {
        this.UUID = UUID;
    }

    public void setUsername(String username) {
        Username = username;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}
