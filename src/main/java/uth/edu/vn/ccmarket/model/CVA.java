package uth.edu.vn.ccmarket.model;

public class CVA {
    private String cvaId;
    private String name;

    public CVA(String id, String name) {
        this.cvaId = id;
        this.name = name;
    }

    public String getCvaId() {
        return cvaId;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "CVA{" + cvaId + ", name=" + name + "}";
    }
}
