package com.rntgroup.custom_parser.sax;

import com.rntgroup.custom_parser.PlayParser;
import com.rntgroup.custom_parser.comparator.TagCounterComparator;
import com.rntgroup.custom_parser.entity.Play;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.LinkedHashMap;
import java.util.Map;

import static com.rntgroup.custom_parser.constants.Constants.FILE_NOT_FOUND;

public class PlayParserSAX implements PlayParser {

    private Map<String, Integer> tagCountMap;
    private final File file;
    private boolean parseUsed = false;

    public PlayParserSAX(File file) {
        this.file = file;
    }

    @Override
    public Play parse() {
        try {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser saxParser = factory.newSAXParser();
            PlayHandler handler = new PlayHandler();
            saxParser.parse(file, handler);

            this.tagCountMap = handler.getTagCountMap();
            parseUsed = true;

            return handler.getPlay();

        }catch(Exception ex){
            throw new RuntimeException(ex);
        }
    }

    public Map<String, Integer> getTagsCounter(){
        if(!parseUsed)
            throw new IllegalStateException("The parse method was not called");
        return tagCountMap.entrySet().stream()
            .sorted(new TagCounterComparator())
            .collect(LinkedHashMap::new,
                (m, e) -> m.put(e.getKey(), e.getValue()),
                Map::putAll);
    }

    @Override
    public void exportToCSV(File outputFile) {
        if(!parseUsed)
            throw new IllegalStateException("The parse method was not called");
        try (PrintWriter writer = new PrintWriter(outputFile)) {
            writer.println("word,amount");
            tagCountMap.entrySet().stream()
                .sorted(new TagCounterComparator())
                .forEach( entry -> writer.println(entry.getKey() + "," + entry.getValue()));
        } catch (FileNotFoundException e) {
            throw new IllegalStateException(FILE_NOT_FOUND);
        }
    }
}

