package com.rntgroup.custom_parser.entity;

import lombok.Value;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@Value
public class Play {

    String author;
    String title;
    String annotation;
    List<Person> personList = new ArrayList<>();
    List<Act> acts = new LinkedList<>();

}
