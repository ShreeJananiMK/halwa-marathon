package com.tpSolar.halwaCityMarathon.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.util.Arrays;

@Entity
@Table(name = "registration_details",indexes = {@Index(name = "idx_email_aadhar", columnList = "participant_email, participant_aadhar")})
public class RegistrationDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "participant_number")
    private long id;

    @Column(name = "event_name")
    private String eventName;

    @Column(name = "participant_name")
    private String participantName;

    @Column(name = "dob")
    private LocalDate dob;

    @Column(name = "participant_age")
    private String age;

    @Column(name = "participant_gender")
    private String gender;

    @Column(name = "participant_aadhar")
    private String aadhar;

    @Column(name = "participant_blood_group")
    private String bloodGroup;

    @Lob
    @Column(name = "participant_image",columnDefinition = "LONGBLOB")
    private byte[] image;

    @Column(name = "participant_email")
    private String email;

    @Column(name = "participant_contact")
    private String contactNumber;

    @Column(name = "participant_emergency_contact")
    private String emergencyContact;

    @Column(name = "participant_tsize")
    private String tsize;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getParticipantName() {
        return participantName;
    }

    public void setParticipantName(String participantName) {
        this.participantName = participantName;
    }

    public LocalDate getDob() {
        return dob;
    }

    public void setDob(LocalDate dob) {
        this.dob = dob;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getAadhar() {
        return aadhar;
    }

    public void setAadhar(String aadhar) {
        this.aadhar = aadhar;
    }

    public String getBloodGroup() {
        return bloodGroup;
    }

    public void setBloodGroup(String bloodGroup) {
        this.bloodGroup = bloodGroup;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String getEmergencyContact() {
        return emergencyContact;
    }

    public void setEmergencyContact(String emergencyContact) {
        this.emergencyContact = emergencyContact;
    }

    public String getTsize() {
        return tsize;
    }

    public void setTsize(String tsize) {
        this.tsize = tsize;
    }

    @Override
    public String toString() {
        return "RegistrationDetails{" +
                "id=" + id +
                ", eventName='" + eventName + '\'' +
                ", participantName='" + participantName + '\'' +
                ", dob=" + dob +
                ", age=" + age +
                ", gender='" + gender + '\'' +
                ", aadhar='" + aadhar + '\'' +
                ", bloodGroup='" + bloodGroup + '\'' +
                ", image=" + Arrays.toString(image) +
                ", email='" + email + '\'' +
                ", contactNumber='" + contactNumber + '\'' +
                ", emergencyContact='" + emergencyContact + '\'' +
                ", tsize='" + tsize + '\'' +
                '}';
    }
}
