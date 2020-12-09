package myour.myourforum.model;

public class User {
    private int id;
    private String username;
    private String password;
    private String phoneNumber;
    private String email;
    private String createTime;
    private String updateTime;
    private boolean adminRole;
    private String description;

    public User() {
    }

    public User(int id, String username, String password, String phoneNumber, String email, String createTime, String updateTime, boolean adminRole, String description) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.createTime = createTime;
        this.updateTime = updateTime;
        this.adminRole = adminRole;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public boolean isAdminRole() {
        return adminRole;
    }

    public void setAdminRole(boolean adminRole) {
        this.adminRole = adminRole;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setUserUpdate(User userUpdate) {
        this.id = userUpdate.getId();
        this.username = userUpdate.getUsername();
        this.password = userUpdate.getPassword();
        this.phoneNumber = userUpdate.getPhoneNumber();
        this.email = userUpdate.getEmail();
        this.createTime = userUpdate.getCreateTime();
        this.updateTime = userUpdate.getUpdateTime();
        this.adminRole = userUpdate.isAdminRole();
        this.description = userUpdate.getDescription();
    }

    public void setUser(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.password = user.getPassword();
        this.phoneNumber = user.getPhoneNumber();
        this.email = user.getEmail();
        this.createTime = user.getCreateTime();
        this.updateTime = user.getUpdateTime();
        this.adminRole = user.isAdminRole();
        this.description = user.getDescription();
    }
}
