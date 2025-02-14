package com.rntgroup.custom_parser;

import com.rntgroup.custom_parser.entity.Play;

import java.io.File;
import java.util.Map;

public interface PlayParser {

    Play parse();
    void exportToCSV(File file);
    Map<String, Integer> getTagsCounter();
}
