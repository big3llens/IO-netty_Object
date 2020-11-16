package Simple_Client;

import Netty_Object_Server.Parcel;
import io.netty.handler.codec.serialization.ObjectDecoderInputStream;
import io.netty.handler.codec.serialization.ObjectEncoderOutputStream;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.net.Socket;
import java.util.Arrays;

public class Client extends JFrame {
    private JTextField text;
    private JTextArea chatArea;
    private Socket socket;
    //    private final InputStream in;
//    private final OutputStream out;
//    private ObjectInputStream objectIn;
//    private ObjectOutputStream objectOut;
    private ObjectEncoderOutputStream objectOut;
    private ObjectDecoderInputStream objectIn;
    private ObjectEncoderOutputStream out;
    private ObjectDecoderInputStream in;

    public Client() throws HeadlessException, IOException {
        socket = new Socket("localhost", 9000);
//        in = socket.getInputStream();
//        out = socket.getOutputStream();
        objectIn = new ObjectDecoderInputStream(new BufferedInputStream(socket.getInputStream()));
        objectOut = new ObjectEncoderOutputStream(new BufferedOutputStream(socket.getOutputStream()));
        prepareGui();

        new Thread(() -> {
            try {
                Parcel getParcel = (Parcel) objectIn.readObject();
                System.out.println(getParcel.toString());
                System.out.println("getter: " + getParcel.getMessage());
                chatArea.append(getParcel.getMessage() + "\n");
//                    objectIn.reset();
//                    objectIn.close();
                socket.getInputStream().mark(0);
                socket.getInputStream().reset();
            } catch (ClassNotFoundException | IOException e) {
                e.printStackTrace();
            }
        }).start();
    }
    private void sendMessage(String message) {
        try {
            objectOut.writeObject(new Parcel((byte) 10, message));
            objectOut.flush();
//            System.out.println(new Parcel((byte) 10, message).toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void getFile(String fileName) {

    }

    public void getMessage() {

    }


    private void sendFile(String filename) {

    }

//    public class Run implements Runnable {
//
//        @Override
//        public void run() {
//            while (true) {
//
//            }
//        }
//    }

    public static void main(String[] args) throws IOException {
        new Client();
    }

    public void prepareGui() {
        setBounds(600, 300, 500, 500);
        setTitle("Клиент");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        // Текстовое поле для вывода сообщений
        chatArea = new JTextArea();
        chatArea.setEditable(false);
        chatArea.setLineWrap(true);
        add(new JScrollPane(chatArea), BorderLayout.CENTER);

        // Нижняя панель с полем для ввода сообщений и кнопкой отправки сообщений
        JPanel bottomPanel = new JPanel(new BorderLayout());
        JButton sendMsg = new JButton("Отправить");
        bottomPanel.add(sendMsg, BorderLayout.EAST);
        text = new JTextField();
        add(bottomPanel, BorderLayout.SOUTH);
        bottomPanel.add(text, BorderLayout.CENTER);

        sendMsg.addActionListener(a -> {
            String[] cmd = text.getText().split(" ");
            if (cmd[0].equals("upload")) {
                sendFile(cmd[1]);
            }
            if (cmd[0].equals("download")) {
                getFile(cmd[1]);
            } else sendMessage(text.getText());
        });

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosed(e);
                sendMessage("exit");
            }
        });
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
    }
}
