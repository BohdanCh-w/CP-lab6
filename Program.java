import org.json.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Program {
    public static void main(String[] args) throws Exception {
        Library lib = new Library();
        FillData(lib);

        Scanner sc = new Scanner(System.in);
        System.out.println(
              "Avalilible options\n"
            + "  1 - Sort Books\n"
            + "  2 - Get Users Emails\n"
            + "  3 - Number of users by author\n"
            + "  4 - Most Persistent Reader\n"
            + "  5 - Create Spam");

        while(true) {
            int choice = sc.nextInt();
            switch(choice) {
            case 1:
                SortBooks(lib);
                break;
            case 2:
                getEmailsOfUsers(lib);
                break;
            case 3:
                getUsersByAuthor(lib);
                break;
            case 4:
                System.out.println(lib.getMaxBooksByUser());
                break;
            case 5:
                getSpamLists(lib);
                break;
            case 6:
                getDepts(lib);
                break;
            default:
                sc.close();
                System.exit(0);
            }
        }
    }

    public static void getDepts(Library lib) {
        for(var user : lib.users) {
            System.out.println(user);
            PrintMap(lib.getDeptedBooksByUser(user.subID));
        }
    }

    public static void getSpamLists(Library lib) {
        System.out.println("Enter number of books lended : ");
        Scanner sc = new Scanner(System.in);
        int num = sc.nextInt();
        var NewsEmails = lib.getEmailsOfUserWithNumberOfBooks(num, true);
        var inTimeEmails = lib.getEmailsOfUserWithNumberOfBooks(num, false);
        sc.close();

        System.out.println("We have new Books");
        PrintList(NewsEmails);
        System.out.println("Return books in Time");
        PrintList(inTimeEmails);
    }

    public static void getUsersByAuthor(Library lib) {
        System.out.println("Enter Author name : ");
        Scanner sc = new Scanner(System.in);
        String author = sc.nextLine();
        var users = lib.UsersByAuthor(author);
        sc.close();
        PrintList(users);
        System.out.println(users.size());
    }

    public static void getEmailsOfUsers(Library lib) {
        System.out.println("Enter number of books lended : ");
        Scanner sc = new Scanner(System.in);
        int num = sc.nextInt();
        var emails = lib.getEmailsOfUserWithNumberOfBooks(num, true);
        sc.close();
        PrintList(emails);
    }

    public static void SortBooks(Library lib) {
        Scanner sc = new Scanner(System.in);
        System.out.println(
                    "Sort by\n"
                    + "  1 - Name\n"
                    + "  2 - Author\n"
                    + "  3 - Year\n"
                    + "  4 - Pages");
        
        List<Comparator<? super Book>> vars = Arrays.asList(
            Comparator.comparing(Book::getAuthor),
            Comparator.comparingInt(Book::getYear),
            Comparator.comparingInt(Book::getPages),
            Comparator.comparing(Book::getName)
        );
        List<Book> sortedBooks = lib.getSortedBooks(vars.get(sc.nextInt()-1));
        sc.close();
        PrintList(sortedBooks);
    }

    public static void PrintMap(Map<?, ?> map) {
        if(map.size() == 0) {
            System.out.println("<Void map>");
        }
        for (var obj : map.entrySet()) {
            System.out.println(obj.getKey() + " : " + obj.getValue());
        }
        System.out.println("\n");
    }

    public static void PrintList(List<?> lst) {
        if(lst.size() == 0) {
            System.out.println("<Void list>");
        }
        int counter = 0;
        for (var obj : lst) {
            System.out.println(++counter + ".  " + obj);
        }
        System.out.println("\n");
    }

    public static void FillData(Library lib) throws Exception {
        var books = ReadBooks("data\\books.json");
        var subs = ReadSubscriptions("data\\users.json");
        var lendings = ReadLendings("data\\lendings.json");
        lib.books.addAll(books);
        lib.users.addAll(subs);
        for(var lendperuser : lendings.entrySet()) {
            lib.users.stream().filter(user -> lendperuser.getKey().equals(user.subID))
                .findFirst().orElse(null).lendings.addAll(lendperuser.getValue());
        }
    }

    public static List<Book> ReadBooks(String path) throws Exception {
        String firstFile = Files.readString(Path.of(path));
        JSONArray array = new JSONArray(firstFile);

        ArrayList<Book> ret = new ArrayList<Book>();
        for (int i = 0; i < array.length(); i++) {
            JSONObject obj = array.getJSONObject(i);
            int id = obj.getInt("id");
            String name = obj.getString("name");
            String author = obj.getString("author");
            int year = obj.getInt("year");
            int pages = obj.getInt("pages");
            ret.add(new Book(id, name, author, year, pages));
        }
        return ret;
    }

    public static List<Subscription> ReadSubscriptions(String path) throws Exception {
        String firstFile = Files.readString(Path.of(path));
        JSONArray array = new JSONArray(firstFile);

        ArrayList<Subscription> ret = new ArrayList<Subscription>();
        for (int i = 0; i < array.length(); i++) {
            JSONObject obj = array.getJSONObject(i);
            int id = obj.getInt("id");
            String fname = obj.getString("fname");
            String mname = obj.getString("mname");
            String lname = obj.getString("lname");
            String email = obj.getString("email");
            ret.add(new Subscription(id, fname, mname, lname, email));
        }
        return ret;
    }

    public static Map<Integer, List<Lending>> ReadLendings(String path) throws Exception {
        String firstFile = Files.readString(Path.of(path));
        JSONArray array = new JSONArray(firstFile);

        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd-MM-yy");
        var ret = new HashMap<Integer, List<Lending>>();
        for (int i = 0; i < array.length(); i++) {
            JSONObject obj = array.getJSONObject(i);
            int user = obj.getInt("user");
            int book = obj.getInt("book");
            LocalDate taken = LocalDate.parse(obj.getString("taken"), fmt);
            LocalDate planned = LocalDate.parse(obj.getString("planned"), fmt);
            if(!ret.containsKey(user)) {
                ret.put(user, new ArrayList<Lending>());
            }
            ret.get(user).add(new Lending(book, taken, planned));
            if (obj.has("returned") && !obj.isNull("returned")) {
                var byuser = ret.get(user);
                byuser.get(byuser.size() - 1).ReturnedBook(LocalDate.parse(obj.getString("returned"), fmt));
            }
        }
        return ret;
    }
}