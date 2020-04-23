package com.example.demo.forJsonObject.ElObj;

public class Rules {

     short read;
     short write;
     short delete;

     public Rules () {
     }

     public Rules (short read, short write, short delete) {
          this.read = read;
          this.write = write;
          this.delete = delete;
     }

     public short getRead () {
          return read;
     }

     public void setRead (short read) {
          this.read = read;
     }

     public short getWrite () {
          return write;
     }

     public void setWrite (short write) {
          this.write = write;
     }

     public short getDelete () {
          return delete;
     }

     public void setDelete (short delete) {
          this.delete = delete;
     }
}
