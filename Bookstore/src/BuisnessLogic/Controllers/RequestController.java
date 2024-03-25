package BuisnessLogic.Controllers;

import BuisnessLogic.Models.Book;
import BuisnessLogic.Models.BorrowRequest;
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
        query = "insert into borrowrequests (lenderid , borrowerid , bookid , status) values ('"
                + lenderId + "' , '" + userid + "' , '" +bookid + "' , 'pending')" ;//status = pending by default

        System.out.println("request borrow query = " + query);
        return dbConnection.operate(query);
    }

    public boolean acceptBorrowRequest(int requestId){
        String query = "update borrowrequests set status='accepted' where id='" + requestId+"'";
        return dbConnection.operate(query);
    }
    public boolean rejectBorrowRequest(int requestId){
        String query = "update borrowrequests set status='rejected' where id='" + requestId+"'";
        return dbConnection.operate(query);
    }
    public ArrayList<BorrowRequest> getBorrowRequestById(int requestId){
        String query = "select * from borrowrequests where id='" + requestId+"'";
        return dbConnection.select(BorrowRequest.class ,  query);
    }
    public ArrayList<BorrowRequest> getBorrowRequests(){
        String query = "select * from borrowrequests";
        return dbConnection.select(BorrowRequest.class ,  query);
    }
}
