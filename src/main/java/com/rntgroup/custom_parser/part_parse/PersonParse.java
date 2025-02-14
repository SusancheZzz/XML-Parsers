package com.rntgroup.custom_parser.part_parse;

import com.rntgroup.custom_parser.entity.Person;
import com.rntgroup.custom_parser.entity.Play;

public class PersonParse implements PartParsable{
    @Override
    public void endParse(ParsingState parsingState, Play play, String content) {
        play.getPersonList().put(content, new Person(content, parsingState.getCurrentGroup()));
        if (parsingState.isGroup())
            parsingState.getGroupPersonName().add(content);
    }
}
