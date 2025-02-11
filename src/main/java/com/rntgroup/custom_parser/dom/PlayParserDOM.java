package com.rntgroup.custom_parser.dom;

import com.rntgroup.custom_parser.entity.Act;
import com.rntgroup.custom_parser.entity.Person;
import com.rntgroup.custom_parser.entity.Play;
import com.rntgroup.custom_parser.entity.Scene;
import com.rntgroup.custom_parser.entity.Speech;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class PlayParserDOM {

    private Play play;
    private Document document;
    private final Map<String, Integer> tagCountMap = new HashMap<>();

    public PlayParserDOM(File file) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            this.document = builder.parse(file);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public Play parse() {

        String titlePlay = "";
        String authorPlay = "";

        // Парсинг названия пьесы и её автора
        Element titleElement = (Element) document.getElementsByTagName("TITLE").item(0);
        if ("PLAY".equals(titleElement.getParentNode().getNodeName())) {
            titlePlay = titleElement.getTextContent();
            authorPlay = titleElement.getAttribute("AUTHOR");
        }

        // Парсинг аннотации к пьесе
        NodeList annotationNodes = document.getElementsByTagName("P");
        StringBuilder annotation = new StringBuilder();
        for (int i = 0; i < annotationNodes.getLength(); i++) {
            annotation.append(annotationNodes.item(i).getTextContent()).append('\n');
        }

        play = new Play(authorPlay, titlePlay, annotation.toString());

        // Парсинг персонажей
        NodeList personNodes = document.getElementsByTagName("PERSONA");
        parsePersons(personNodes);

        // Парсинг актов
        NodeList actNodes = this.document.getElementsByTagName("ACT");
        for (int i = 0; i < actNodes.getLength(); i++) {
            Element actElement = (Element) actNodes.item(i);
            play.getActs().add(parseAct(actElement));
        }
        return play;
    }

    private void parsePersons(NodeList personNodes) {
        for (int i = 0; i < personNodes.getLength(); i++) {
            String groupId = null;
            Element curPersonNode = (Element) personNodes.item(i);
            Element curParentPersonNode = (Element) curPersonNode.getParentNode();
            String namePerson = curPersonNode.getTextContent();

            if ("PGROUP".equals(curParentPersonNode.getNodeName())) {
                groupId = curParentPersonNode.getElementsByTagName("GRPDESCR").item(0).getTextContent();
            }
            play.getPersonList().add(new Person(namePerson, groupId));
        }
    }

    private Act parseAct(Element actElement) {
        LinkedList<Scene> scenes = new LinkedList<>();
        String titleAct = actElement.getElementsByTagName("TITLE").item(0).getTextContent();
        NodeList sceneNodes = actElement.getElementsByTagName("SCENE");
        for (int i = 0; i < sceneNodes.getLength(); i++) {
            Element sceneElement = (Element) sceneNodes.item(i);
            scenes.add(parseScene(sceneElement));
        }
        return new Act(titleAct, scenes);
    }

    private Scene parseScene(Element sceneElement) {
        String title = sceneElement.getElementsByTagName("TITLE").item(0).getTextContent();
        LinkedList<Speech> speeches = new LinkedList<>();
        LinkedList<String> actions = new LinkedList<>();

        NodeList speechNodes = sceneElement.getElementsByTagName("SPEECH");
        for (int i = 0; i < speechNodes.getLength(); i++) {
            Element speechElement = (Element) speechNodes.item(i);
            speeches.add(parseSpeech(speechElement));
        }

        // Парсинг всех действий между спичами
        NodeList actionNodes = sceneElement.getElementsByTagName("STAGEDIR");
        for (int i = 0; i < actionNodes.getLength(); i++) {
            if ("SCENE".equals(actionNodes.item(i).getParentNode().getNodeName())) {
                actions.add(actionNodes.item(i).getTextContent());
            }
        }

        return new Scene(title, speeches, actions);
    }

    private Speech parseSpeech(Element speechElement) {
        String speaker = speechElement.getElementsByTagName("SPEAKER").item(0).getTextContent();
        NodeList textNodes = speechElement.getElementsByTagName("LINE");
        StringBuilder text = new StringBuilder();

        for (int i = 0; i < textNodes.getLength(); i++) {
            text.append(textNodes.item(i).getTextContent()).append('\n');
        }
        // Связываем speaker с существующим персонажем, либо возвращаем нового
        Person person = play.findPersonByName(speaker)
            .orElse(new Person(speaker, null));

        // Парсинг всех действий, внутри одного спича
        List<String> actionsSpeech = new LinkedList<>();
        NodeList actions = speechElement.getElementsByTagName("STAGEDIR");
        for (int i = 0; i < actions.getLength(); i++) {
            actionsSpeech.add(actions.item(i).getTextContent());
        }

        return new Speech(person, text.toString(), actionsSpeech);
    }

    public Map<String, Integer> getTagsCounter(){
        countTags(document.getDocumentElement());

        return tagCountMap.entrySet().stream()
            .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
            .collect(LinkedHashMap::new,
                (m, e) -> m.put(e.getKey(), e.getValue()),
                Map::putAll);
    }

    private void countTags(Node node) {
        if (node.getNodeType() == Node.ELEMENT_NODE) {
            tagCountMap.put(node.getNodeName(), tagCountMap.getOrDefault(node.getNodeName(), 0) + 1);
        }
        NodeList childs = node.getChildNodes();
        for (int i = 0; i < childs.getLength(); i++) {
            countTags(childs.item(i));
        }
    }

    public void exportToCSV(File outputFile) {
        try (PrintWriter writer = new PrintWriter(outputFile)) {
            writer.println("word,amount");
            tagCountMap.entrySet().stream()
                .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
                .forEach( entry -> writer.println(entry.getKey() + "," + entry.getValue()));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

}
