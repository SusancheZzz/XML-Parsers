package com.rntgroup.custom_parser.part_parse;

import com.rntgroup.custom_parser.entity.Play;
import com.rntgroup.custom_parser.entity.Speech;
import org.xml.sax.Attributes;

public class SpeechParse implements PartParsable{
    @Override
    public void startParse(Attributes attributes, ParsingState parsingState, Play play) {
        parsingState.setSpeech(true);
        parsingState.setCurrentSpeaker(null);
        play.getActs().getLast()
            .getSceneList().getLast()
            .getSpeeches().add(new Speech());
    }

    @Override
    public void endParse(ParsingState parsingState, Play play, String content) {
        play.getActs().getLast()
            .getSceneList().getLast()
            .getSpeeches().getLast()
            .setText(parsingState.getSpeechText().toString());

        parsingState.setCurrentSpeaker(null);
        parsingState.getSpeechText().setLength(0); // Обнуляем текст последнего спича
        parsingState.setSpeech(false);    }
}
