package entity;

import processors.TextProcessor;

/**
 * Листовой класс для букв и цифр
 */
public class CharacterLeaf extends TextLeaf {

    public CharacterLeaf(char value) {
        super(value);
    }

    @Override
    public void process(TextProcessor processor) {
        processor.process(this);
    }
}