package read;

import exception.FileProcessingException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class Reader {
    private static final Logger logger = LogManager.getLogger(Reader.class);

    public static String readFile(String filename) throws FileProcessingException {
        logger.debug("Попытка чтения файла: {}", filename);

        if (filename == null || filename.trim().isEmpty()) {
            logger.error("Имя файла не может быть пустым");
            throw new FileProcessingException("Имя файла не может быть пустым");
        }

        File file = new File(filename);
        if (!file.exists()) {
            logger.error("Файл не существует: {}", filename);
            throw new FileProcessingException("Файл не существует: " + filename);
        }

        if (!file.canRead()) {
            logger.error("Нет прав на чтение файла: {}", filename);
            throw new FileProcessingException("Нет прав на чтение файла: " + filename);
        }

        StringBuilder content = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
            logger.info("Файл успешно прочитан: {}, размер: {} символов",
                    filename, content.length());
        } catch (IOException e) {
            logger.error("Ошибка при чтении файла: {}", filename, e);
            throw new FileProcessingException("Ошибка при чтении файла: " + filename, e);
        }

        return content.toString();
    }

}