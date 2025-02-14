package com.rntgroup.custom_parser.part_parse;

import com.rntgroup.custom_parser.entity.Play;
import com.rntgroup.custom_parser.entity.Scene;
import org.xml.sax.Attributes;

public class SceneParse implements PartParsable{
    @Override
    public void startParse(Attributes attributes, ParsingState parsingState, Play play) {
        parsingState.setScene(true);
        play.getActs().getLast().getSceneList()
            .add(new Scene());
    }

    @Override
    public void endParse(ParsingState parsingState, Play play, String content) {
        parsingState.setScene(false);
    }
}
