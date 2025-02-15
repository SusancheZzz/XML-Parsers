package com.rntgroup.customparser.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@AllArgsConstructor
public class Scene {
    @Setter
    private String title;
    private final List<Speech> speeches;
    private final List<String> actions;

    public Scene() {
        this.speeches = new ArrayList<>();
        this.actions = new ArrayList<>();
    }
}
