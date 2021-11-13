import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.time.temporal.ChronoUnit;

public class Library {
    public ArrayList<Book> books;
    public ArrayList<Subscription> users;

    public Library() {
        books = new ArrayList<Book>();
        users = new ArrayList<Subscription>();
    }

    public List<Book> getSortedBooks(Comparator<? super Book> comp) {
        return books.stream().sorted(comp)
                .collect(Collectors.toList());
    }

    public List<String> getEmailsOfUserWithNumberOfBooks(int num, boolean bigger) {
        return users.stream().filter(user -> bigger ? user.lendings.size() >= num : user.lendings.size() < num)
            .map(Subscription::getEmail).collect(Collectors.toList());
    }

    public List<Subscription> UsersByAuthor(String author) {
        List<Integer> booksByAuthor = books.stream().filter(book -> book.author.equals(author))
            .map(Book::getID).collect(Collectors.toList());
        return users.stream().filter(user -> !user.lendings.stream()
            .map(Lending::getBookID).filter(booksByAuthor::contains).collect(Collectors.toList()).isEmpty())
            .collect(Collectors.toList());
    }

    public int getMaxBooksByUser() {
        return users.stream().map(Subscription::getLendings).map(List::size).sorted(Comparator.reverseOrder())
            .findFirst().orElse(-1);
    }

    public Map<Book, Integer> getDeptedBooksByUser(Integer userId) {
        Subscription user = users.stream().filter(u -> userId.equals(u.subID)).findFirst().orElse(null);
        var debts = user.lendings.stream().filter(l -> l.returned == null).collect(Collectors.toList());
        
        return debts.stream().collect(Collectors.toMap(
            l -> (Book)books.stream().filter(b -> Integer.valueOf(b.bookID).equals(l.getBookID())).findFirst().orElse(null),
            l -> (int)ChronoUnit.DAYS.between(l.planned, LocalDate.now())
        ));

    }
}
