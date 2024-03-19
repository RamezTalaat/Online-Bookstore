package BuisnessLogic.Controllers;

import BuisnessLogic.Models.Book;
import DbContext.DbConnection;

import java.util.ArrayList;

public class RequestController {
    private DbConnection dbConnection;
    public RequestController(){
        dbConnection= new DbConnection();
    }
    public boolean submitBorrowRequest(int userid , int bookid){
        String query = "select * from books where id = '" + bookid + "'";
        ArrayList<Book> books = dbConnection.select(Book.class , query);
        if(books.size() != 1)
            return false;
        int lenderId = books.get(0).ownerid;
        query = "insert into users (lenderid , borrowerid , bookid , status) values ('"
                + lenderId + "' , '" + userid + "' , '" +bookid + "' , 'pending')" ;//status = pending by default

        return dbConnection.operate(query);
    }
}
