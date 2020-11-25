
import io.netty.handler.codec.serialization.ObjectDecoderInputStream;
import io.netty.handler.codec.serialization.ObjectEncoderOutputStream;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

public class Client extends JFrame {
    private JTextField text;
    private JTextArea chatArea;
    private final ObjectEncoderOutputStream objectOut;
    private final ObjectDecoderInputStream objectIn;
    Path rootPath = Path.of("Client/client_disk");

    public Client() throws HeadlessException, IOException {
        Socket socket = new Socket("localhost", 9000);
        objectIn = new ObjectDecoderInputStream(new BufferedInputStream(socket.getInputStream()));
        objectOut = new ObjectEncoderOutputStream(new BufferedOutputStream(socket.getOutputStream()));
        prepareGui();
        new Thread(() -> {
            while (true) {
                try {
                    Parcel getParcel = (Parcel) objectIn.readObject();
                    if (getParcel.getMessageType() == 10) {
                        chatArea.append(getParcel.getMessage() + "\n");
                    }
                    if (getParcel.getMessageType() == 20) {
                        if (!Files.exists(Path.of(rootPath.toString() + "/" + getParcel.getNameFile()))) {
                            Files.createFile(Path.of(rootPath.toString() + "/" + getParcel.getNameFile()));
                        }
                        Path newFile = Path.of(rootPath.toString() + "/" + getParcel.getNameFile());
                        FileOutputStream fos = new FileOutputStream(newFile.toFile());
                        fos.write(getParcel.getFileContents());
                        fos.flush();
                    }

                } catch (ClassNotFoundException | IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void sendMessage(byte command, String message) {
        try {
            objectOut.writeObject(new Parcel(command, message));
            objectOut.flush();
            text.setText("");
            text.grabFocus();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendFile(String filename) {
        if (!Files.exists(Path.of(rootPath.toString() + "/" + filename))) {
            chatArea.append("Такого файла не существует\n");
        }
        File file = new File(rootPath.toString() + "/" + filename);
        System.out.println(file.getName());
        FileInputStream fin = null;
        try {
            fin = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            System.out.println(fin.available());
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println((int) file.length());
        int read = 0;
        if (file.length() < 2000000000) {
            byte[] bytes = new byte[(int) file.length()];
            System.out.println(file.length());
            try {
                bytes = fin.readNBytes((int) file.length());
            } catch (IOException e) {
                e.printStackTrace();
            }
//            fin.read(bytes, 0, bytes.length);
//            System.out.println(new Parcel((byte)30, file.getName(), bytes).toString());
            try {
                objectOut.writeObject(new Parcel((byte) 30, file.getName(), bytes));
                objectOut.flush();
                text.setText("");
                text.grabFocus();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void getFile(String fileName) {
        try {
            objectOut.writeObject(new Parcel((byte) 20, "", fileName));
            objectOut.flush();
            text.setText("");
            text.grabFocus();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void getMessage() {

    }

    public void prepareGui() {
        setBounds(800, 400, 500, 500);
        setTitle("Клиент");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        // Текстовое поле для вывода сообщений
        chatArea = new JTextArea();
        chatArea.setEditable(false);
        chatArea.setLineWrap(true);
        add(new JScrollPane(chatArea), BorderLayout.CENTER);

        // Нижняя панель с полем для ввода сообщений и кнопкой отправки сообщений
        JPanel bottomPanel = new JPanel(new BorderLayout());
//        JButton sendMsg = new JButton("Отправить");
//        bottomPanel.add(sendMsg, BorderLayout.EAST);
        text = new JTextField();
        Font f = new Font("TimesRoman", Font.PLAIN, 14);
        chatArea.setFont(f);
        add(bottomPanel, BorderLayout.SOUTH);
        bottomPanel.add(text, BorderLayout.CENTER);

        text.addActionListener(a -> {
            String[] cmd = text.getText().split(" ");
            switch (cmd[0]) {
                case "upload":
                    sendFile(cmd[1]);
                    break;
                case "download":
                    getFile(cmd[1]);
                    break;
                case "/auth":
                    sendMessage((byte) 9, cmd[1] + " " + cmd[2]);
                    break;
                case "ls":
                    sendMessage((byte) 11, "");
                    break;
                case "cd":
                    sendMessage((byte) 12, cmd[1]);
                    break;
                case "mkdir":
                    sendMessage((byte) 13, cmd[1]);
                    break;
                case "touch":
                    sendMessage((byte) 14, cmd[1]);
                    break;
                case "rm":
                    sendMessage((byte) 15, cmd[1]);
                    break;
                case "cope":
                    sendMessage((byte) 16, cmd[1]);
                    break;
                case "cat":
                    sendMessage((byte) 17, cmd[1]);
                    break;
                default:
                    sendMessage((byte) 10, text.getText());

            }
        });

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosed(e);
                sendMessage((byte) 10, "exit");
            }
        });

        setDefaultCloseOperation(EXIT_ON_CLOSE);

        setVisible(true);
    }

    public static void main(String[] args) throws IOException {
        new Client();
    }
}
