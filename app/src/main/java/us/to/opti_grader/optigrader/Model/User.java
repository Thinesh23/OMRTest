package us.to.opti_grader.optigrader.Model;

public class User {
    private String Name;
    private String Password;
    private String Phone;
    private String isStaff;
    private String isTeacher;
    private String secureCode;
    private String status;

    public User() {
    }

    public User(String name, String password, String secureCode, String phone, String isTeacher) {
        Name = name;
        Password = password;
        Phone = phone;
        isStaff = "false";
        this.isTeacher = isTeacher;
        this.status = "offline";
        this.secureCode = secureCode;
    }

    public User(String name, String password, String secureCode, String phone, String isTeacher, String isStaff) {
        Name = name;
        Password = password;
        Phone = phone;
        this.isStaff = isStaff;
        this.isTeacher = isTeacher;
        this.status = "offline";
        this.secureCode = secureCode;
    }



    public String getIsStaff() {
        return isStaff;
    }

    public void setIsStaff(String isStaff) {
        this.isStaff = isStaff;
    }

    public String getIsTeacher() {
        return isTeacher;
    }

    public void setIsTeacher(String isTeacher) {
        this.isTeacher = isTeacher;
    }

    public String getSecureCode() {
        return secureCode;
    }

    public void setSecureCode(String secureCode) {
        this.secureCode = secureCode;
    }

    public String getPhone() {
        return Phone;
    }

    public void setPhone(String phone) {
        Phone = phone;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}