
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;

public class FileWorker {
    private Path rootPath;
    private Path currentPath;

    public FileWorker(Path p) {
        rootPath = p;
        currentPath = rootPath;
    }

    public Path getCurrentPath() {
        return currentPath;
    }

    public void setCurrentPath(Path currentPath) {
        this.currentPath = currentPath;
    }

    public String changeDirectory(String dir) throws IOException {
        Path newDir;
        StringBuilder sb = new StringBuilder();
        if (dir.equals("../")){
            currentPath = rootPath;
            return currentPath.toString();
        }
        if (dir.equals("./")){
            if (!currentPath.toString().equals(rootPath.toString())){
                if (currentPath.getNameCount() > 2){
                    for (int i = 0; i < currentPath.getNameCount() - 1; i++) {
                        if (i == currentPath.getNameCount() - 2) {
                            sb.append(currentPath.getName(i));
                            break;
                        }
                        sb.append(currentPath.getName(i) + "/");
                    }
                    currentPath = Path.of(sb.toString());
                    return sb.toString();
                }
                else {
                    currentPath = rootPath;
                    return currentPath.toString();
                }
            } else {
                return currentPath.toString();
            }
        }
        String[] arrDir = dir.split("/");
        if (arrDir.length < 2) {
            if (!Files.exists(Path.of(currentPath.toString() + "/" + dir))) {
                return "Такой директории не существует";
            }
            currentPath = Path.of(currentPath.toString() + "/" + dir);
            return currentPath.toString();
        }
        newDir = Path.of(dir);
        if (!Files.exists(newDir)) {
            System.out.println("Такой директории не существует");
            return "Такой директории не существует";
        }
        if (!Files.isDirectory(newDir)) {
            for (int i = 0; i < newDir.getNameCount() - 1; i++) {
                sb.append(newDir.getName(i) + "/");
            }
            currentPath = Path.of(sb.toString());
            return currentPath.toString();
        }
        currentPath = Path.of(newDir.toString());
        return currentPath.toString();
    }

    public String touch(String name) throws IOException {
        if (!Files.exists(Path.of(currentPath.toString(), name + ".txt"))) {
            Files.createFile(Path.of(currentPath.toString(), name + ".txt"));
            return "Файл " + name + " успешно создан";
        } else return "Такой файл уже существует";
    }

    public void makedirectory(String name) throws IOException {
        if (!Files.exists(Path.of(currentPath.toString() + "/" + name))) {
            Files.createDirectory(Path.of(currentPath.toString() + "/" + name));
        } else
            System.out.println("Такая директория уже существует");
    }

    public String remove(String name) throws IOException {
        List<String> filesList = Arrays.asList(currentPath.toFile().list());
        if (filesList == null) {
            return "В данной директории нет файлов";
        }
        for (String file : filesList) {
            if (file.equals(name)) {
                Files.delete(Path.of(currentPath.toString(), file));
                return String.format("Файл %s успешно удален", name);
            }
        }
        return "В данной директории нет такого файла";
    }

    public String cope(String src, String target) throws IOException {
        Files.copy(Path.of(currentPath.toString(), src), Path.of(target), StandardCopyOption.REPLACE_EXISTING);
        return "Файл скопирован";
    }

    public String cat(String name) throws IOException {
        if (Files.exists(Path.of(currentPath.toString(), name))) {
            if (Files.readString(Path.of(currentPath.toString(), name)) == null) return "Файл пустой";
            return Files.readString(Path.of(currentPath.toString(), name));
        }
        return "В данной директории нет такого файла";
    }
    public String getFilesList() {
        return String.join(" ", currentPath.toFile().list());
    }
}

