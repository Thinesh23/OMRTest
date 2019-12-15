package us.to.opti_grader.optigrader.Model;

public class Subject {
    private String Name, Image, MenuId, sbid;

    public Subject(String name, String image, String menuId, String sbid) {
        Name = name;
        Image = image;
        MenuId = menuId;
        this.sbid = sbid;
    }

    public String getSbid() {
        return sbid;
    }

    public void setSbid(String sbid) {
        this.sbid = sbid;
    }

    public Subject() {
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }

    public String getMenuId() {
        return MenuId;
    }

    public void setMenuId(String menuId) {
        MenuId = menuId;
    }
}
