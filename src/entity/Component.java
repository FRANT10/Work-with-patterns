package entity;

import processors.TextProcessor;

/**
 * Базовый интерфейс для всех компонентов текста
 */
public interface Component {
    String getText();
    void process(TextProcessor processor);
    int countElements();
}