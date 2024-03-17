package BuisnessLogic.Controllers;

import BuisnessLogic.Authentication.Response;
import BuisnessLogic.Models.Book;
import DbContext.DbConnection;

import java.util.ArrayList;

public class BookController {
    public boolean removeBook(int bookID){
        Response response = new Response();
        String query = "select * from books where id ="+bookID;
        DbConnection dbConnection = new DbConnection();

        ArrayList<Book> result = dbConnection.select(Book.class, query);
        //System.out.println(result.get(0).quantity);
        boolean res;
        if(result.get(0).quantity > 1){
            int numOfBooks = result.get(0).quantity ;
            query = "update books set quantity ="+(numOfBooks - 1)+" where id ="+bookID;
            res = dbConnection.operate(query);
            System.out.println("The book has been removed from the books and the number of books now: "+result.get(0).quantity);
            return res;
        }
        String sql = "Delete from books where id ="+bookID;
        res = dbConnection.operate(sql);
        if(res){
            System.out.println("Book deleted Successfully");
            return true;
        }
        return false;
    }
}
