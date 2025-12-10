package write;

import exception.FileProcessingException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class Writer {
    private static final Logger logger = LogManager.getLogger(Writer.class);
    public static void writeFile(String filename, String content) throws FileProcessingException {
        logger.debug("Попытка записи в файл: {}", filename);

        if (filename == null || filename.trim().isEmpty()) {
            logger.error("Имя файла не может быть пустым");
            throw new FileProcessingException("Имя файла не может быть пустым");
        }

        if (content == null) {
            logger.warn("Попытка записи null содержимого в файл: {}", filename);
            content = "";
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            writer.write(content);
            logger.info("Файл успешно записан: {}, размер: {} символов",
                    filename, content.length());
        } catch (IOException e) {
            logger.error("Ошибка при записи в файл: {}", filename, e);
            throw new FileProcessingException("Ошибка при записи в файл: " + filename, e);
        }
    }
}

