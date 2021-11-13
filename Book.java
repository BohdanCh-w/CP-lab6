public class Book {
    public int bookID;
    public String name;
    public String author;
    public int year;
    public int pages;

    public Book(int id, String name_, String author_, int year_, int pages_) {
        bookID = id;
        name = name_;
        author = author_;
        year = year_;
        pages = pages_;
    }

    public int getID() { return bookID; }
    public String getName() { return name; }
    public String getAuthor() { return author; }
    public int getYear() { return year; }
    public int getPages() { return pages; }

    public String toString() {
        return String.format("%6d: %-50s by %-20s %dy, %d pages.", bookID, name, author, year, pages);
    }
}
