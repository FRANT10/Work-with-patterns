package processors;

import entity.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Обработчик пробелов и табуляций
 */
public class WhitespaceProcessor extends TextProcessor {
    private static final Logger logger = LogManager.getLogger(WhitespaceProcessor.class);
    private boolean previousWasWhitespace = false;

    @Override
    public void process(CharacterLeaf character) {
        char value = character.getValue();

        if (value == ' ' || value == '\t') {
            if (!previousWasWhitespace) {
                logger.debug("Замена табуляции/пробела на одиночный пробел");
                previousWasWhitespace = true;

                TextProcessor next = getNextProcessor();
                if (next != null) {
                    next.process(new CharacterLeaf(' '));
                }
            } else {
                logger.trace("Пропуск лишнего пробела");
            }
        } else {
            previousWasWhitespace = false;

            TextProcessor next = getNextProcessor();
            if (next != null) {
                next.process(character);
            }
        }
    }

    @Override
    public void process(CompositeTool composite) {
        TextProcessor next = getNextProcessor();
        if (next != null) {
            next.process(composite);
        }
    }

    @Override
    public void process(PunctuationLeaf punctuation) {
        TextProcessor next = getNextProcessor();
        if (next != null) {
            next.process(punctuation);
        }
    }
}