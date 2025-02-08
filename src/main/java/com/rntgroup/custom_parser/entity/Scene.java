package com.rntgroup.custom_parser.entity;

import lombok.Value;

import java.util.List;

@Value
public class Scene {

    String title;
    List<Speech> speeches;
    List<String> actions;

}
