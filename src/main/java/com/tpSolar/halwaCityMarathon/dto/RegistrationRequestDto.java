package com.tpSolar.halwaCityMarathon.dto;

import java.time.LocalDate;

public class RegistrationRequestDto {

    private String eventName;

    private String participantName;

    private LocalDate dob;

    private String age;

    private String gender;

    private String aadhar;

    private String bloodGroup;

    private String email;

    private String contactNumber;

    private String emergencyContact;

    private String tsize;

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
        return "RegistrationDto{" +
                "eventName='" + eventName + '\'' +
                ", participantName='" + participantName + '\'' +
                ", dob=" + dob +
                ", age='" + age + '\'' +
                ", gender='" + gender + '\'' +
                ", aadhar='" + aadhar + '\'' +
                ", bloodGroup='" + bloodGroup + '\'' +
                ", email='" + email + '\'' +
                ", contactNumber='" + contactNumber + '\'' +
                ", emergencyContact='" + emergencyContact + '\'' +
                ", tsize='" + tsize + '\'' +
                '}';
    }
}
