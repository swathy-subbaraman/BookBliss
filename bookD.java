import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
class bookD{
    String bid;
    String bookName;
    String author;
    double price;
    boolean availability;
    bookD books[] = new bookD[10];
    String url = "jdbc:mysql://localhost:3306/bookstore";
    String user = "root";
    String password = "root"; // Replace with your MySQL password

    public void fetchBooks() {
        try {
            Connection connection = DriverManager.getConnection(url, user, password);
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM books");

            int index = 0;
            while (resultSet.next() && index < 10) {
                bookD book = new bookD();
                book.bid=String.valueOf(resultSet.getInt("book_id"));
                book.bookName = resultSet.getString("name");
                book.author = resultSet.getString("author");
                book.price = resultSet.getDouble("price");
                book.availability = resultSet.getInt("num_of_books_available") > 0;

                books[index] = book;
                index++;
            }

            resultSet.close();
            statement.close();
            connection.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
