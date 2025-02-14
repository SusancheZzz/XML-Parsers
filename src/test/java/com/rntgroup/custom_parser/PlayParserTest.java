package com.rntgroup.custom_parser;

import com.rntgroup.custom_parser.dom.PlayParserDOM;
import com.rntgroup.custom_parser.entity.Play;
import com.rntgroup.custom_parser.sax.PlayParserSAX;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Execution(ExecutionMode.CONCURRENT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class PlayParserTest {

    private static final File inputFile = Path.of("src", "main", "resources", "static", "hamlet.xml").toFile();
    private File outputFile;
    private final File testCSV = Path.of("src", "test", "resources", "static", "testCSV").toFile();

    private static final String ANNOTATION =
        "Text placed in the public domain by Moby Lexical Tools, 1992.\n" +
            "SGML markup by Jon Bosak, 1992-1994.\n" +
            "XML version by Jon Bosak, 1996-1998.\n" +
            "This work may be freely copied and distributed worldwide.\n";
    private static final String AUTHOR = "William Shakespeare";
    private static final String TITLE = "The Tragedy of Hamlet, Prince of Denmark";


    public static Stream<Arguments> getParserDOM() {
        return Stream.of(Arguments.of(new PlayParserDOM(inputFile)));
    }

    public static Stream<Arguments> getParserSAX() {
        return Stream.of(Arguments.of(new PlayParserSAX(inputFile)));
    }

    public static Stream<Arguments> getPlaySAX() {
        return Stream.of(Arguments.of(new PlayParserSAX(inputFile).parse()));
    }

    public static Stream<Arguments> getPlayDOM() {
        return Stream.of(Arguments.of(new PlayParserDOM(inputFile).parse()));
    }

    @BeforeEach
    public void init(@TempDir Path tempDir) throws IOException {
        outputFile = Files.createTempFile(tempDir, "countTags", ".csv").toFile();
    }

    @ParameterizedTest
    @MethodSource({"getPlayDOM", "getPlaySAX"})
    public void compareTitle(Play play) {
        assertEquals(TITLE, play.getTitle());
    }

    @ParameterizedTest
    @MethodSource({"getPlayDOM", "getPlaySAX"})
    public void compareAuthor(Play play) {
        assertEquals(AUTHOR, play.getAuthor());
    }

    @ParameterizedTest
    @MethodSource({"getPlayDOM", "getPlaySAX"})
    public void compareAnnotation(Play play) {
        assertEquals(ANNOTATION, play.getAnnotation());
    }

    @ParameterizedTest
    @MethodSource({"getPlayDOM", "getPlaySAX"})
    public void testCountAct(Play play) {
        assertEquals(5, play.getActs().size());
    }

    @ParameterizedTest
    @MethodSource({"getPlayDOM", "getPlaySAX"})
    public void testCountPerson(Play play) {
        assertEquals(26, play.getPersonList().size());
    }

    @ParameterizedTest
    @MethodSource({"getPlayDOM", "getPlaySAX"})
    public void compareCountUniqueWordsPerson(Play play) {
        assertEquals(5193, play.getUniqueWordsPerson("Hamlet").size());
    }

    @ParameterizedTest
    @MethodSource({"getPlayDOM", "getPlaySAX"})
    public void compareCountScene(Play play) {
        long countScene = play.getActs().stream()
            .mapToLong(act -> act.getSceneList().size())
            .sum();

        assertEquals(20, countScene);
    }

    @ParameterizedTest
    @MethodSource({"getPlayDOM", "getPlaySAX"})
    public void compareCountSpeeches(Play play) {
        long countSpeeches = play.getActs().stream()
            .flatMap(act -> act.getSceneList().stream())
            .mapToLong(scene -> scene.getSpeeches().size())
            .sum();

        assertEquals(1138, countSpeeches);
    }

    @ParameterizedTest
    @MethodSource({"getPlayDOM", "getPlaySAX"})
    public void compareCountPersonWithNullGroup(Play play) {
        long countPersonWithNullGroup = play.getPersonList().entrySet().stream()
            .filter(pers -> Objects.isNull(pers.getValue().getGroupId()))
            .count();

        assertEquals(19, countPersonWithNullGroup);
    }

    @ParameterizedTest
    @MethodSource({"getPlayDOM", "getPlaySAX"})
    public void compareCountPersonWithGroupCourtiers(Play play) {
        long countPersonWithGroupCourtiers = play.getPersonList().entrySet().stream()
            .filter(pers -> "courtiers.".equals(pers.getValue().getGroupId()))
            .count();

        assertEquals(5, countPersonWithGroupCourtiers);
    }

    @ParameterizedTest
    @MethodSource({"getPlayDOM", "getPlaySAX"})
    public void compareCountActionsInScenes(Play play) {
        long countActionsInScenes = play.getActs().stream()
            .flatMap(act -> act.getSceneList().stream())
            .mapToLong(scene -> scene.getActions().size())
            .sum();

        assertEquals(134, countActionsInScenes);
    }

    @ParameterizedTest
    @MethodSource({"getPlayDOM", "getPlaySAX"})
    public void compareCountActionsInSpeeches(Play play) {
        long countActionsInSpeeches = play.getActs().stream()
            .flatMap(act -> act.getSceneList().stream())
            .flatMap(scene -> scene.getSpeeches().stream())
            .mapToLong(speech -> speech.getActions().size())
            .sum();

        assertEquals(109, countActionsInSpeeches);
    }

    @ParameterizedTest
    @MethodSource({"getPlayDOM", "getPlaySAX"})
    public void testCountAllActionsInPlay(Play play) {
        long actionsInSpeeches = play.getActs().stream()
            .flatMap(act -> act.getSceneList().stream())
            .flatMap(scene -> scene.getSpeeches().stream())
            .mapToLong(speech -> speech.getActions().size())
            .sum();
        long actionsInScenes = play.getActs().stream()
            .flatMap(act -> act.getSceneList().stream())
            .mapToLong(scene -> scene.getActions().size())
            .sum();

        assertEquals(243, actionsInScenes + actionsInSpeeches);
    }

    @ParameterizedTest
    @MethodSource({"getParserDOM", "getParserSAX"})
    public void testTagsCounter(PlayParser playParser) {
        playParser.parse();
        Map<String, Integer> tagsCounterMap = new HashMap<>();
        tagsCounterMap.put("LINE", 4014);
        tagsCounterMap.put("SPEAKER", 1150);
        tagsCounterMap.put("SPEECH", 1138);
        tagsCounterMap.put("STAGEDIR", 243);
        tagsCounterMap.put("TITLE", 27);
        tagsCounterMap.put("PERSONA", 26);
        tagsCounterMap.put("SCENE", 20);
        tagsCounterMap.put("ACT", 5);
        tagsCounterMap.put("P", 4);
        tagsCounterMap.put("GRPDESCR", 2);
        tagsCounterMap.put("PGROUP", 2);
        tagsCounterMap.put("FM", 1);
        tagsCounterMap.put("PLAYSUBT", 1);
        tagsCounterMap.put("PERSONAE", 1);
        tagsCounterMap.put("PLAY", 1);
        tagsCounterMap.put("SCNDESCR", 1);

        assertEquals(tagsCounterMap, playParser.getTagsCounter());
    }

    @ParameterizedTest
    @MethodSource({"getParserDOM", "getParserSAX"})
    public void testExportToCsv(PlayParser parser) throws IOException {
        parser.parse();
        parser.exportToCSV(outputFile);

        assertTrue(FileUtils.contentEqualsIgnoreEOL(testCSV, outputFile, "UTF-8"), "Files CSV differ");
    }

}
