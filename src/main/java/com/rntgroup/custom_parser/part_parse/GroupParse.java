package com.rntgroup.custom_parser.part_parse;

import com.rntgroup.custom_parser.entity.Play;
import org.xml.sax.Attributes;

public class GroupParse implements PartParsable{

    @Override
    public void startParse(Attributes attributes, ParsingState parsingState, Play play) {
        parsingState.setGroup(true);
    }

    @Override
    public void endParse(ParsingState parsingState, Play play, String content) {
        parsingState.setGroup(false);
        for (String name : parsingState.getGroupPersonName()) {
            play.getPersonList().get(name).setGroupId(parsingState.getCurrentGroup());
        }
        parsingState.getGroupPersonName().clear();
        parsingState.setCurrentGroup(null);
    }
}
