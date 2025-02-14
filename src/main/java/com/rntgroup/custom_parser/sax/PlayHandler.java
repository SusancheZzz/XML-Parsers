package com.rntgroup.custom_parser.sax;

import com.rntgroup.custom_parser.entity.Play;
import com.rntgroup.custom_parser.part_parse.ActParse;
import com.rntgroup.custom_parser.part_parse.FmParse;
import com.rntgroup.custom_parser.part_parse.GroupParse;
import com.rntgroup.custom_parser.part_parse.GrpdescrParse;
import com.rntgroup.custom_parser.part_parse.LineParse;
import com.rntgroup.custom_parser.part_parse.PParse;
import com.rntgroup.custom_parser.part_parse.ParsingState;
import com.rntgroup.custom_parser.part_parse.PartParsable;
import com.rntgroup.custom_parser.part_parse.PersonParse;
import com.rntgroup.custom_parser.part_parse.SceneParse;
import com.rntgroup.custom_parser.part_parse.SpeakerParse;
import com.rntgroup.custom_parser.part_parse.SpeechParse;
import com.rntgroup.custom_parser.part_parse.StagedirParse;
import com.rntgroup.custom_parser.part_parse.TitleParse;
import lombok.Getter;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.rntgroup.custom_parser.constants.Constants.ACT;
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


class PlayHandler extends DefaultHandler {

    @Getter
    private final Play play = new Play();
    @Getter
    private final Map<String, Integer> tagCountMap = new HashMap<>();
    private final ParsingState parsingState = new ParsingState();

    private final Map<String, PartParsable> parsePartMap = new HashMap<>();

    public PlayHandler() {
        initParseMap();
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) {

        // Для накопления числа вхождений тегов
        tagCountMap.put(qName, tagCountMap.getOrDefault(qName, 0) + 1);

        // Обнуляем накопленный контент
        parsingState.getContentText().setLength(0);

        PartParsable curPartParser = parsePartMap.get(qName);
        if (Objects.nonNull(curPartParser))
            curPartParser.startParse(attributes, parsingState, play);
    }

    @Override
    public void endElement(String uri, String localName, String qName) {

        String text = parsingState.getContentText().toString().trim();

        PartParsable curPartParser = parsePartMap.get(qName);
        if (Objects.nonNull(curPartParser))
            curPartParser.endParse(parsingState, play, text);
    }

    @Override
    public void characters(char[] ch, int start, int length) {
        parsingState.getContentText().append(ch, start, length);
    }

    private void initParseMap() {
        parsePartMap.put(TITLE, new TitleParse());
        parsePartMap.put(ACT, new ActParse());
        parsePartMap.put(FM, new FmParse());
        parsePartMap.put(PGROUP, new GroupParse());
        parsePartMap.put(GRPDESCR, new GrpdescrParse());
        parsePartMap.put(LINE, new LineParse());
        parsePartMap.put(PERSONA, new PersonParse());
        parsePartMap.put(P, new PParse());
        parsePartMap.put(SCENE, new SceneParse());
        parsePartMap.put(SPEAKER, new SpeakerParse());
        parsePartMap.put(SPEECH, new SpeechParse());
        parsePartMap.put(STAGEDIR, new StagedirParse());
    }
}
