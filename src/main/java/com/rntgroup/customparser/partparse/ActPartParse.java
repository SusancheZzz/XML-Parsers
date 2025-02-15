package com.rntgroup.customparser.partparse;

import com.rntgroup.customparser.entity.Act;
import com.rntgroup.customparser.entity.Play;
import org.xml.sax.Attributes;

public class ActPartParse implements PartParse {
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
