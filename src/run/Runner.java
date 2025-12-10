package run;

import entity.Component;
import exception.FileProcessingException;
import exception.ParsingException;
import parse.TextParser;
import processors.SubstringRemovalProcessor;
import processors.WhitespaceProcessor;
import read.Reader;
import write.Writer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Scanner;

/**
 * Класс для запуска и управления обработкой текста
 */
public class Runner {
    private static final Logger logger = LogManager.getLogger(Runner.class);
    private final Scanner scanner;

    public Runner() {
        this.scanner = new Scanner(System.in);
        logger.info("Инициализирован Runner");
    }

    public void processText() {
        try {
            logger.info("=== Запуск программы обработки текста ===");

            String inputFilename = "resources/input.txt";
            String text = Reader.readFile(inputFilename);

            printText("Исходный текст из файла:", text);

            logger.info("Парсинг текста...");
            TextParser parser = new TextParser();
            Component document = parser.parse(text);

            char startChar = getStartChar();
            char endChar = getEndChar();

            logger.info("Создание цепочки обработчиков...");
            SubstringRemovalProcessor removalProcessor = new SubstringRemovalProcessor(startChar, endChar);
            WhitespaceProcessor whitespaceProcessor = new WhitespaceProcessor();

            removalProcessor.setNext(whitespaceProcessor);

            logger.info("Обработка документа...");
            document.process(removalProcessor);

            String processedText = document.getText();

            printText("Обработанный текст:", processedText);

            String outputFilename = "resources/output.txt";
            Writer.writeFile(outputFilename, processedText);

            printStatistics(text, processedText, inputFilename, outputFilename);

            logger.info("=== Обработка завершена успешно ===");

        } catch (FileProcessingException | ParsingException e) {
            logger.error("Критическая ошибка при обработки текста", e);
            System.err.println("Ошибка: " + e.getMessage());
            System.err.println("Убедитесь, что файл resources/input.txt существует в директории проекта.");
        } catch (Exception e) {
            logger.error("Неожиданная ошибка", e);
            System.err.println("Произошла непредвиденная ошибка: " + e.getMessage());
        } finally {
            scanner.close();
            logger.info("Runner завершил работу");
        }
    }

    /**
     * Получение начального символа для удаления подстроки
     */
    private char getStartChar() {
        System.out.print("\nВведите начальный символ для удаления подстроки (оставьте пустым для '('): ");
        String input = scanner.nextLine().trim();

        if (input.isEmpty()) {
            System.out.println("Используется символ по умолчанию: '('");
            return '(';
        }

        return input.charAt(0);
    }

    /**
     * Получение конечного символа для удаления подстроки
     */
    private char getEndChar() {
        System.out.print("Введите конечный символ для удаления подстроки (оставьте пустым для ')'): ");
        String input = scanner.nextLine().trim();

        if (input.isEmpty()) {
            System.out.println("Используется символ по умолчанию: ')'");
            return ')';
        }

        return input.charAt(0);
    }

    /**
     * Печать текста с заголовком
     */
    private void printText(String title, String text) {
        System.out.println("\n" + title);
        System.out.println("=".repeat(60));
        System.out.println(text);
        System.out.println("=".repeat(60));
    }

    /**
     * Вывод статистики обработки
     */
    private void printStatistics(String originalText, String processedText, String inputFile, String outputFile) {
        System.out.println("\n=== Статистика обработки ===");
        System.out.printf("Входной файл: %s%n", inputFile);
        System.out.printf("Выходной файл: %s%n", outputFile);
        System.out.printf("Длина исходного текста: %d символов%n", originalText.length());
        System.out.printf("Длина обработанного текста: %d символов%n", processedText.length());
        System.out.printf("Удалено символов: %d%n", originalText.length() - processedText.length());

        // Подсчет абзацев
        int originalParagraphs = originalText.split("\n\n").length;
        int processedParagraphs = processedText.split("\n\n").length;
        System.out.printf("Абзацев исходных/обработанных: %d/%d%n", originalParagraphs, processedParagraphs);

        // Подсчет предложений (простой способ)
        int originalSentences = countSentences(originalText);
        int processedSentences = countSentences(processedText);
        System.out.printf("Предложений исходных/обработанных: %d/%d%n", originalSentences, processedSentences);

        // Подсчет слов
        int originalWords = countWords(originalText);
        int processedWords = countWords(processedText);
        System.out.printf("Слов исходных/обработанных: %d/%d%n", originalWords, processedWords);

        System.out.println("=".repeat(50));
    }

    /**
     * Подсчет предложений в тексте
     */
    private int countSentences(String text) {
        if (text == null || text.trim().isEmpty()) {
            return 0;
        }

        String[] sentences = text.split("[.!?]+\\s*");
        return sentences.length;
    }

    /**
     * Подсчет слов в тексте
     */
    private int countWords(String text) {
        if (text == null || text.trim().isEmpty()) {
            return 0;
        }

        String[] words = text.split("\\s+");
        return words.length;
    }
}