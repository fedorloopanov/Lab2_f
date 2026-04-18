package app.parser;

import app.exception.MissionParseException;
import org.w3c.dom.*;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class XmlMissionParser implements MissionParser {
    @Override
    public boolean supports(File file) {
        return file.getName().toLowerCase().endsWith(".xml");
    }

    @Override
    public String getFormatName() {
        return "XML";
    }

    @Override
    public Map<String, Object> parse(File file) throws MissionParseException {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setIgnoringComments(true);
            factory.setNamespaceAware(false);
            Document doc = factory.newDocumentBuilder().parse(
                    new InputSource(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))
            );
            doc.getDocumentElement().normalize();
            Element root = doc.getDocumentElement();

            Map<String, Object> mission = new LinkedHashMap<>();
            for (Element child : childElements(root)) {
                String tag = child.getTagName();
                switch (tag) {
                    case "missionId", "date", "location", "outcome" -> mission.put(tag, text(child));
                    case "damageCost" -> mission.put(tag, parseNumber(text(child)));
                    case "curse" -> mission.put("curse", parseSimpleObject(child));
                    case "sorcerers" -> mission.put("sorcerers", parseWrappedList(child, "sorcerer"));
                    case "techniques" -> mission.put("techniques", parseWrappedList(child, "technique"));
                    case "operationTimeline" -> mission.put("operationTimeline", parseTimeline(child));
                    default -> mission.put(tag, parseFlexibleElement(child));
                }
            }
            return mission;
        } catch (Exception e) {
            throw new MissionParseException("Не удалось разобрать XML: " + e.getMessage(), e);
        }
    }

    private Object parseFlexibleElement(Element element) {
        List<Element> children = childElements(element);
        if (children.isEmpty()) {
            return text(element);
        }

        boolean sameTag = children.stream().map(Element::getTagName).distinct().count() == 1;
        if (sameTag) {
            List<Object> list = new ArrayList<>();
            for (Element child : children) {
                list.add(parseFlexibleElement(child));
            }
            return list;
        }

        Map<String, Object> map = new LinkedHashMap<>();
        for (Element child : children) {
            String key = child.getTagName();
            Object value = parseFlexibleElement(child);
            mergeValue(map, key, value);
        }
        return map;
    }

    private List<Map<String, Object>> parseTimeline(Element parent) {
        List<Map<String, Object>> list = new ArrayList<>();
        for (Element event : childElements(parent)) {
            list.add(parseSimpleObject(event));
        }
        return list;
    }

    private List<Map<String, Object>> parseWrappedList(Element wrapper, String itemTag) {
        List<Map<String, Object>> list = new ArrayList<>();
        for (Element child : childElements(wrapper)) {
            if (itemTag.equals(child.getTagName())) {
                list.add(parseSimpleObject(child));
            }
        }
        return list;
    }

    private Map<String, Object> parseSimpleObject(Element element) {
        Map<String, Object> map = new LinkedHashMap<>();
        for (Element child : childElements(element)) {
            List<Element> grandChildren = childElements(child);
            if (grandChildren.isEmpty()) {
                map.put(child.getTagName(), parseMaybeNumberOrBoolean(text(child)));
            } else if (grandChildren.stream().map(Element::getTagName).distinct().count() == 1) {
                List<Object> list = new ArrayList<>();
                for (Element item : grandChildren) {
                    if (childElements(item).isEmpty()) {
                        list.add(parseMaybeNumberOrBoolean(text(item)));
                    } else {
                        list.add(parseSimpleObject(item));
                    }
                }
                map.put(child.getTagName(), list);
            } else {
                map.put(child.getTagName(), parseFlexibleElement(child));
            }
        }
        return map;
    }

    private void mergeValue(Map<String, Object> map, String key, Object value) {
        if (!map.containsKey(key)) {
            map.put(key, value);
            return;
        }
        Object existing = map.get(key);
        if (existing instanceof List<?> existingList) {
            List<Object> merged = new ArrayList<>(existingList);
            merged.add(value);
            map.put(key, merged);
        } else {
            List<Object> merged = new ArrayList<>();
            merged.add(existing);
            merged.add(value);
            map.put(key, merged);
        }
    }

    private List<Element> childElements(Element element) {
        List<Element> elements = new ArrayList<>();
        NodeList nodes = element.getChildNodes();
        for (int i = 0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                elements.add((Element) node);
            }
        }
        return elements;
    }

    private String text(Element element) {
        return element.getTextContent() == null ? "" : element.getTextContent().trim();
    }

    private Object parseMaybeNumberOrBoolean(String value) {
        if (value == null || value.isBlank()) return "";
        if ("true".equalsIgnoreCase(value) || "false".equalsIgnoreCase(value)) {
            return Boolean.parseBoolean(value);
        }
        try {
            if (value.contains(".")) return Double.parseDouble(value);
            return Long.parseLong(value);
        } catch (NumberFormatException ignored) {
            return value;
        }
    }

    private Long parseNumber(String value) {
        try {
            return Long.parseLong(value.trim());
        } catch (Exception e) {
            return null;
        }
    }
}
