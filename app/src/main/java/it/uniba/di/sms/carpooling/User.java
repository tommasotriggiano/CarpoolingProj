package it.uniba.di.sms.carpooling;


public class User {

    private String userEmail;
    private String userName;
    private String userSurname;
    private String userAddress;
    private String userCompany;
    private String userPhone;

    //la macchina non è obbligatoria ma si può inserire

    private String UserCar;

    public User(String useremail, String username, String usersurname, String useraddress, String usercompany, String userphone){
        this.userEmail = useremail;
        this.userName = username;
        this.userSurname = usersurname;
        this.userAddress = useraddress;
        this.userCompany = usercompany;
        this.userPhone = userphone;
    }

    public User(){
        //costruttore vuoto che servirà per il metodo OnDataChange(
    }



    //metodi di get
    public String getUseremail() {
        return userEmail;
    }

    public String getUsername() {
        return userName;
    }

    public String getUsersurname() {
        return userSurname;
    }

    public String getUseraddress() {
        return userAddress;
    }

    public String getUsercompany() {
        return userCompany;
    }

    public String getUserphone() {
        return userPhone;
    }


    //metodi di set
    public void setUseremail(String userEmail) {
        this.userEmail = userEmail;
    }

    public void setUsername(String userName) {
        this.userName = userName;
    }

    public void setUsersurname(String userSurname) {
        this.userSurname = userSurname;
    }

    public void setUseraddress(String userAddress) {
        this.userAddress = userAddress;
    }

    public void setUsercompany(String userCompany) {
        this.userCompany = userCompany;
    }

    public void setUserphone(String userPhone) {
        this.userPhone = userPhone;
    }



}
