package wellness.shop.Models.Users;

public class RegularUser extends User {
    private Boolean subscription;
    public RegularUser() {
    }

    public RegularUser(Boolean subscription) {
        this.subscription = subscription;
    }

    public void setSubscription(Boolean subscription) {
        this.subscription = subscription;
    }
}

