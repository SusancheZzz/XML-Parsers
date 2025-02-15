package com.rntgroup.customparser.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@AllArgsConstructor
public class Act {
    @Setter
    private String title;
    private final List<Scene> sceneList;

    public Act() {
        this.sceneList = new ArrayList<>();
    }
}
