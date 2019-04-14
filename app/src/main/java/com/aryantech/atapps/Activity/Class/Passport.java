package com.aryantech.atapps.Activity.Class;

public class Passport {
    private String id;
    private String firstName;
    private String secondName;
    private String passportNo;
    private String gender;
    private String issue_country;
    private String nationality;
    private String dob;
    private String doe;
    private String myKad;
    private String state_dob;
    private String doi;
    private String phone_no;
    private String issue_place;

    private String faceURL;
    private String passportURL;
    private String dateScan;
    public Passport(){
        //this constructor is required
    }

    public Passport(String id, String firstName, String secondName, String passportNo, String gender, String issue_country, String nationality,
                    String dob, String doe,String doi,String phone_no, String issue_place, String myKad, String state_dob, String faceURL, String passportURL, String dateScan) {
        this.id = id;
        this.firstName = firstName;
        this.secondName = secondName;
        this.passportNo = passportNo;
        this.gender = gender;
        this.issue_country = issue_country;
        this.nationality = nationality;
        this.dob = dob;
        this.doe = doe;
        this.doi = doi;
        this.phone_no = phone_no;
        this.issue_place = issue_place;
        this.myKad = myKad;
        this.state_dob = state_dob;
        this.faceURL = faceURL;
        this.passportURL = passportURL;
        this.dateScan = dateScan;
    }

    public String getIssue_place() {
        return issue_place;
    }

    public String getPhone_no() {
        return phone_no;
    }

    public String getDoi() {
        return doi;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getId() {
        return id;
    }

    public String getSecondName() {
        return secondName;
    }

    public String getFaceURL() {
        return faceURL;
    }

    public String getDateScan() {
        return dateScan;
    }

    public String getDob() {
        return dob;
    }

    public String getDoe() {
        return doe;
    }

    public String getGender() {
        return gender;
    }

    public String getIssue_country() {
        return issue_country;
    }

    public String getMyKad() {
        return myKad;
    }

    public String getNationality() {
        return nationality;
    }

    public String getPassportNo() {
        return passportNo;
    }

    public String getPassportURL() {
        return passportURL;
    }

    public String getState_dob() {
        return state_dob;
    }
}