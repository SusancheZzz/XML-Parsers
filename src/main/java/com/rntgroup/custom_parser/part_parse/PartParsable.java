package com.rntgroup.custom_parser.part_parse;

import com.rntgroup.custom_parser.entity.Play;
import org.xml.sax.Attributes;

public interface PartParsable {

    void startParse(Attributes attributes, SaxParserFlags saxParserFlags, Play play);

    void endParse();
}
