package processors;

import entity.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.util.regex.*;

/**
 * Обработчик удаления подстрок
 */
public class SubstringRemovalProcessor extends TextProcessor {
    private static final Logger logger = LogManager.getLogger(SubstringRemovalProcessor.class);
    private final char startChar;
    private final char endChar;

    public SubstringRemovalProcessor(char startChar, char endChar) {
        this.startChar = startChar;
        this.endChar = endChar;
        logger.debug("Создан SubstringRemovalProcessor для символов: '{}' и '{}'",
                startChar, endChar);
    }

    @Override
    public void process(CompositeTool composite) {
        if (composite.getType() == CompositeTool.CompositeType.SENTENCE) {
            String sentenceText = composite.getText();
            logger.debug("Обработка предложения: {}", sentenceText);

            String processedText = removeMaxSubstring(sentenceText, startChar, endChar);

            if (!sentenceText.equals(processedText)) {
                logger.info("Удалена подстрока из предложения. Было: '{}', стало: '{}'",
                        sentenceText, processedText);

                rebuildSentence(composite, processedText);
            } else {
                logger.trace("Изменений в предложении не требуется");
            }
        }

        TextProcessor next = getNextProcessor();
        if (next != null) {
            next.process(composite);
        }
    }

    private String removeMaxSubstring(String text, char start, char end) {
        String startStr = String.valueOf(start);
        String endStr = String.valueOf(end);

        String patternStr = Pattern.quote(startStr) + ".*?" + Pattern.quote(endStr);
        Pattern pattern = Pattern.compile(patternStr);
        Matcher matcher = pattern.matcher(text);

        String maxSubstring = "";
        int maxLength = -1;

        while (matcher.find()) {
            String found = matcher.group();
            if (found.length() > maxLength) {
                maxLength = found.length();
                maxSubstring = found;
            }
        }

        if (maxLength > 0) {
            logger.debug("Найдена максимальная подстрока для удаления: '{}'", maxSubstring);
            return text.replace(maxSubstring, "");
        }

        return text;
    }

    private void rebuildSentence(CompositeTool sentenceComposite, String newText) {
        logger.debug("Перестройка предложения: {}", newText);

        sentenceComposite.clear();

        String[] words = newText.split("\\s+");
        for (String wordText : words) {
            if (wordText.isEmpty()) continue;

            CompositeTool wordComposite = new CompositeTool("word", CompositeTool.CompositeType.WORD);
            for (char c : wordText.toCharArray()) {
                if (Character.isLetterOrDigit(c)) {
                    wordComposite.add(new CharacterLeaf(c));
                } else {
                    wordComposite.add(new PunctuationLeaf(c));
                }
            }
            sentenceComposite.add(wordComposite);
        }
    }

    @Override
    public void process(CharacterLeaf character) {
        TextProcessor next = getNextProcessor();
        if (next != null) next.process(character);
    }

    @Override
    public void process(PunctuationLeaf punctuation) {
        TextProcessor next = getNextProcessor();
        if (next != null) next.process(punctuation);
    }
}