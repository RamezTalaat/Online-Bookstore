package BuisnessLogic.Models;

public class BorrowRequest {
    public int id;
    public int lenderid;
    public int borrowerid;
    public int bookid;
    public String status;

    @Override
    public String toString() {
        return "BORROWREQUEST=> id = " + id + " , lenderid = " + lenderid + " , borrowerid = " + borrowerid +
                " , bookid = " + bookid + " , comment = " + status;
    }
}
