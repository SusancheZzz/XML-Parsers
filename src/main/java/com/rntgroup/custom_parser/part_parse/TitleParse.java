package com.rntgroup.custom_parser.part_parse;

import com.rntgroup.custom_parser.entity.Play;
import org.xml.sax.Attributes;

import java.util.Objects;

import static com.rntgroup.custom_parser.constants.Constants.AUTHOR;

public class TitleParse implements PartParsable{


    @Override
    public void startParse(Attributes attributes, SaxParserFlags saxParserFlags, Play play) {
        if (Objects.nonNull(attributes.getValue(AUTHOR))) {
            saxParserFlags.setTitlePlay(true);
            play.setAuthor(attributes.getValue(AUTHOR));
        }
    }

    @Override
    public void endParse() {

    }
}
