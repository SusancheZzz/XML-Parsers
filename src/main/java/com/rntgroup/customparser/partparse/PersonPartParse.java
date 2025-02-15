package com.rntgroup.customparser.partparse;

import com.rntgroup.customparser.entity.Person;
import com.rntgroup.customparser.entity.Play;
import org.xml.sax.Attributes;

import javax.naming.OperationNotSupportedException;

public class PersonPartParse implements PartParse {
    @Override
    public void startParse(Attributes attributes, ParsingState parsingState, Play play) throws OperationNotSupportedException {
        throw new OperationNotSupportedException();
    }

    @Override
    public void endParse(ParsingState parsingState, Play play, String content) {
        play.getPersonList().put(content, new Person(content, parsingState.getCurrentGroup()));
        if (parsingState.isGroup())
            parsingState.getGroupPersonName().add(content);
    }
}
