package com.example.sweatshirts;

import java.io.Serializable;

public class Data implements Serializable {

    // It's good practice to declare a serialVersionUID for versioning.
    // If you change the class structure later, you can update this ID.
    // For now, you can generate one (IntelliJ/Android Studio can do this automatically)
    // or just use a default like 1L if you don't plan to deserialize old versions.
    private static final long serialVersionUID = 1L; // You can generate this randomly or use 1L

    private String name;
    private String email;

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
}
