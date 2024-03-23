package BuisnessLogic.Models;

import ClientLogic.UserController;

import java.io.Serializable;

public class BorrowRequest implements Serializable {
    public int id;
    public int lenderid;
    public int borrowerid;
    public int bookid;
    public String status;

    @Override
    public String toString() {
        return "BORROWREQUEST=> id = " + id + " , lenderid = " + lenderid + " , borrowerid = " + borrowerid +
                " , bookid = " + bookid + " , status = " + status;
    }
}
