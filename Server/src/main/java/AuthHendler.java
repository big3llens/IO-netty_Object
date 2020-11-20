
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AuthHendler extends ChannelInboundHandlerAdapter {
    private boolean authOk;
    private Parcel authParcel;
    private AuthService authService;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ctx.writeAndFlush(new Parcel((byte)10, "Добро пожаловать в файловое хранилище, введите пожалуйста свой логин и пароль в формате: /auth login password"));
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        authService = new AuthService();
        authParcel = (Parcel)msg;
        System.out.println(authParcel.toString());
        if (authParcel.getMessageType() == 9) {
            String[] authLoginPass = authParcel.getMessage().split(" ");
            authOk = authService.findUsers(authLoginPass[0], authLoginPass[1]);
            if (authOk) {
                System.out.println("Клиент авторизовался");
                ctx.writeAndFlush(new Parcel((byte) 10, "С подключением"));
                ctx.pipeline().addLast(new MainHendler(new User(authLoginPass[0], ctx.channel())));
                ctx.pipeline().remove(this);
            } else {
                ctx.writeAndFlush(new Parcel((byte) 10, "Вы ввели неверные данные, попробуйте еще раз"));
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
    }
}
