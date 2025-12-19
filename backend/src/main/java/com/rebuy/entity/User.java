package com.rebuy.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "users") // table name in Supabase
public class User {

    // =================== PRIMARY KEY ===================
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    // =================== BASIC USER INFO ===================

    @Column(nullable = false, unique = true, length = 255)
    private String email;  // used for login, must be unique

    @Column(nullable = false)
    private String password; // bcrypt hashed password

    @Column(nullable = false, length = 100)
    private String name; // renamed from displayName → shows in profile & products


    // =================== OPTIONAL PROFILE FIELDS ===================

    @Column(unique = true, length = 20)
    private String phone;  // user phone number

    private String city; // location of the user (optional)

    @Column(length = 255)
    private String shippingAddress; // address for buying/delivery

    @Column(length = 500)
    private String profileImageUrl; // profile picture URL (Supabase Storage or local)


    // =================== RELATION: USER → PRODUCTS ===================
    // One user can publish many products.
    @OneToMany(mappedBy = "seller", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Product> products;


    // =================== CONSTRUCTORS ===================
    public User() {}

    public User(String email, String password, String name) {
        this.email = email;
        this.password = password;
        this.name = name;
    }


    // =================== GETTERS & SETTERS ===================
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getShippingAddress() {
        return shippingAddress;
    }

    public void setShippingAddress(String shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }
}
