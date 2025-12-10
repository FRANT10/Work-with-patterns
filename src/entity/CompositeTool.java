package entity;

import processors.TextProcessor;
import java.util.ArrayList;
import java.util.List;

/**
 * Составной компонент - может содержать другие компоненты
 */
public class CompositeTool implements Component {
    private final List<Component> components = new ArrayList<>();
    private final String name;
    private final CompositeType type;

    public enum CompositeType {
        DOCUMENT, PARAGRAPH, SENTENCE, WORD, LEXEME
    }

    public CompositeTool(String name, CompositeType type) {
        this.name = name;
        this.type = type;
    }

    public void add(Component component) {
        components.add(component);
    }

    public void remove(Component component) {
        components.remove(component);
    }

    public Component getChild(int index) {
        return components.get(index);
    }

    public List<Component> getComponents() {
        return new ArrayList<>(components);
    }

    public void clear() {
        components.clear();
    }

    public CompositeType getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    @Override
    public String getText() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < components.size(); i++) {
            Component component = components.get(i);
            sb.append(component.getText());

            if (i < components.size() - 1) {
                switch (type) {
                    case DOCUMENT:
                        sb.append("\n\n");
                        break;
                    case PARAGRAPH:
                        sb.append("\n");
                        break;
                    case SENTENCE:
                        sb.append(" ");
                        break;
                    case WORD, LEXEME:
                        break;
                }
            }
        }
        return sb.toString();
    }

    @Override
    public void process(TextProcessor processor) {
        processor.process(this);

        for (Component component : components) {
            component.process(processor);
        }
    }

    @Override
    public int countElements() {
        int count = 0;
        for (Component component : components) {
            count += component.countElements();
        }
        return count;
    }

    @Override
    public String toString() {
        return String.format("CompositeTool{name='%s', type=%s, components=%d}",
                name, type, components.size());
    }
}