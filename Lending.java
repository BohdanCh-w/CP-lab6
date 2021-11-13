import java.time.LocalDate;

public class Lending {
    int bookID;
    LocalDate taken;
    LocalDate planned;
    LocalDate returned;

    public int getBookID() { return bookID; }

    public Lending(int book_id, LocalDate taken_, LocalDate planned_) {
        bookID = book_id;
        taken = taken_;
        planned = planned_;
        returned = null;
    }

    public void ReturnedBook(LocalDate time) {
        returned = time;
    }
}
