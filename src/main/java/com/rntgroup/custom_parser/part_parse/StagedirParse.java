package com.rntgroup.custom_parser.part_parse;

import com.rntgroup.custom_parser.entity.Play;

public class StagedirParse implements PartParsable{
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
