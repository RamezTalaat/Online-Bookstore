package BuisnessLogic.Controllers;

import BuisnessLogic.Authentication.Response;
import BuisnessLogic.Models.Book;
import DbContext.DbConnection;

import java.util.ArrayList;

public class BookController {
    private DbConnection dbConnection;
    public BookController(){
        dbConnection= new DbConnection();
    }
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
            response.status=200;
            response.message="The book has been removed from the books and the number of books now: "+result.get(0).quantity;
            System.out.println(response);
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
        Response response = new Response();
        ArrayList<Book> result;
        String query;
        switch(str){
            case "title":
                query = "select * from books where title ='"+value+"'";
                result = dbConnection.select(Book.class, query);
                if(!result.isEmpty()){
                    response.status=200;
                    response.message="list returned Successfully";
                    return result;
                }
                System.out.println("There is no books with this "+str);
                break;
            case "author":
                query = "select * from books where author ='"+value+"'";
                result = dbConnection.select(Book.class, query);
                if(!result.isEmpty()){
                    response.status=200;
                    response.message="list returned Successfully";
                    return result;
                }
                System.out.println("There is no books with this "+str);
                break;
            case "genre":
                query = "select * from books where genre ='"+value+"'";
                result = dbConnection.select(Book.class, query);
                if(!result.isEmpty()){
                    response.status=200;
                    response.message="list returned Successfully";
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

    public ArrayList<Book> getUserBooks(int userid){
        String query = "select * from books where ownerid = '" + userid + "' or borrowerid = '" + userid + "'";
        ArrayList<Book> result ;
        result = dbConnection.select(Book.class,query);
        return result;
    }
    public ArrayList<Book> browseBooks(){
        String query = "select * from books";
        ArrayList<Book> result ;
        result = dbConnection.select(Book.class,query);
        return result;
    }
    public Response addBook(double price, String genre, String title, String author, int quantity, String description) {
        Response response = new Response();
        if (genre == null || title == null || author == null || quantity ==0) {  //used == not .equals() to compare references not string values
            response.status = 402; //custom error for null values
            response.message = "null arguments are not valid , please try again";
            return response;
        }
        DbConnection dbConnection = new DbConnection();
        String query = "";

        query = "select * from books where price ="+price + " AND genre='" +genre + "' AND " + "title='" +title  + "' AND " + "author='" + author + "'";
        System.out.println("query = " + query);
        ArrayList<Book> result = dbConnection.select(Book.class, query);
        System.out.println(result.toString());
        if (!result.isEmpty()) { //custom error status 400 for existing book.
            System.out.println("Book already exists, quantity will be incremented.");
            query = "UPDATE books SET quantity=" + (quantity + result.get(0).quantity) + " WHERE title='" + title + "' AND genre='" + genre + "' AND author='" + author + "'";
            Boolean queryResult = dbConnection.operate(query);
            if (!queryResult)
            {
                response.status = 400;
                response.message = "Quantity couldn't be incremented";
                return response;
            }
            response.status=200;
            response.message="Book quantity incremented successfully!";
            response.object=result;
            return response;
        } else {
            query = "insert into books (price ,genre ,title, author, quantity, description) values ('" + price + "' , '" + genre + "' , '"+title+"' , '" +author + "' , '"+quantity+"', '"+description+"')";
            Boolean queryResult = dbConnection.operate(query);
            if (!queryResult) {
                response.status = 400;
                response.message = "Error adding this book!";
                return response;
            }
            response.status=200;
            response.message="Book added successfully.";
            response.object=result;
            return response;
        }
    }
}
