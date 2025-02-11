package com.rntgroup.custom_parser;

import com.rntgroup.custom_parser.dom.PlayParserDOM;
import com.rntgroup.custom_parser.entity.Play;
import com.rntgroup.custom_parser.sax.PlayParserSAX;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Path;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PlayParserTest {

    private Play playDOM;
    private Play playSAX;
    private PlayParserDOM playParserDOM;
    private PlayParserSAX playParserSAX;
    File inputFile = Path.of("src", "main", "resources", "static", "hamlet.xml").toFile();
    File outputFile = Path.of("src", "main", "resources", "static", "countTags.csv").toFile();

    @BeforeEach
    public void init() {
        playParserDOM = new PlayParserDOM(inputFile);
        playParserSAX = new PlayParserSAX(inputFile);
        playDOM = playParserDOM.parse();
        playSAX = playParserSAX.parse();
    }

    @Test
    public void compareTitle() {
        assertEquals(playDOM.getTitle(), playSAX.getTitle());
    }

    @Test
    public void compareAuthor() {
        assertEquals(playDOM.getAuthor(), playSAX.getAuthor());
    }

    @Test
    public void compareAnnotation() {
        assertEquals(playDOM.getAnnotation(), playSAX.getAnnotation());
    }

    @Test
    public void testCountAct() {
        assertEquals(5, playDOM.getActs().size());
        assertEquals(5, playSAX.getActs().size());
    }

    @Test
    public void testCountPerson() {
        assertEquals(26, playDOM.getPersonList().size());
        assertEquals(26, playSAX.getPersonList().size());
    }

    @Test
    public void compareCountUniqueWordsPerson() {
        assertEquals(
            playDOM.getUniqueWordsPerson("Hamlet").size(),
            playSAX.getUniqueWordsPerson("Hamlet").size()
        );
        assertEquals(5193, playSAX.getUniqueWordsPerson("Hamlet").size());
    }

    @Test
    public void compareCountScene() {
        long countSceneDOM = playDOM.getActs().stream()
            .mapToLong(act -> act.getSceneList().size())
            .sum();
        long countSceneSAX = playSAX.getActs().stream()
            .mapToLong(act -> act.getSceneList().size())
            .sum();

        assertEquals(countSceneDOM, countSceneSAX);
        assertEquals(20, countSceneDOM);
    }

    @Test
    public void compareCountSpeeches() {
        long countSpeechesDOM = playDOM.getActs().stream()
            .flatMap(act -> act.getSceneList().stream())
            .mapToLong(scene -> scene.getSpeeches().size())
            .sum();
        long countSpeechesSAX = playSAX.getActs().stream()
            .flatMap(act -> act.getSceneList().stream())
            .mapToLong(scene -> scene.getSpeeches().size())
            .sum();

        assertEquals(1138, countSpeechesDOM);
        assertEquals(1138, countSpeechesSAX);
    }

    @Test
    public void compareCountPersonWithNullGroup(){
        long countPersonWithNullGroupDOM = playDOM.getPersonList().stream()
            .filter(pers -> Objects.isNull(pers.getGroupId()))
            .count();
        long countPersonWithNullGroupSAX = playSAX.getPersonList().stream()
            .filter(pers -> Objects.isNull(pers.getGroupId()))
            .count();

        assertEquals(countPersonWithNullGroupDOM, countPersonWithNullGroupSAX);
        assertEquals(19, countPersonWithNullGroupDOM);
    }

    @Test
    public void compareCountPersonWithGroupCourtiers(){
        long countPersonWithGroupCourtiersDOM = playDOM.getPersonList().stream()
            .filter(pers -> "courtiers.".equals(pers.getGroupId()))
            .count();
        long countPersonWithGroupCourtiersSAX = playSAX.getPersonList().stream()
            .filter(pers -> "courtiers.".equals(pers.getGroupId()))
            .count();

        assertEquals(countPersonWithGroupCourtiersDOM, countPersonWithGroupCourtiersSAX);
        assertEquals(5, countPersonWithGroupCourtiersDOM);
    }

    @Test
    public void compareCountActionsInScenes() {
        long countActionsInScenesDOM = playDOM.getActs().stream()
            .flatMap(act -> act.getSceneList().stream())
            .mapToLong(scene -> scene.getActions().size())
            .sum();
        long countActionsInScenesSAX = playSAX.getActs().stream()
            .flatMap(act -> act.getSceneList().stream())
            .mapToLong(scene -> scene.getActions().size())
            .sum();

        assertEquals(134, countActionsInScenesDOM);
        assertEquals(134, countActionsInScenesSAX);
    }

    @Test
    public void compareCountActionsInSpeeches() {
        long countActionsInSpeechesDOM = playDOM.getActs().stream()
            .flatMap(act -> act.getSceneList().stream())
            .flatMap(scene -> scene.getSpeeches().stream())
            .mapToLong(speech -> speech.getActions().size())
            .sum();
        long countActionsInSpeechesSAX = playSAX.getActs().stream()
            .flatMap(act -> act.getSceneList().stream())
            .flatMap(scene -> scene.getSpeeches().stream())
            .mapToLong(speech -> speech.getActions().size())
            .sum();

        assertEquals(109, countActionsInSpeechesDOM);
        assertEquals(109, countActionsInSpeechesSAX);
    }

    @Test
    public void testCountAllActionsInPlayDOM() {
        long actionsInSpeechesDOM = playDOM.getActs().stream()
            .flatMap(act -> act.getSceneList().stream())
            .flatMap(scene -> scene.getSpeeches().stream())
            .mapToLong(speech -> speech.getActions().size())
            .sum();
        long actionsInScenesDOM = playDOM.getActs().stream()
            .flatMap(act -> act.getSceneList().stream())
            .mapToLong(scene -> scene.getActions().size())
            .sum();

        assertEquals(243, actionsInScenesDOM + actionsInSpeechesDOM);
    }

    @Test
    public void testCountAllActionsInPlaySAX() {
        long actionsInSpeechesSAX = playSAX.getActs().stream()
            .flatMap(act -> act.getSceneList().stream())
            .flatMap(scene -> scene.getSpeeches().stream())
            .mapToLong(speech -> speech.getActions().size())
            .sum();
        long actionsInScenesSAX = playSAX.getActs().stream()
            .flatMap(act -> act.getSceneList().stream())
            .mapToLong(scene -> scene.getActions().size())
            .sum();

        assertEquals(243, actionsInScenesSAX + actionsInSpeechesSAX);
    }

    @Test
    public void compareTagsCounterDOMandSAX(){
        assertEquals(playParserDOM.getTagsCounter(), playParserSAX.getTagsCounter());
    }

    @Test
    public void testExportToCsvSAX(){
        playParserSAX.exportToCSV(outputFile);
    }

    @Test
    public void testExportToCsvDOM(){
        playParserDOM.exportToCSV(outputFile);
    }
}
