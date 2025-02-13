package com.rntgroup.custom_parser.sax;

import com.rntgroup.custom_parser.entity.Act;
import com.rntgroup.custom_parser.entity.Person;
import com.rntgroup.custom_parser.entity.Play;
import com.rntgroup.custom_parser.entity.Scene;
import com.rntgroup.custom_parser.entity.Speech;
import com.rntgroup.custom_parser.part_parse.PartParsable;
import com.rntgroup.custom_parser.part_parse.SaxParserFlags;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.rntgroup.custom_parser.constants.Constants.ACT;
import static com.rntgroup.custom_parser.constants.Constants.AUTHOR;
import static com.rntgroup.custom_parser.constants.Constants.FM;
import static com.rntgroup.custom_parser.constants.Constants.GRPDESCR;
import static com.rntgroup.custom_parser.constants.Constants.LINE;
import static com.rntgroup.custom_parser.constants.Constants.P;
import static com.rntgroup.custom_parser.constants.Constants.PERSONA;
import static com.rntgroup.custom_parser.constants.Constants.PGROUP;
import static com.rntgroup.custom_parser.constants.Constants.SCENE;
import static com.rntgroup.custom_parser.constants.Constants.SPEAKER;
import static com.rntgroup.custom_parser.constants.Constants.SPEECH;
import static com.rntgroup.custom_parser.constants.Constants.STAGEDIR;
import static com.rntgroup.custom_parser.constants.Constants.TITLE;

@NoArgsConstructor
class PlayHandler extends DefaultHandler {

    @Getter
    private final Play play = new Play();
    @Getter
    private final Map<String, Integer> tagCountMap = new HashMap<>();

    private final SaxParserFlags saxParserFlags = new SaxParserFlags();

    private final StringBuilder contentText = new StringBuilder();
    private final StringBuilder annotation = new StringBuilder();
    private String currentGroup = null;
    private String currentSpeaker = null;
    private final StringBuilder speechText = new StringBuilder();
    private final List<String> groupPersonName = new ArrayList<>();

    // Создание и инициализация мапы для стратегии
    private final Map<String, PartParsable> parseSaxPart = new HashMap<>();

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) {

        // Для накопления числа вхождений тегов
        tagCountMap.put(qName, tagCountMap.getOrDefault(qName, 0) + 1);

        // Обнуляем накопленный контент
        contentText.setLength(0);

        parseSaxPart.get(qName).startParse(attributes, saxParserFlags, play);

        switch (qName) {
            case TITLE:
                if (Objects.nonNull(attributes.getValue(AUTHOR))) {
                    saxParserFlags.setTitlePlay(true);
                    play.setAuthor(attributes.getValue(AUTHOR));
                }
                break;
            case PGROUP:
                saxParserFlags.setGroup(true);
                break;
            case ACT:
                saxParserFlags.setAct(true);
                play.getActs().add(new Act());
                break;
            case SCENE:
                saxParserFlags.setScene(true);
                play.getActs().getLast().getSceneList()
                    .add(new Scene());
                break;
            case SPEECH:
                saxParserFlags.setSpeech(true);
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
            case TITLE:
                if (saxParserFlags.isTitlePlay()) {
                    saxParserFlags.setTitlePlay(false);
                    play.setTitle(text);
                }
                if (saxParserFlags.isAct()) {
                    saxParserFlags.setAct(false);
                    play.getActs().getLast()
                        .setTitle(text);
                }
                if (saxParserFlags.isScene()) {
                    saxParserFlags.setScene(false);
                    play.getActs().getLast()
                        .getSceneList().getLast()
                        .setTitle(text);
                }
                break;

            // Парсинг аннотации
            case P:
                annotation.append(text).append('\n');
                break;
            case FM:
                play.setAnnotation(annotation.toString());
                break;

            // Парсинг персонажей
            case PERSONA:
                play.getPersonList().put(text, new Person(text, currentGroup));
                if (saxParserFlags.isGroup())
                    groupPersonName.add(text);
                break;
            case GRPDESCR:
                currentGroup = text;
                break;
            case PGROUP:
                saxParserFlags.setGroup(false);
                for (String name : groupPersonName) {
                    play.getPersonList().get(name).setGroupId(currentGroup);
                }
                groupPersonName.clear();
                currentGroup = null;
                break;

            case ACT:
                saxParserFlags.setAct(false);
                break;
            case SCENE:
                saxParserFlags.setScene(false);
                break;

            case SPEAKER:
                currentSpeaker = text;
                Person person = play.findPersonByName(currentSpeaker)
                    .orElse(new Person(currentSpeaker, null));
                play.getActs().getLast()
                    .getSceneList().getLast()
                    .getSpeeches().getLast()
                    .setPerson(person);
                break;
            case SPEECH:
                play.getActs().getLast()
                    .getSceneList().getLast()
                    .getSpeeches().getLast()
                    .setText(speechText.toString());

                currentSpeaker = null;
                speechText.setLength(0); // Обнуляем текст последнего спича
                saxParserFlags.setSpeech(false);
                break;

            case LINE:
                speechText.append(text).append("\n");
                break;

            case STAGEDIR: // Разделение двух действий -- в SPEECH и в SCENE
                if (saxParserFlags.isSpeech()) {
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
