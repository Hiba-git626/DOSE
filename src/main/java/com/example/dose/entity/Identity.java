package com.example.dose.entity;


import com.example.dose.security.EncryptedStringConverter;
import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "identity")
public class Identity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private AppUser user;

    @Convert(converter = EncryptedStringConverter.class) //chiffrement des données avant les enregistrées
    @Column (length = 512)
    private String email;

    @Convert(converter = EncryptedStringConverter.class)
    @Column(length = 512)
    private String phone;

    @Convert(converter = EncryptedStringConverter.class)
    @Column(length = 512)
    private String guardianContact;

    public Identity (){
    }

    public UUID getId(){
        return id;
    }

    public AppUser getUser(){
        return user;
    }

    public void setUser(AppUser user) {
        this.user = user;
    }

    public String getEmail(){
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone(){
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getGuardianContact(){
        return guardianContact;
    }

    public void setGuardianContact(String guardianContact) {
        this.guardianContact = guardianContact;
    }
}
