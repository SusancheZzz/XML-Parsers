package com.rntgroup.custom_parser.part_parse;

import com.rntgroup.custom_parser.entity.Play;

public class PParse implements PartParsable {

    @Override
    public void endParse(ParsingState parsingState, Play play, String content) {
        parsingState.getAnnotation().append(content).append('\n');
    }
}
