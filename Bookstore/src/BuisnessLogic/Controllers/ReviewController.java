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
    public int rates(int bookId){
        String query = "select * from reviews where bookid='" +  bookId+ "'";
        ArrayList<Review> res = dbConnection.select(Review.class, query);
        if(res == null || res.isEmpty()){
            return 0;
        }
        int sum = 0;
        for (int i = 0; i < res.size(); i++) {
            sum = sum + res.get(i).rate;
        }
        int accumilativeRate = sum /res.size();
        return accumilativeRate;
    }
}
