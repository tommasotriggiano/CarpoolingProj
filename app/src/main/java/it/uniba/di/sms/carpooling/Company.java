package it.uniba.di.sms.carpooling;



public class Company {
    private String name;
    private Address companyAddress;
    private String idAdmin;

    public Company(String name,Address address,String idAdmin) {
        this.name = name;
        this.companyAddress = address;
        this.idAdmin = idAdmin;
    }

    public String getName() {
        return name;
    }

    public Address getCompanyAddress() {
        return companyAddress;
    }

    public String getIdAdmin() {
        return idAdmin;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCompanyAddress(Address companyAddress) {
        this.companyAddress = companyAddress;
    }

    public void setIdAdmin(String idAdmin) {
        this.idAdmin = idAdmin;
    }
}
