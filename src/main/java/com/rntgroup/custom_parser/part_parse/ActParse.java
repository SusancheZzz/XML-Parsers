package com.rntgroup.custom_parser.part_parse;

import com.rntgroup.custom_parser.entity.Act;
import com.rntgroup.custom_parser.entity.Play;
import org.xml.sax.Attributes;

public class ActParse implements PartParsable{
    @Override
    public void startParse(Attributes attributes, ParsingState parsingState, Play play) {
        parsingState.setAct(true);
        play.getActs().add(new Act());
    }

    @Override
    public void endParse(ParsingState parsingState, Play play, String content) {
        parsingState.setAct(false);
    }
}
