import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AuthService {
    public boolean findUsers (String login, String pass){
        Connection connection = DB_Connection.getConnection();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM users WHERE users_login = ? AND users_pass = ?");
            preparedStatement.setString(1, login);
            preparedStatement.setString(2, pass);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()){
                if (resultSet.getString("users_login").equals(login)&resultSet.getString("users_pass").equals(pass)){
                    return true;
                }
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return false;
    }
}
