package com.test.nfc_demo.pojo;

/**
 * This is a plain old java class(POJO) that contains the contact template.
 */

public class ContactInfo {

    private int id;
    private String name;
    private String number;
    private String email;
    private String url;
    private String address;

    public ContactInfo(String name, String number, String email, String url, String address) {
        this.name = name;
        this.number = number;
        this.email = email;
        this.url = url;
        this.address = address;
    }

    public ContactInfo(String name) {
        super();
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public String toString() {
        return "ContactInfo{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", number='" + number + '\'' +
                ", email='" + email + '\'' +
                ", url='" + url + '\'' +
                ", address='" + address + '\'' +
                '}';
    }
}
