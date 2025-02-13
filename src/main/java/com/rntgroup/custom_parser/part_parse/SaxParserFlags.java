package com.rntgroup.custom_parser.part_parse;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class SaxParserFlags {

    private boolean isTitlePlay = false;
    private boolean isAct = false;
    private boolean isScene = false;
    private boolean isSpeech = false;
    private boolean isGroup = false;
}