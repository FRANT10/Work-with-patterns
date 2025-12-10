package entity;

import processors.TextProcessor;

/**
 * Абстрактный класс для листовых компонентов
 */
public abstract class TextLeaf implements Component {
    private final char value;

    public TextLeaf(char value) {
        this.value = value;
    }

    public char getValue() {
        return value;
    }

    @Override
    public String getText() {
        return String.valueOf(value);
    }

    @Override
    public int countElements() {
        return 1;
    }

    @Override
    public abstract void process(TextProcessor processor);

    @Override
    public String toString() {
        return String.format("%s{value='%s'}",
                getClass().getSimpleName(), value);
    }
}