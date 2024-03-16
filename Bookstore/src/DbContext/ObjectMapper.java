package DbContext;

import BuisnessLogic.Models.Book;
import BuisnessLogic.Models.BorrowRequest;
import BuisnessLogic.Models.Review;
import BuisnessLogic.Models.User;

import java.sql.ResultSet;

public class ObjectMapper {
    public User mapUser(ResultSet resultSet){
        User user= new User();
        try {
            user.id = resultSet.getInt("id");
            user.name = resultSet.getString("name");
            user.userName = resultSet.getString("userName");
            user.password = resultSet.getString("password");
            user.role = resultSet.getString("role");
        }catch (Exception e){
            e.printStackTrace();
            System.out.println("Error in mapping user");
        }
        return user;
    }

    public Book mapBook(ResultSet resultSet){
        Book book= new Book();
        try {
            book.id = resultSet.getInt("id");
            book.title = resultSet.getString("title");
            book.quantity = resultSet.getInt("quantity");
            book.description = resultSet.getString("description");
            book.ownerid = resultSet.getInt("ownerid");
            book.author = resultSet.getString("author");
            book.genre = resultSet.getString("genre");
            book.price = resultSet.getDouble("price");
        }catch (Exception e){
            e.printStackTrace();
            System.out.println("Error in mapping book");
        }
        return book;
    }
    public Review mapReview(ResultSet resultSet){
        Review review= new Review();
        try {
            review.id = resultSet.getInt("id");
            review.userid = resultSet.getInt("userid");
            review.bookid = resultSet.getInt("bookid");
            review.rate = resultSet.getInt("rate");
            review.comment = resultSet.getString("comment");
        }catch (Exception e){
            e.printStackTrace();
            System.out.println("Error in mapping review");
        }
        return review;
    }

    public BorrowRequest mapBorrowRequest(ResultSet resultSet){
        BorrowRequest borrowRequest= new BorrowRequest();
        try {
            borrowRequest.id = resultSet.getInt("id");
            borrowRequest.lenderid = resultSet.getInt("lenderid");
            borrowRequest.bookid = resultSet.getInt("bookid");
            borrowRequest.borrowerid = resultSet.getInt("borrowerid");
            borrowRequest.status = resultSet.getString("status");
        }catch (Exception e){
            e.printStackTrace();
            System.out.println("Error in mapping borrowRequest");
        }
        return borrowRequest;
    }
    public <T> T mapObject(Class<T> tClass ,ResultSet resultSet){


        try {
            T object = tClass.getDeclaredConstructor().newInstance(); //to create wanted object

            if(tClass == User.class){
                object =  tClass.cast(mapUser(resultSet));
            }
            else if(tClass == Book.class){
                object = tClass.cast(mapBook(resultSet));
            }else if(tClass == Review.class){
                object = tClass.cast(mapReview(resultSet));
            }else if(tClass == BorrowRequest.class){
                object = tClass.cast(mapBorrowRequest(resultSet));
            }else{ //no valid class type to map to
                return null;
            }
            return object;
        }catch (Exception e){
            e.printStackTrace();
            System.out.println("Error in mapping user");
            return  null;
        }

    }
}
