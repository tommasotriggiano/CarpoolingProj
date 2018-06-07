package it.uniba.di.sms.carpooling;


public class User {

    private String UserEmail;
    private String UserName;
    private String UserSurname;
    private String UserAddress;
    private String UserCompany;
    private String UserPhone;

    //la macchina non è obbligatoria ma si può inserire

    private String UserCar;

    public User(String UserEmail, String UserName, String UserSurname, String UserAddress, String UserCompany, String UserPhone){
        this.UserEmail = UserEmail;
        this.UserName = UserName;
        this.UserSurname = UserSurname;
        this.UserAddress = UserAddress;
        this.UserCompany = UserCompany;
        this.UserPhone = UserPhone;
    }



    //metodi di get

    public String getUserEmail(){
        return UserEmail;
    }
    public String getUserName() {
        return UserName;}
    public String getUserSurname() {
        return UserSurname;}

    public String getUserAddress() {
        return UserAddress;}

    public String getUserCompany() {
        return UserCompany;}

    public String getUserPhone() {
        return UserPhone;}

    public String getUserCar() {
        return UserCar;}

}
