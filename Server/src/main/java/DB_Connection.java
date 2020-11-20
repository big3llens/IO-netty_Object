
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DB_Connection {
    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Драйвер не найден");
        }
    }
    public static Connection getConnection (){
        try {
            return DriverManager.getConnection("jdbc:mysql://localhost:3306/cloud_users?useUnicode=true&serverTimezone=UTC&useSSL=false", "root", "qwerty");
//                                                  jdbc:mysql://localhost:3306/?user=root   ?autoReconnect=true&useSSL=false   &useSSL=false
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return  null;
    }
}
