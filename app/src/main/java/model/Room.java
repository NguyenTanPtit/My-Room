package model;

import java.io.Serializable;

public class Room implements Serializable {
    private String id;
    private String owner;
    private String address;
    private String name;
    private String img_url1;
    private String img_url2;
    private String img_url3;
    private String description;
    private String price;
    private String num_bedroom;
    private String num_bathroom;
    private String num_kitchen;
    private String num_parking;
    private String quantity_room;
    private String acreage;

    public Room() {
    }

    public Room(String id,String owner, String address, String name, String img_url1, String img_url2, String img_url3,
                String description, String price, String num_bedroom, String num_bathroom, String num_kitchen,
                String num_parking, String quantity_room, String acreage ) {
        this.id =  id;
        this.owner = owner;
        this.address = address;
        this.name = name;
        this.img_url1 = img_url1;
        this.img_url2 = img_url2;
        this.img_url3 = img_url3;
        this.description = description;
        this.price = price;
        this.num_bedroom = num_bedroom;
        this.num_bathroom = num_bathroom;
        this.num_kitchen = num_kitchen;
        this.num_parking = num_parking;
        this.quantity_room = quantity_room;
        this.acreage = acreage;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAcreage() {
        return acreage;
    }

    public void setAcreage(String acreage) {
        this.acreage = acreage;
    }

    public String getImg_url2() {
        return img_url2;
    }

    public void setImg_url2(String img_url2) {
        this.img_url2 = img_url2;
    }

    public String getImg_url3() {
        return img_url3;
    }

    public void setImg_url3(String img_url3) {
        this.img_url3 = img_url3;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImg_url1() {
        return img_url1;
    }

    public void setImg_url1(String img_url) {
        this.img_url1 = img_url;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getNum_bedroom() {
        return num_bedroom;
    }

    public void setNum_bedroom(String num_bedroom) {
        this.num_bedroom = num_bedroom;
    }

    public String getNum_bathroom() {
        return num_bathroom;
    }

    public void setNum_bathroom(String num_bathroom) {
        this.num_bathroom = num_bathroom;
    }

    public String getNum_kitchen() {
        return num_kitchen;
    }

    public void setNum_kitchen(String num_kitchen) {
        this.num_kitchen = num_kitchen;
    }

    public String getNum_parking() {
        return num_parking;
    }

    public void setNum_parking(String num_parking) {
        this.num_parking = num_parking;
    }

    public String getQuantity_room() {
        return quantity_room;
    }

    public void setQuantity_room(String quantity_room) {
        this.quantity_room = quantity_room;
    }
}
