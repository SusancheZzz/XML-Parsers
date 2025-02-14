package com.rntgroup.custom_parser.part_parse;

import com.rntgroup.custom_parser.entity.Play;

public class LineParse implements PartParsable{
    @Override
    public void endParse(ParsingState parsingState, Play play, String content) {
        parsingState.getSpeechText().append(content).append("\n");
    }
}
