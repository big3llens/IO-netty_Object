
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class MainHendler extends ChannelInboundHandlerAdapter {
    private final Path rootPath = Path.of("Server/server_disk");
    private final FileWorker fw = new FileWorker(rootPath);
    private Path newFile;
    private FileOutputStream fos;
    private FileInputStream fis;
    private User user;
    private static List <User> users = new ArrayList<>();

    public MainHendler(User user){
        this.user = user;
        users.add(user);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ctx.write(new Parcel((byte) 10, "С подключением"));
        System.out.println("Клиент подключился");
    }


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        Parcel parcel = (Parcel) msg;
        String s;

        switch (parcel.getMessageType()){
            case 10:
                ctx.writeAndFlush(new Parcel((byte) 10, "[" + user.getName() + "]:" + "[" + fw.getCurrentPath() + "]~" + parcel.getMessage()));
                System.out.println(parcel.toString());
                break;
            case 11:
                ctx.write(new Parcel((byte) 10, "[" + user.getName() + "]:" + "[" + fw.getCurrentPath() + "]~" + fw.getFilesList())); break;
            case 12:
                System.out.println(parcel.toString());
                System.out.println(fw.getCurrentPath().toString());
                s = fw.changeDirectory(parcel.getMessage());
                System.out.println(s);
                if (s.equals("Такой директории не существует")){
                    ctx.write(new Parcel((byte) 10, "[" + user.getName() + "]:" + "[" + fw.getCurrentPath() + "]~" + "Такой директории не существует")); return;
                } else {
                    ctx.write(new Parcel((byte) 10, "[" + user.getName() + "]:" + "[" + fw.getCurrentPath() + "]~")); return;
                }
            case 13:
                fw.makedirectory(parcel.getMessage()); break;
            case 14:
                s = fw.touch(parcel.getMessage());
                ctx.write(new Parcel((byte) 10, "[" + user.getName() + "]:" + "[" + fw.getCurrentPath() + "]~" + s));break;
            case 15:
                s = fw.remove(parcel.getMessage());
                ctx.write(new Parcel((byte) 10, "[" + user.getName() + "]:" + "[" + fw.getCurrentPath() + "]~" + s));break;
            case 16:
                String[] arrStr = parcel.getMessage().split(" ");
                fw.cope(arrStr[0], arrStr[1]); break;
            case 17:
                ctx.write(new Parcel((byte) 10, "[" + user.getName() + "]:" + "[" + fw.getCurrentPath() + "]~" + "\n" + fw.cat(parcel.getMessage())));break;
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
