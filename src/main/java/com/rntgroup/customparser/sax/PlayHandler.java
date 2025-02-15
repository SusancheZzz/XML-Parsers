package com.rntgroup.customparser.sax;

import com.rntgroup.customparser.entity.Play;
import com.rntgroup.customparser.partparse.ActPartParse;
import com.rntgroup.customparser.partparse.FmPartParse;
import com.rntgroup.customparser.partparse.GroupPartParse;
import com.rntgroup.customparser.partparse.GrpdescrPartParse;
import com.rntgroup.customparser.partparse.LinePartParse;
import com.rntgroup.customparser.partparse.PPartParse;
import com.rntgroup.customparser.partparse.ParsingState;
import com.rntgroup.customparser.partparse.PartParse;
import com.rntgroup.customparser.partparse.PersonPartParse;
import com.rntgroup.customparser.partparse.ScenePartParse;
import com.rntgroup.customparser.partparse.SpeakerPartParse;
import com.rntgroup.customparser.partparse.SpeechPartParse;
import com.rntgroup.customparser.partparse.StagedirPartParse;
import com.rntgroup.customparser.partparse.TitlePartParse;
import lombok.Getter;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

import javax.naming.OperationNotSupportedException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.rntgroup.customparser.constants.Constants.ACT;
import static com.rntgroup.customparser.constants.Constants.FM;
import static com.rntgroup.customparser.constants.Constants.GRPDESCR;
import static com.rntgroup.customparser.constants.Constants.LINE;
import static com.rntgroup.customparser.constants.Constants.P;
import static com.rntgroup.customparser.constants.Constants.PERSONA;
import static com.rntgroup.customparser.constants.Constants.PGROUP;
import static com.rntgroup.customparser.constants.Constants.SCENE;
import static com.rntgroup.customparser.constants.Constants.SPEAKER;
import static com.rntgroup.customparser.constants.Constants.SPEECH;
import static com.rntgroup.customparser.constants.Constants.STAGEDIR;
import static com.rntgroup.customparser.constants.Constants.TITLE;


class PlayHandler extends DefaultHandler {

    @Getter
    private final Play play = new Play();
    @Getter
    private final Map<String, Integer> tagCountMap = new HashMap<>();
    private final ParsingState parsingState = new ParsingState();

    private final Map<String, PartParse> parsePartMap = new HashMap<>();

    public PlayHandler() {
        initParseMap();
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) {

        // Для накопления числа вхождений тегов
        tagCountMap.put(qName, tagCountMap.getOrDefault(qName, 0) + 1);

        // Обнуляем накопленный контент
        parsingState.getContentText().setLength(0);

        PartParse curPartParser = parsePartMap.get(qName);
        if (Objects.nonNull(curPartParser)) {
            try {
                curPartParser.startParse(attributes, parsingState, play);
            } catch (OperationNotSupportedException e) {}
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) {

        String text = parsingState.getContentText().toString().trim();

        PartParse curPartParser = parsePartMap.get(qName);
        if (Objects.nonNull(curPartParser)) {
            try {
                curPartParser.endParse(parsingState, play, text);
            } catch (OperationNotSupportedException e) {}
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) {
        parsingState.getContentText().append(ch, start, length);
    }

    private void initParseMap() {
        parsePartMap.put(TITLE, new TitlePartParse());
        parsePartMap.put(ACT, new ActPartParse());
        parsePartMap.put(FM, new FmPartParse());
        parsePartMap.put(PGROUP, new GroupPartParse());
        parsePartMap.put(GRPDESCR, new GrpdescrPartParse());
        parsePartMap.put(LINE, new LinePartParse());
        parsePartMap.put(PERSONA, new PersonPartParse());
        parsePartMap.put(P, new PPartParse());
        parsePartMap.put(SCENE, new ScenePartParse());
        parsePartMap.put(SPEAKER, new SpeakerPartParse());
        parsePartMap.put(SPEECH, new SpeechPartParse());
        parsePartMap.put(STAGEDIR, new StagedirPartParse());
    }
}
