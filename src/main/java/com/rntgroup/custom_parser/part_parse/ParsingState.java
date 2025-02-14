package com.rntgroup.custom_parser.part_parse;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
public class ParsingState {

    private boolean isTitlePlay = false;
    private boolean isAct = false;
    private boolean isScene = false;
    private boolean isSpeech = false;
    private boolean isGroup = false;

    private final StringBuilder contentText = new StringBuilder();
    private final StringBuilder annotation = new StringBuilder();
    private String currentGroup = null;
    private String currentSpeaker = null;
    private final StringBuilder speechText = new StringBuilder();
    private final List<String> groupPersonName = new ArrayList<>();

    private String text = contentText.toString().trim();
}