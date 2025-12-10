package entity;

import processors.TextProcessor;

/**
 * Листовой класс для знаков препинания
 */
public class PunctuationLeaf extends TextLeaf {

    public PunctuationLeaf(char value) {
        super(value);
    }

    @Override
    public void process(TextProcessor processor) {
        processor.process(this);
    }
}