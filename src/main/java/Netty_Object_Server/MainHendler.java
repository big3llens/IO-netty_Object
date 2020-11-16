package Netty_Object_Server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.nio.file.Path;

public class MainHendler extends ChannelInboundHandlerAdapter {
    private Path rootPath = Path.of("server");
    private FileWorker fw = new FileWorker(rootPath);

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
//        ctx.write(new Parcel((byte) 10, "С подключением"));
//        System.out.println("Клиент подключился");
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        Parcel getParcel = (Parcel) msg;
        if (getParcel.getMessageType() == 20){

        }
        if (getParcel.getMessageType() == 10) {
            ctx.write(new Parcel((byte) 10, getParcel.getMessage()));
            System.out.println(getParcel.toString());
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
    }
}
