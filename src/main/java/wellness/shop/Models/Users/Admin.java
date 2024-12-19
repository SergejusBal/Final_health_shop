package wellness.shop.Models.Users;

import wellness.shop.Models.Users.Enums.Privileges;

import java.util.List;

public class Admin extends User{
    private List<Privileges> privileges;
    public Admin() {
    }

    public List<Privileges> getPrivileges() {
        return privileges;
    }

    public void setPrivileges(List<Privileges> privileges) {
        this.privileges = privileges;
    }

    public void addPrivilege(Privileges privilege){
        privileges.add(privilege);
    }
    public void removePrivilege(Privileges privilege){
        privileges.remove(privilege);
    }

}
