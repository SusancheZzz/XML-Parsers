package com.rntgroup.customparser.partparse;

import com.rntgroup.customparser.entity.Play;
import org.xml.sax.Attributes;

import javax.naming.OperationNotSupportedException;

public class StagedirPartParse implements PartParse {
    @Override
    public void startParse(Attributes attributes, ParsingState parsingState, Play play) throws OperationNotSupportedException {
        throw new OperationNotSupportedException();
    }

    @Override
    public void endParse(ParsingState parsingState, Play play, String content) {
        if (parsingState.isSpeech()) {
            play.getActs().getLast()
                .getSceneList().getLast()
                .getSpeeches().getLast()
                .getActions().add(content);
        } else {
            play.getActs().getLast()
                .getSceneList().getLast()
                .getActions().add(content);
        }
    }
}
