package com.rntgroup.custom_parser.part_parse;

import com.rntgroup.custom_parser.entity.Play;
import org.xml.sax.Attributes;

public interface PartParsable {

    default void startParse(Attributes attributes, ParsingState parsingState, Play play){
    }

    default void endParse(ParsingState parsingState, Play play, String content){
    }
}
