package BuisnessLogic.Models;

public class Review {
    public int id;
    public int userid;
    public int bookid;
    public int rate;
    public String comment;

    @Override
    public String toString() {
        return "REVIEW=> id = " + id + " , userid = " + userid + " , bookid = " + bookid +
                " , rate = " + rate + " , comment = " + comment;
    }
}
