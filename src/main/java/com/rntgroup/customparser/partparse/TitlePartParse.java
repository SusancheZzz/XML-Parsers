package com.rntgroup.customparser.partparse;

import com.rntgroup.customparser.entity.Play;
import org.xml.sax.Attributes;

import java.util.Objects;

import static com.rntgroup.customparser.constants.Constants.AUTHOR;

public class TitlePartParse implements PartParse {


    @Override
    public void startParse(Attributes attributes, ParsingState parsingState, Play play) {
        if (Objects.nonNull(attributes.getValue(AUTHOR))) {
            parsingState.setTitlePlay(true);
            play.setAuthor(attributes.getValue(AUTHOR));
        }
    }

    @Override
    public void endParse(ParsingState parsingState, Play play, String content) {
        if (parsingState.isTitlePlay()) {
            parsingState.setTitlePlay(false);
            play.setTitle(content);
        }
        if (parsingState.isAct()) {
            parsingState.setAct(false);
            play.getActs().getLast()
                .setTitle(content);
        }
        if (parsingState.isScene()) {
            parsingState.setScene(false);
            play.getActs().getLast()
                .getSceneList().getLast()
                .setTitle(content);
        }
    }
}
