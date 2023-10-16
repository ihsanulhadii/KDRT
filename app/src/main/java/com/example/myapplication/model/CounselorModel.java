package com.example.myapplication.model;

import java.io.Serializable;

public class CounselorModel implements Serializable {

   private String id;
   private String email;
   private String img;
   private String name;
   private String password;
   private String nohp;

   public String getId() {
      return id;
   }

   public void setId(String id) {
      this.id = id;
   }

   public String getEmail() {
      return email;
   }

   public void setEmail(String email) {
      this.email = email;
   }

   public String getImg() {
      return img;
   }

   public void setImg(String img) {
      this.img = img;
   }

   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public String getPassword() {
      return password;
   }

   public void setPassword(String password) {
      this.password = password;
   }

   public String getNohp() {
      return nohp;
   }

   public void setNohp(String nohp) {
      this.nohp = nohp;
   }


}
