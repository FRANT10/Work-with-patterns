package processors;

import entity.*;

/**
 * Базовый класс для процессоров (Chain of Responsibility)
 */
public abstract class TextProcessor {
    private TextProcessor nextProcessor;

    public void setNext(TextProcessor nextProcessor) {
        this.nextProcessor = nextProcessor;
    }

    public TextProcessor getNextProcessor() {
        return nextProcessor;
    }

    public void process(CharacterLeaf character) {
        if (nextProcessor != null) {
            nextProcessor.process(character);
        }
    }

    public void process(PunctuationLeaf punctuation) {
        if (nextProcessor != null) {
            nextProcessor.process(punctuation);
        }
    }

    public void process(CompositeTool composite) {
        if (nextProcessor != null) {
            nextProcessor.process(composite);
        }
    }
}