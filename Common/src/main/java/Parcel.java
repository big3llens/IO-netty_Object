
import java.io.Serializable;
import java.util.Arrays;

public class Parcel implements Serializable {
    /* 10- отправка обычного сообщение, 11- команда ls, 12- команда cd, 13- команда mkdir, 14- команда touch,
    15- команда rm, 16- команда cope, 17- команда cat, 20- download, 30- upload
    */
    private byte messageType;
    private String message;
    private String nameFile;
    private byte[] fileContents;

    public Parcel(){
    }

    public Parcel(byte messageType, String message){
        this.messageType = messageType;
        this.message = message;
    }

    public Parcel(byte messageType, String message, String nameFile){
        this.messageType = messageType;
        this.message = message;
        this.nameFile = nameFile;
    }

    public Parcel(byte messageType, String nameFile, byte[] fileContents){
        this.messageType = messageType;
        this.nameFile = nameFile;
        this.fileContents = fileContents;
    }

    public Parcel(byte messageType, String message, String nameFile, byte[] fileContents){
        this.messageType = messageType;
        this.message = message;
        this.nameFile = nameFile;
        this.fileContents = fileContents;
    }

    @Override
    public String toString() {
        return "Parcel{" +
                "messageType=" + messageType +
                ", message='" + message + '\'' +
                ", nameFile='" + nameFile + '\'' +
                ", fileContents=" + Arrays.toString(fileContents) +
                '}';
    }

    public byte getMessageType() {
        return messageType;
    }

    public String getMessage() {
        return message;
    }

    public String getNameFile() {
        return nameFile;
    }

    public byte[] getFileContents() {
        return fileContents;
    }
}
