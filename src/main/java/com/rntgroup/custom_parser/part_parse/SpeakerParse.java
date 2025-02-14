package com.rntgroup.custom_parser.part_parse;

import com.rntgroup.custom_parser.entity.Person;
import com.rntgroup.custom_parser.entity.Play;

public class SpeakerParse implements PartParsable{
    @Override
    public void endParse(ParsingState parsingState, Play play, String content) {
        parsingState.setCurrentSpeaker(content);
        Person person = play.findPersonByName(parsingState.getCurrentSpeaker())
            .orElse(new Person(parsingState.getCurrentSpeaker(), null));
        play.getActs().getLast()
            .getSceneList().getLast()
            .getSpeeches().getLast()
            .setPerson(person);
    }
}
