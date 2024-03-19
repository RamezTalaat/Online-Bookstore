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

    public ArrayList<Book> browseBooks(){
        String query = "select * from books";
        ArrayList<Book> result ;
        result = dbConnection.select(Book.class,query);
        return result;
    }
}
