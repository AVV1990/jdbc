package ru.geekbrains.sqlite;

/* CREATE TABLE students (
    id    INTEGER PRIMARY KEY AUTOINCREMENT,
    name  TEXT,
    score INTEGER
);


 */


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class JdbcMainApp {
    private  static Connection connection; //  объявили соединение


    public static void main(String[] args) {
        connect();


    }


    public static void connect () {
        try {
            Class.forName("org.sqlite.JDBC"); //  загрузка драйвера в память ->  срабатывает классический блок инициализации -> и он себя зарегистрировал в драйвер менеджере
            connection = DriverManager.getConnection("jdbc:sqlite:mydatabase.db"); // открытие соединения
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Невозможно подключиться к БД");
        }
    }



}
