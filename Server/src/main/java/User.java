
import io.netty.channel.Channel;

public class User {
    private String name;
    private String pass;
    private Channel channel;

    public User (String name, Channel channel){
        this.name = name;
        this.channel = channel;
    }

    public String getPass() {
        return pass;
    }

    public String getName() {
        return name;
    }

//    @Override
//    public String toString() {
//        return "User{" +
//                "name='" + name + '\'' +
//                ", pass='" + pass + '\'' +
//                ", channel=" + channel +
//                '}';
//    }
}
