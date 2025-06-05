package com.peaceandcotton.sweatshirts;

import java.io.Serializable;

public class Data implements Serializable {

    // It's good practice to declare a serialVersionUID for versioning.
    // If you change the class structure later, you can update this ID.
    // For now, you can generate one (IntelliJ/Android Studio can do this automatically)
    // or just use a default like 1L if you don't plan to deserialize old versions.
    private static final long serialVersionUID = 1L; // You can generate this randomly or use 1L

    private String name;
    private String email;

    private String color;
    private String size;
    private String initials;
    private boolean[] bTikTokSolutions;
    private String[] sTikTokSolutions;
    private String Logo;

    public Data()
    {
        bTikTokSolutions = new boolean[7];
        sTikTokSolutions = new String[7];
    }

    // Getters and Setters (standard practice)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean[] getTikTokSolutions() {
        return bTikTokSolutions;
    }

    public void setTikTokSolutions(boolean[] tikTokSolutions) {
        bTikTokSolutions = tikTokSolutions;
    }

    public String[] getsTikTokSolutions() {
        return sTikTokSolutions;
    }

    public void setsTikTokSolutions(String[] sTikTokSolutions) {
        this.sTikTokSolutions = sTikTokSolutions;
    }

    public void setCheckedTikTokSolutionAt(int index, boolean bTikTokSolutions) {
        this.bTikTokSolutions[index] = bTikTokSolutions;
    }
    public void setCheckedTikTokSolutionTextAt(int index, String sTikTokSolutions) {
        this.sTikTokSolutions[index] = sTikTokSolutions;
    }

    public boolean getCheckedTikTokSolutionAt(int index) {
        return this.bTikTokSolutions[index];
    }
    public String getCheckedTikTokSolutionTextAt(int index) {
        return this.sTikTokSolutions[index];
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getInitials() {
        return initials;
    }

    public void setInitials(String initials) {
        this.initials = initials;
    }

    public String getLogo() {
        return Logo;
    }

    public void setLogo(String logo) {
        Logo = logo;
    }
}
