package JDBC;

import java.sql.*;
import java.util.Scanner;
import java.sql.Connection;
public class MovieDatabase {
	
	

    private static final String DB_URL = "jdbc:mysql://localhost:3306/moviedb";
    private static final String USER = "root";
    private static final String PASSWORD = "root";

    private Connection connection;
    private Scanner scanner;

    public MovieDatabase() {
        try {
            connection = DriverManager.getConnection(DB_URL, USER, PASSWORD);
            createTable();
            scanner = new Scanner(System.in);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        MovieDatabase movieDatabase = new MovieDatabase();
        movieDatabase.run();
    }

    private void createTable() {
        try (Statement statement = connection.createStatement()) {
            String createTableQuery = "CREATE TABLE IF NOT EXISTS movies (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY," +
                    "title VARCHAR(255) NOT NULL," +
                    "director VARCHAR(255) NOT NULL," +
                    "year INT NOT NULL)";
            statement.executeUpdate(createTableQuery);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void run() {
        int choice;
        do {
            System.out.println("1. Add new movie");
            System.out.println("2. Search for a movie");
            System.out.println("3. Display all movies");
            System.out.println("4. Exit");
            System.out.print("Enter your choice: ");
            choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline character

            switch (choice) {
                case 1:
                    addMovie();
                    break;
                case 2:
                    searchMovie();
                    break;
                case 3:
                    displayAllMovies();
                    break;
                case 4:
                    System.out.println("Exiting the program. Goodbye!");
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        } while (choice != 4);

        try {
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void addMovie() {
        try {
            System.out.print("Enter the movie title: ");
            String title = scanner.nextLine();

            System.out.print("Enter the director's name: ");
            String director = scanner.nextLine();

            System.out.print("Enter the release year: ");
            int year = scanner.nextInt();
            scanner.nextLine(); // Consume newline character

            String insertQuery = "INSERT INTO movies (title, director, year) VALUES (?, ?, ?)";
            try (PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)) {
                preparedStatement.setString(1, title);
                preparedStatement.setString(2, director);
                preparedStatement.setInt(3, year);
                preparedStatement.executeUpdate();
            }

            System.out.println("Movie added successfully!\n");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void searchMovie() {
        try {
            System.out.print("Enter the movie title to search: ");
            String searchTitle = scanner.nextLine();

            String searchQuery = "SELECT * FROM movies WHERE title LIKE ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(searchQuery)) {
                preparedStatement.setString(1, "%" + searchTitle + "%");
                ResultSet resultSet = preparedStatement.executeQuery();

                boolean found = false;
                while (resultSet.next()) {
                    System.out.println("Movie found!\n" +
                            "ID: " + resultSet.getInt("id") + "\n" +
                            "Title: " + resultSet.getString("title") + "\n" +
                            "Director: " + resultSet.getString("director") + "\n" +
                            "Year: " + resultSet.getInt("year") + "\n");
                    found = true;
                }

                if (!found) {
                    System.out.println("Movie not found!\n");
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void displayAllMovies() {
        try {
            String selectAllQuery = "SELECT * FROM movies";
            try (Statement statement = connection.createStatement();
                 ResultSet resultSet = statement.executeQuery(selectAllQuery)) {

                if (!resultSet.isBeforeFirst()) {
                    System.out.println("No movies in the database.\n");
                } else {
                    System.out.println("All movies in the database:\n");
                    while (resultSet.next()) {
                        System.out.println("ID: " + resultSet.getInt("id") + "\n" +
                                "Title: " + resultSet.getString("title") + "\n" +
                                "Director: " + resultSet.getString("director") + "\n" +
                                "Year: " + resultSet.getInt("year") + "\n");
                    }
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
