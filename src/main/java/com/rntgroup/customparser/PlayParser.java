package com.rntgroup.customparser;

import com.rntgroup.customparser.entity.Play;

import java.io.File;
import java.util.Map;

public interface PlayParser {

    Play parse();
    void exportToCSV(File file);
    Map<String, Integer> getTagsCounter();
}
