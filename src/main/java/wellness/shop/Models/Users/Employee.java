package wellness.shop.Models.Users;

import wellness.shop.Models.Users.Enums.Specialization;

import java.util.List;

public class Employee extends User{
    private List<Specialization> specializations;
    public Employee() {
    }

    public List<Specialization> getSpecializations() {
        return specializations;
    }

    public void setSpecializations(List<Specialization> specializations) {
        this.specializations = specializations;
    }

    public void addSpecialization(Specialization specialization){
        specializations.add(specialization);
    }

    public void removeSpecialization(Specialization specialization){
        specializations.remove(specialization);
    }

}
