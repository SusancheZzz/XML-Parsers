package com.rntgroup.customparser.partparse;

import com.rntgroup.customparser.entity.Play;
import com.rntgroup.customparser.entity.Scene;
import org.xml.sax.Attributes;

public class ScenePartParse implements PartParse {
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
