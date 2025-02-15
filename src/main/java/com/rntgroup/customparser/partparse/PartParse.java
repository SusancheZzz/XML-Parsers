package com.rntgroup.customparser.partparse;

import com.rntgroup.customparser.entity.Play;
import org.xml.sax.Attributes;

import javax.naming.OperationNotSupportedException;

public interface PartParse {

    void startParse(Attributes attributes, ParsingState parsingState, Play play) throws OperationNotSupportedException;

    void endParse(ParsingState parsingState, Play play, String content) throws OperationNotSupportedException;

}
