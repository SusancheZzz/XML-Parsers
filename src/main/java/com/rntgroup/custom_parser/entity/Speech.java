package com.rntgroup.custom_parser.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@AllArgsConstructor
public class Speech {
    @Setter
    private Person person;
    @Setter
    private String text;
    private final List<String> actions;

    public Speech() {
        this.actions = new ArrayList<>();
    }
}
