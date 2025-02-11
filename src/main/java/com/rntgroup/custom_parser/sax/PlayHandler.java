package com.rntgroup.custom_parser.sax;

import com.rntgroup.custom_parser.entity.Act;
import com.rntgroup.custom_parser.entity.Person;
import com.rntgroup.custom_parser.entity.Play;
import com.rntgroup.custom_parser.entity.Scene;
import com.rntgroup.custom_parser.entity.Speech;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@NoArgsConstructor
class PlayHandler extends DefaultHandler {

    @Getter
    private final Play play = new Play();
    @Getter
    private final Map<String, Integer> tagCountMap = new HashMap<>();

    private final StringBuilder contentText = new StringBuilder();
    private final StringBuilder annotation = new StringBuilder();
    private boolean isTitlePlay = false;
    private boolean isAct = false;
    private boolean isScene = false;
    private boolean isSpeech = false;
    private int countPerson = 0;
    private String currentGroup = null;
    private boolean isGroup = false;
    private String currentSpeaker = null;
    private final StringBuilder speechText = new StringBuilder();

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) {

        // Для накопления числа вхождений тегов
        tagCountMap.put(qName, tagCountMap.getOrDefault(qName, 0) + 1);

        // Обнуляем накопленный контент
        contentText.setLength(0);

        switch (qName) {
            case "TITLE":
                if (Objects.nonNull(attributes.getValue("AUTHOR"))) {
                    isTitlePlay = true;
                    play.setAuthor(attributes.getValue("AUTHOR"));
                }
                break;
            case "PERSONA":
                if (isGroup)
                    countPerson++;
                break;
            case "PGROUP":
                isGroup = true;
                break;
            case "ACT":
                isAct = true;
                play.getActs().add(new Act());
                break;
            case "SCENE":
                isScene = true;
                play.getActs().getLast().getSceneList()
                    .add(new Scene());
                break;
            case "SPEECH":
                isSpeech = true;
                currentSpeaker = null;
                play.getActs().getLast()
                    .getSceneList().getLast()
                    .getSpeeches().add(new Speech());
                break;
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) {

        String text = contentText.toString().trim();
        switch (qName) {

            // Парсинг названий в зависимости от родительского тега
            case "TITLE":
                if (isTitlePlay) {
                    isTitlePlay = false;
                    play.setTitle(text);
                }
                if (isAct) {
                    isAct = false;
                    play.getActs().getLast()
                        .setTitle(text);
                }
                if (isScene) {
                    isScene = false;
                    play.getActs().getLast()
                        .getSceneList().getLast()
                        .setTitle(text);
                }
                break;

            // Парсинг аннотации
            case "P":
                annotation.append(text).append('\n');
                break;
            case "FM":
                play.setAnnotation(annotation.toString());
                break;

            // Парсинг персонажей
            case "PERSONA":
                play.getPersonList().add(new Person(text, currentGroup));
                break;
            case "GRPDESCR":
                currentGroup = text;
                break;
            case "PGROUP":
                isGroup = false;
                for (int i = play.getPersonList().size() - countPerson; i < play.getPersonList().size(); i++)
                    play.getPersonList().get(i).setGroupId(currentGroup);
                currentGroup = null;
                countPerson = 0;
                break;

            case "ACT":
                isAct = false;
                break;
            case "SCENE":
                isScene = false;
                break;

            case "SPEAKER":
                currentSpeaker = text;
                Person person = play.findPersonByName(currentSpeaker)
                    .orElse(new Person(currentSpeaker, null));
                play.getActs().getLast()
                    .getSceneList().getLast()
                    .getSpeeches().getLast()
                    .setPerson(person);
                break;
            case "SPEECH":
                play.getActs().getLast()
                    .getSceneList().getLast()
                    .getSpeeches().getLast()
                    .setText(speechText.toString());

                currentSpeaker = null;
                speechText.setLength(0); // Обнуляем текст последнего спича
                isSpeech = false;
                break;

            case "LINE":
                speechText.append(text).append("\n");
                break;

            case "STAGEDIR": // Разделение двух действий -- в SPEECH и в SCENE
                if (isSpeech) {
                    play.getActs().getLast()
                        .getSceneList().getLast()
                        .getSpeeches().getLast()
                        .getActions().add(text);
                } else {
                    play.getActs().getLast()
                        .getSceneList().getLast()
                        .getActions().add(text);
                }
                break;
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) {
        contentText.append(ch, start, length);
    }
}
