package kz.kase.reporter.test;

public class CustomBean {
    private String city;
    private Integer id;
    private String name;
    private String street;

//    public CustomBean(
//            String pcity,
//            Integer pid,
//            String pname,
//            String pstreet
//    )
//    {
//        city = pcity;
//        id = pid;
//        name = pname;
//        street = pstreet;
//    }

    public CustomBean getMe() {
        return this;
    }

    //
    public String getCity() {
        return city;
    }

    public void setCity(String city1) {
        this.city = city1;
    }

    public Integer getId() {
        return id;
    }

    public void setId(int id1) {
        this.id = id1;
    }

    //
    public String getName() {
        return name;
    }

    public void setName(String name1) {
        this.name = name1;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street1) {
        this.street = street1;
    }


}