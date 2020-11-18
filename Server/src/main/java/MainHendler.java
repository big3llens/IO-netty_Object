
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class MainHendler extends ChannelInboundHandlerAdapter {
    private final Path rootPath = Path.of("server");
    private FileWorker fw = new FileWorker(rootPath);
    private Path newFile;
    private FileOutputStream fos;
    private FileInputStream fis;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ctx.writeAndFlush(new Parcel((byte) 10, "С подключением"));
        System.out.println("Клиент подключился");
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        Parcel parcel = (Parcel) msg;
        switch (parcel.getMessageType()){
            case 10:
                ctx.write(new Parcel((byte) 10, parcel.getMessage()));
                System.out.println(parcel.toString());
                break;
            case 20:
                if (!Files.exists(Path.of(fw.getCurrentPath().toString() + "/" + parcel.getNameFile()))){
                    ctx.write(new Parcel((byte)10, "Файла: " + "[" + parcel.getNameFile() + "]" + " в директории: " + "[" + fw.getCurrentPath() + "]" + " нет"));
                }
                newFile = Path.of(fw.getCurrentPath().toString() + "/" + parcel.getNameFile());
                fis = new FileInputStream(newFile.toFile());
                if (newFile.toFile().length() < 2000000000){
                    byte[] bytes = new byte[(int)newFile.toFile().length()];
                    fis.read(bytes, 0, bytes.length);
                    ctx.write(new Parcel((byte)20, newFile.toFile().getName(), bytes));
                }
                break;
            case 30:
                if (!Files.exists(Path.of(fw.getCurrentPath() + "/" + parcel.getNameFile()))) {
                    Files.createFile(Path.of(fw.getCurrentPath() + "/" + parcel.getNameFile()));
                }
                newFile = Path.of(fw.getCurrentPath() + "/" +parcel.getNameFile());
                System.out.println(fw.getCurrentPath());
                System.out.println(newFile.toString());
                System.out.println(newFile.toAbsolutePath());
                fos = new FileOutputStream(newFile.toFile());
                fos.write(parcel.getFileContents());
                fos.flush();
                break;
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
