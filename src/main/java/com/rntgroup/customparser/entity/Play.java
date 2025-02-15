package com.rntgroup.customparser.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
public class Play {
    @Setter
    private String author;
    @Setter
    private String title;
    @Setter
    private String annotation;
    private final Map<String, Person> personList = new HashMap<>();
    private final List<Act> acts = new ArrayList<>();


    public Play(String author, String title, String annotation) {
        this.author = author;
        this.title = title;
        this.annotation = annotation;
    }

    public Set<String> getUniqueWordsPerson(String personName) {
        return acts.stream()
            .flatMap(act -> act.getSceneList().stream())
            .flatMap(scene -> scene.getSpeeches().stream())
            .filter(speech -> findPersonByName(personName).isPresent())
            .flatMap(speech -> Arrays.stream(speech.getText().split("[!,.?;:+\\-`\\s]")))
            .collect(Collectors.toSet());
    }

    public Optional<Person> findPersonByName(String name) {
        return personList.entrySet().stream()
            .filter(pers -> {
                String lowerCaseName = pers.getKey().toLowerCase();
                return Arrays.stream(name.toLowerCase().split("[!,.?;:+\\-`\\s]"))
                    .anyMatch(lowerCaseName::contains);
            })
            .map(Map.Entry::getValue)
            .findFirst();
    }
}
