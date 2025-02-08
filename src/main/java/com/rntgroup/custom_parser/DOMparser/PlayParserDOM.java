package com.rntgroup.custom_parser.DOMparser;

import com.rntgroup.custom_parser.entity.Act;
import com.rntgroup.custom_parser.entity.Person;
import com.rntgroup.custom_parser.entity.Play;
import com.rntgroup.custom_parser.entity.Scene;
import com.rntgroup.custom_parser.entity.Speech;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.Arrays;
import java.util.LinkedList;

public class PlayParserDOM {

    private static Play play;
    private static final String EMPTY_STR = "";

    public static Play parse(String filePath) {
        try {
            File inputFile = new File(filePath);
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(inputFile);

            String titlePlay = EMPTY_STR;
            String authorPlay = EMPTY_STR;

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

            // Парсинг персонажей -- ВЫНЕСТИ В МЕТОД!!!!!!!!!!!!!!!!!!!!!!!!!!!
            NodeList personNodes = document.getElementsByTagName("PERSONA");
            for (int i = 0; i < personNodes.getLength(); i++) {
                String groupId = null;
                Element curPersonNode = (Element) personNodes.item(i);
                Element curParentPersonNode = (Element) curPersonNode.getParentNode();
                String namePerson = curPersonNode.getTextContent();
                // Получаем название группы
                if ("PGROUP".equals(curParentPersonNode.getNodeName())) {
                    groupId = curParentPersonNode.getElementsByTagName("GRPDESCR").item(0).getTextContent();
                }
                play.getPersonList().add(new Person(namePerson, groupId));
            }

            // Парсинг актов
            NodeList actNodes = document.getElementsByTagName("ACT");
            for (int i = 0; i < actNodes.getLength(); i++) {
                Element actElement = (Element) actNodes.item(i);
                play.getActs().add(parseAct(actElement));
            }

            return play;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static Act parseAct(Element actElement) {
        LinkedList<Scene> scenes = new LinkedList<>();
        NodeList sceneNodes = actElement.getElementsByTagName("SCENE");
        for (int i = 0; i < sceneNodes.getLength(); i++) {
            Element sceneElement = (Element) sceneNodes.item(i);
            scenes.add(parseScene(sceneElement));
        }
        return new Act(scenes);
    }

    private static Scene parseScene(Element sceneElement) {
        String title = sceneElement.getElementsByTagName("TITLE").item(0).getTextContent();
        LinkedList<Speech> speeches = new LinkedList<>();
        LinkedList<String> actions = new LinkedList<>();

        NodeList speechNodes = sceneElement.getElementsByTagName("SPEECH");
        for (int i = 0; i < speechNodes.getLength(); i++) {
            Element speechElement = (Element) speechNodes.item(i);
            speeches.add(parseSpeech(speechElement));
        }

        NodeList actionNodes = sceneElement.getElementsByTagName("STAGEDIR");
        for (int i = 0; i < actionNodes.getLength(); i++) {
            actions.add(actionNodes.item(i).getTextContent());
        }

        return new Scene(title, speeches, actions);
    }

    private static Speech parseSpeech(Element speechElement) {
        String speaker = speechElement.getElementsByTagName("SPEAKER").item(0).getTextContent();
        NodeList textNodes = speechElement.getElementsByTagName("LINE");
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < textNodes.getLength(); i++) {
            text.append(textNodes.item(i).getTextContent()).append('\n');
        }

        // Связываем с существующим персонажем
        Person person = play.getPersonList().stream()
            .filter(pers -> {
                String lowerCaseName = pers.getName().toLowerCase();
                return Arrays.stream(speaker.toLowerCase().split("\\s+")) // Разбиваем speaker на слова
                    .anyMatch(lowerCaseName::contains); // Проверяем вхождение хотя бы одного слова из speaker в список имеющихся персонажей
            })
            .findFirst()
            .orElse(new Person(speaker, null));

        return new Speech(person, text.toString());
    }
}
