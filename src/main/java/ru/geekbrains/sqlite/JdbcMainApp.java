package ru.geekbrains.sqlite;



import java.sql.*;

public class JdbcMainApp {
    private  static Connection connection; //  объявили соединение
    private static Statement stmt; // объявляем для создания запросов в бд
    private static PreparedStatement psInsert; //  преподготовленный  запрос, чтобы не было sql  инъекций, то есть это некая защита и небольшое ускорение


    public static void main(String[] args) {
        try {
            connect();
            dropAndCreateTable();
            fillTable();
//            prepareStatements(); //  такую защиту ставим после заполнения таблицы
//            preparedStatementExample();


        } catch (SQLException e) {
            e.printStackTrace();
        } finally { //  нельзя,чтобы бд осталась открытым, чтобы не тратить ресурсы бд
            disconnect();
        }
    }

    private static void preparedStatementExample() throws SQLException {
        connection.setAutoCommit(false); //  для того, чтобы сделать скорость быстрой:    чтобы jdbc отрубил коммит

        for (int i = 1; i <= 50; i++) {
            // insert into students (name, score) values (?,?)

            psInsert.setString(1, "Bob" + i);
            psInsert.setInt(2, 100);
            psInsert.executeUpdate();

        }
        connection.commit();
    }

    private static void prepareStatements () throws SQLException {
        // //  преподготовленный  запрос, чтобы не было sql  инъекций
        psInsert = connection.prepareStatement("insert into students (name, score) values (?,?);");

    }

    private static void batchExample() throws SQLException {
        //             добавление запроса пакетами в БД. Даже в этом случае включат setAutoCommit
        connection.setAutoCommit(false); //  для того, чтобы сделать скорость быстрой:    чтобы jdbc отрубил коммит
        for (int i = 1; i <= 50; i++) {
            //   1   Bob#1    100
            stmt.addBatch(String.format("insert into students (name,score) values ('%s',%d);", "Bob #" + i, 100));
        }
        stmt.executeBatch(); // executeBatch в этом случае возвращается массив интов
        connection.commit();
    }

    private static void dropAndCreateTable() throws SQLException {
        stmt.executeUpdate("drop table if exists students");// удалить таблицу, если она существует.
        stmt.executeUpdate("CREATE TABLE if not exists students (\n" +
                "    id    INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                "    name  TEXT,\n" +
                "    score INTEGER)");// создать таблицу, если она не существует.
    }

    //  это метод заполнения  таблицы
    private static void fillTable() throws SQLException {
        long time = System.currentTimeMillis();
        connection.setAutoCommit(false); //  для того, чтобы сделать скорость быстрой:    чтобы jdbc отрубил коммит

        for (int i = 1; i <= 50; i++) {

            //   1   Bob#1    100
            stmt.executeUpdate(String.format("insert into students (name,score) values ('%s',%d);", "Bob #" + i, 100));
        }
        connection.setAutoCommit(true); // или можно connection.commit
        System.out.println("TIME: " + (System.currentTimeMillis() - time));
    }

    private static void clearTableExample() throws SQLException {
        stmt.executeUpdate("delete from students;");
    }

    private static void deleteOneExample() throws SQLException {
        stmt.executeUpdate("delete from students where id = 12;");
    }

    private static void updateExample() throws SQLException {
        stmt.executeUpdate("update students set score = 100 where id > 0;");
    }



    // метод чтения, в результате выполнения запроса на чтение вернется результат, поэтому его сохраняем
    private static void readExample() throws SQLException {
        try (ResultSet rs = stmt.executeQuery("select * from students where id > 2")) {
            while (rs.next()) {
                // rs. next =  двигает курсор
                //  id    name  score
                //  1      Bob    90
                System.out.println(rs.getInt(1) + " " + rs.getString(2) + " " + rs.getInt(3));
            }
        }
    }



    private static void insertExample() throws SQLException { // добавление  записи в бд
        stmt.executeUpdate("insert into students (name, score) values ('Max', 90);");
    }


    public static void connect () {
        try {
            Class.forName("org.sqlite.JDBC"); //  загрузка драйвера в память ->  срабатывает классический блок инициализации -> и он себя зарегистрировал в драйвер менеджере
            connection = DriverManager.getConnection("jdbc:sqlite:mydatabase.db"); // открытие соединения
            stmt = connection.createStatement(); //  создаем Statement

        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Невозможно подключиться к БД");
        }
    }

    public static void disconnect (){ // закрывается в том же порядке,  в котором открывался
        try {
            if (stmt != null) {
                stmt.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            if (psInsert != null){
                psInsert.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if(connection != null) { //  проверка обязательна: а не null  ли то,что мы закрываем
            try {
                connection.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
    }



}
