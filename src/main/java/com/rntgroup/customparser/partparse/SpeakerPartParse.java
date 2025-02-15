package com.rntgroup.customparser.partparse;

import com.rntgroup.customparser.entity.Person;
import com.rntgroup.customparser.entity.Play;
import org.xml.sax.Attributes;

import javax.naming.OperationNotSupportedException;

public class SpeakerPartParse implements PartParse {
    @Override
    public void startParse(Attributes attributes, ParsingState parsingState, Play play) throws OperationNotSupportedException {
        throw new OperationNotSupportedException();
    }

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
