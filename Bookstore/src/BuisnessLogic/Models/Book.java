package BuisnessLogic.Models;

public class Book {
    public int id;
    public double price;
    public String author;
    public String title;
    public int quantity;
    public String description;
    public String genre;
    public  int ownerid;

    @Override
    public String toString() {
        return "BOOK=> id = " + id + " , price = " + price + " $ , title = " + title +
                " , author = " + author +" , genre = " + genre +  " , quantity = " + quantity
                + " , description = " + description + " , ownerId = " + ownerid ;
    }
}
