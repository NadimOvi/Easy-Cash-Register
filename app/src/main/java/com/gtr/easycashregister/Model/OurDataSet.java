package com.gtr.easycashregister.Model;

import java.util.ArrayList;

public class OurDataSet {

   private String phoneNumber;
   private String itemName;
   private Float quantity;
   private Double price;
   private String image;

   public OurDataSet(String phoneNumber, String itemName, Float quantity, Double price, String image) {
      this.phoneNumber = phoneNumber;
      this.itemName = itemName;
      this.quantity = quantity;
      this.price = price;
      this.image = image;
   }

   public OurDataSet() {
   }

   public String getPhoneNumber() {
      return phoneNumber;
   }

   public void setPhoneNumber(String phoneNumber) {
      this.phoneNumber = phoneNumber;
   }

   public String getItemName() {
      return itemName;
   }

   public void setItemName(String itemName) {
      this.itemName = itemName;
   }

   public Float getQuantity() {
      return quantity;
   }

   public void setQuantity(Float quantity) {
      this.quantity = quantity;
   }

   public Double getPrice() {
      return price;
   }

   public void setPrice(Double price) {
      this.price = price;
   }

   public String getImage() {
      return image;
   }

   public void setImage(String image) {
      this.image = image;
   }
}
