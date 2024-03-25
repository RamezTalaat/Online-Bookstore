package BuisnessLogic.Controllers;

import BuisnessLogic.Models.BorrowRequest;
import BuisnessLogic.Models.Review;
import DbContext.DbConnection;

import java.util.ArrayList;

public class ReviewController {
    private DbConnection dbConnection;
    public ReviewController(){
        dbConnection= new DbConnection();
    }

    public boolean removeBookReviews(int bookId){
        String query = "delete from reviews where bookid='" +  bookId+ "'";
        return dbConnection.operate(query);
    }

    public ArrayList<Review> getReviewsByBookId(int bookId){
        String query = "select * from reviews where bookid='" +  bookId+ "'";
        return dbConnection.select(Review.class,query);
    }
}
