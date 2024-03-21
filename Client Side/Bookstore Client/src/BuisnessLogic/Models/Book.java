package BuisnessLogic.Models;

import java.io.Serializable;

public class Book implements Serializable {
    public int id;
    public double price;
    public String author;
    public String title;
    public int quantity;
    public String description;
    public String genre;
    public  int ownerid;
    public  int borrowerid;

//    @Override
//    public String toString() {
//        return "BOOK=> id = " + id + " , price = " + price + " $ , title = " + title +
//                " , author = " + author +" , genre = " + genre +  " , quantity = " + quantity
//                + " , description = " + description + " , ownerId = " + ownerid ;
//    }
    @Override
    public String toString() {
        return "Book title :" + title + " , price = " + price + " $, author = " + author +
                "\ngenre = " + genre +  " , quantity = " + quantity + " , description = " + description +
                "\n=======================================================================";
    }
}
