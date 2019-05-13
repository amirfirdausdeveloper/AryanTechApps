package com.aryantech.atapps.Activity.Class;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import java.util.Date;

@Table(name = "tbl_passport")
public class PassportDB extends Model {

    @Column(name = "uploadStatus")
    public String uploadStatus;

    @Column(name = "pass_no")
    public String pass_no;

    @Column(name = "first_name")
    public String first_name;

    @Column(name = "last_name")
    public String last_name;

    @Column(name = "type")
    public String type;

    @Column(name = "expiry_date")
    public String expiry_date;

    @Column(name = "birth_date")
    public String birth_date;

    @Column(name = "issuing_date")
    public String issuing_date;

    @Column(name = "issuing_off")
    public String issuing_off;

    @Column(name = "birth_place")
    public String birth_place;

    @Column(name = "ic_no")
    public String ic_no;

    @Column(name = "gender")
    public String gender;

    @Column(name = "citizenship")
    public String citizenship;

    @Column(name = "country_code")
    public String country_code;

    @Column(name = "no_phone")
    public String no_phone;

    @Column(name = "scan_date")
    public Date scan_date;

    @Column(name = "pas_img")
    public String pas_img;



}
