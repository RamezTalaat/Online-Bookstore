package BuisnessLogic.Controllers;

import BuisnessLogic.Authentication.Response;
import BuisnessLogic.Models.Book;
import BuisnessLogic.Models.BorrowRequest;
import DbContext.DbConnection;

import java.util.ArrayList;

public class BookController {
    private DbConnection dbConnection;
    public BookController(){
        dbConnection= new DbConnection();
    }
    public boolean removeBook(int bookID){
        String query = "select * from books where id ="+bookID;
        DbConnection dbConnection = new DbConnection();

        ArrayList<Book> result = dbConnection.select(Book.class, query);
        //System.out.println(result.get(0).quantity);
        boolean res;
        if(result.get(0).quantity > 1){
            int numOfBooks = result.get(0).quantity ;
            query = "update books set quantity ="+(numOfBooks - 1)+" where id ="+bookID;
            res = dbConnection.operate(query);
            return res;
        }
        String sql = "Delete from books where id ="+bookID;
        res = dbConnection.operate(sql);
        if(res){
            System.out.println("Book deleted Successfully");
            return true;
        }
        System.out.println("There is a problem");
        return false;
    }
    public ArrayList<Book> searchForBook(String str, String value){

        ArrayList<Book> result;
        String query;
        switch(str){
            case "title":
                query = "select * from books where title ='"+value+"'";
                result = dbConnection.select(Book.class, query);
                if(!result.isEmpty()){
                    return result;
                }
                System.out.println("There is no books with this "+str);
                break;
            case "author":
                query = "select * from books where author ='"+value+"'";
                result = dbConnection.select(Book.class, query);
                if(!result.isEmpty()){

                    return result;
                }
                System.out.println("There is no books with this "+str);
                break;
            case "genre":
                query = "select * from books where genre ='"+value+"'";
                result = dbConnection.select(Book.class, query);
                if(!result.isEmpty()){
                    return result;
                }
                System.out.println("There is no books with this "+str);
                break;
            default:
                System.out.println("please enter valid data");
                break;
        }
        return null;
    }

    public boolean updateBookBorrower(int bookId , int borrowerId){
        String query = "update books set borrowerid='" + borrowerId+ "' where id='" + bookId+"'";
        return dbConnection.operate(query);
    }
    public ArrayList<BorrowRequest> getBorrowRequestsHistory(int userid){
        String query = "select * from borrowrequests where borrowerid = '" + userid + "' or lenderid = '" + userid +"'";
        ArrayList<BorrowRequest> result ;
        result = dbConnection.select(BorrowRequest.class,query);
        return result;
    }

    public ArrayList<Book> getUserBooks(int userid){
        String query = "select * from books where ownerid = '" + userid + "' or borrowerid = '" + userid + "'";
        ArrayList<Book> result ;
        result = dbConnection.select(Book.class,query);
        return result;
    }
    public Book getBookById(int bookId){
        String query = "select * from books where id = '" + bookId + "'";
        ArrayList<Book> result ;
        result = dbConnection.select(Book.class,query);
        if(result == null || result.isEmpty())
            return null;
        return result.get(0);
    }
    public ArrayList<Book> browseBooks(){
        String query = "select * from books";
        ArrayList<Book> result ;
        result = dbConnection.select(Book.class,query);
        return result;
    }
    public Boolean addBook(double price, String genre, String title, String author, int quantity, String description, int currentUserID) {
        if (genre == null || title == null || author == null || quantity ==0 || currentUserID<0) {
            System.out.println("Can't accept null values or qty equals to 0");
            return false;
        }
        DbConnection dbConnection = new DbConnection();
        String query = "";
        query = "insert into books (price ,genre ,title, author, quantity, description, ownerid) values ('" + price + "' , '" + genre + "' , '"+title+"' , '" +author + "' , '"+quantity+"', '"+description+"', '"+currentUserID+"')";
        Boolean queryResult = dbConnection.operate(query);
        if (!queryResult) {
            //Couldn't add this book
            //System.out.println("Couldn't add this book.");
            return false;
        }
        //Book added successfully
        //System.out.println("Book added successfully!");
        return true;
    }
    public Boolean addReview(int userId,String comment, int rate, int bookId)
    {
        if(rate<=-1 || rate>11 || bookId<0)
        {
            System.out.println("Rating must be between 0 and 10, and bookId must be more than 0");
            return false;
        }
        DbConnection dbConnection = new DbConnection();
        String query = "";
        int querybookId = getBookById(bookId).id;
        query = "insert into reviews (userid ,bookid ,rate, comment) values ('" + userId + "' , '" + querybookId + "' , '"+rate+"' , '" +comment + "')";
        Boolean queryResult = dbConnection.operate(query);
        if (!queryResult) {
            //Couldn't add this review
            //System.out.println("Couldn't add this review.");
            return false;
        }
        return true;
    }
}
