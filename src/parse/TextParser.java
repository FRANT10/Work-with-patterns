package parse;

import entity.*;
import exception.ParsingException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Парсер текста
 */
public class TextParser {
    private static final Logger logger = LogManager.getLogger(TextParser.class);

    private static final int RED_LINE_INDENT = 4;

    private static final boolean USE_TABS = true;

    public Component parse(String text) throws ParsingException {
        logger.debug("Начало парсинга текста");

        if (text == null || text.trim().isEmpty()) {
            logger.warn("Попытка парсинга пустого текста");
            return new CompositeTool("empty", CompositeTool.CompositeType.DOCUMENT);
        }

        CompositeTool document = new CompositeTool("document", CompositeTool.CompositeType.DOCUMENT);

        try {
            String[] lines = text.split("\n");
            logger.debug("Найдено строк: {}", lines.length);

            List<String> currentParagraphLines = new ArrayList<>();
            int paragraphCount = 0;
            boolean inParagraph = false;

            for (String line : lines) {
                boolean isRedLine = isRedLine(line);

                if (isRedLine) {
                    if (inParagraph && !currentParagraphLines.isEmpty()) {
                        String paragraphText = String.join("\n", currentParagraphLines);
                        CompositeTool paragraph = parseParagraph(
                                "paragraph_" + paragraphCount++,
                                paragraphText
                        );
                        document.add(paragraph);
                        currentParagraphLines.clear();
                        logger.debug("Завершен абзац #{}, строк: {}",
                                paragraphCount-1, paragraph.getComponents().size());
                    }

                    inParagraph = true;
                    currentParagraphLines.add(line);
                    logger.trace("Начало нового абзаца (красная строка): {}", line);
                }
                else if (inParagraph && !line.trim().isEmpty()) {
                    currentParagraphLines.add(line);
                    logger.trace("Продолжение абзаца: {}", line);
                }
                else if (inParagraph && line.trim().isEmpty()) {
                    currentParagraphLines.add("");
                    logger.trace("Пустая строка внутри абзаца");
                }
                else if (!inParagraph && !line.trim().isEmpty()) {
                    inParagraph = true;
                    currentParagraphLines.add(line);
                    logger.debug("Начало первого абзаца: {}", line);
                }
                else if (!inParagraph && line.trim().isEmpty()) {
                    logger.trace("Пустая строка до первого абзаца");
                }
            }

            if (!currentParagraphLines.isEmpty()) {
                String paragraphText = String.join("\n", currentParagraphLines);
                CompositeTool paragraph = parseParagraph(
                        "paragraph_" + paragraphCount,
                        paragraphText
                );
                document.add(paragraph);
                logger.debug("Завершен последний абзац #{}, строк: {}",
                        paragraphCount, paragraph.getComponents().size());
            }

            logger.info("Парсинг завершен успешно. Создано абзацев: {}, элементов: {}",
                    document.getComponents().size(), document.countElements());
            return document;

        } catch (Exception e) {
            logger.error("Ошибка при парсинге текста", e);
            throw new ParsingException("Ошибка при парсинге текста", e);
        }
    }

    /**
     * Определяет, является ли строка красной строкой
     */
    private boolean isRedLine(String line) {
        if (line == null || line.isEmpty()) {
            return false;
        }

        if (line.startsWith(" ")) {
            int spaceCount = 0;
            while (spaceCount < line.length() && line.charAt(spaceCount) == ' ') {
                spaceCount++;
            }
            return spaceCount == RED_LINE_INDENT;
        }

        if (USE_TABS && line.startsWith("\t")) {
            logger.trace("Строка с табуляцией - красная строка: {}",
                    line.substring(0, Math.min(10, line.length())) + "...");
            return true;
        }

        return false;
    }

    private CompositeTool parseParagraph(String name, String paragraphText) {
        logger.debug("Парсинг абзаца '{}': {} символов", name, paragraphText.length());
        CompositeTool paragraph = new CompositeTool(name, CompositeTool.CompositeType.PARAGRAPH);

        String trimmedText = paragraphText.trim();

        String[] sentences = trimmedText.split("(?<=[.!?])\\s+");
        logger.debug("Абзац '{}': найдено предложений: {}", name, sentences.length);

        for (int i = 0; i < sentences.length; i++) {
            String sentenceText = sentences[i];
            if (sentenceText.trim().isEmpty()) continue;

            CompositeTool sentence = parseSentence("sentence_" + i, sentenceText.trim());
            paragraph.add(sentence);
        }

        return paragraph;
    }

    private CompositeTool parseSentence(String name, String sentenceText) {
        logger.trace("Парсинг предложения '{}': {}", name, sentenceText);
        CompositeTool sentence = new CompositeTool(name, CompositeTool.CompositeType.SENTENCE);

        String[] words = sentenceText.split("\\s+");

        for (int i = 0; i < words.length; i++) {
            String wordText = words[i];
            if (wordText.isEmpty()) continue;

            CompositeTool word = parseWord("word_" + i, wordText);
            sentence.add(word);
        }

        return sentence;
    }

    private CompositeTool parseWord(String name, String wordText) {
        logger.trace("Парсинг слова '{}': {}", name, wordText);
        CompositeTool word = new CompositeTool(name, CompositeTool.CompositeType.WORD);

        for (int i = 0; i < wordText.length(); i++) {
            char c = wordText.charAt(i);
            if (Character.isLetterOrDigit(c)) {
                word.add(new CharacterLeaf(c));
            } else {
                word.add(new PunctuationLeaf(c));
            }
        }

        return word;
    }
}