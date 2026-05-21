package models.people;

import java.io.Serializable;

public abstract class Person implements Serializable {
    private static final long serialVersionUID = 1L;


    private String name;
    private int age;
    private String email;
    private int phoneNumber;
    
    public Person(String name, int age, String email, int phoneNumber) {
        this.name = name;
        this.age = age;
        this.email = email;
        this.phoneNumber = phoneNumber;
    }

    // Getters

    public String getName() { return name; }
    public int getAge() { return age; }
    public String getEmail() { return email; }
    public int getPhoneNumber() { return phoneNumber; }

    // setters 

    public void setName(String name) {
        this.name = name;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhoneNumber(int phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    // display info

    public void displayInfo() {
        System.out.println("=================================================");
        System.out.println("                 PERSONAL IDENTITY RECORD        ");
        System.out.println("=================================================");
        System.out.printf("  %-22s : %s\n", "Full Legal Name", name);
        System.out.printf("  %-22s : %d years old\n", "Age Record", age);
        System.out.printf("  %-22s : %s\n", "Email Address", email);
        System.out.printf("  %-22s : %d\n", "Phone Registration", phoneNumber);
        System.out.println("=================================================");
    }
}