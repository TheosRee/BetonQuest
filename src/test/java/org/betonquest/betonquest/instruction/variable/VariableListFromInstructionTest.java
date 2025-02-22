package org.betonquest.betonquest.instruction.variable;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.Variable;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.config.DefaultConfigAccessorFactory;
import org.betonquest.betonquest.config.quest.QuestPackageImpl;
import org.betonquest.betonquest.id.EventID;
import org.betonquest.betonquest.id.NoID;
import org.betonquest.betonquest.instruction.Instruction;
import org.betonquest.betonquest.instruction.argument.IDArgument;
import org.betonquest.betonquest.logger.util.BetonQuestLoggerService;
import org.betonquest.betonquest.quest.registry.processor.VariableProcessor;
import org.bukkit.configuration.InvalidConfigurationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Test for getting a {@link VariableList} from a {@link Instruction}.
 */
@ExtendWith(BetonQuestLoggerService.class)
@ExtendWith(MockitoExtension.class)
class VariableListFromInstructionTest {

    /**
     * The QuestPackage used for generating instructions.
     */
    private QuestPackage questPackage;

    /**
     * The variable processor to create variables.
     */
    private VariableProcessor variableProcessor;

    private QuestPackage setupQuestPackage(final BetonQuestLoggerFactory factory, final BetonQuestLogger logger, final Path questPackagesDirectory)
            throws IOException, InvalidConfigurationException {
        final Path packageDirectory = questPackagesDirectory.resolve("test");
        if (!packageDirectory.toFile().mkdir()) {
            throw new IOException("Failed to create test package directory.");
        }
        final File packageConfigFile = packageDirectory.resolve("package.yml").toFile();
        if (!packageConfigFile.createNewFile()) {
            throw new IOException("Failed to create test package main configuration file.");
        }
        final QuestPackageImpl test = new QuestPackageImpl(logger, new DefaultConfigAccessorFactory(factory, logger), "test", packageConfigFile, Collections.emptyList());
        test.applyQuestTemplates(Map.of());
        test.getConfig().set("events.a", "?");
        test.getConfig().set("events.b", "?");
        test.getConfig().set("events.c", "?");
        return test;
    }

    private VariableProcessor setUpProcessor() {
        final VariableProcessor processor = mock(VariableProcessor.class);
        when(BetonQuest.getInstance().getVariableProcessor()).thenReturn(processor);
        return processor;
    }

    private Instruction instructionFromString(final String instruction) throws QuestException {
        return new Instruction(questPackage, new NoID(questPackage), instruction);
    }

    @BeforeEach
    void prepareQuestPackage(final BetonQuestLoggerFactory factory, final BetonQuestLogger logger, @TempDir final Path questPackagesDirectory)
            throws IOException, InvalidConfigurationException {
        questPackage = setupQuestPackage(factory, logger, questPackagesDirectory);
        variableProcessor = setUpProcessor();
    }

    @Test
    void constructNonBackedList() throws QuestException {
        final Instruction instruction = instructionFromString("first a,z,c");
        assertThrows(QuestException.class, () -> instruction.get(IDArgument.ofList(EventID::new)),
                "Non existing event ID should throw an exception when validating");
    }

    @Test
    void constructBackedList() throws QuestException {
        final Instruction instruction = instructionFromString("first a,c");
        assertDoesNotThrow(() -> instruction.get(IDArgument.ofList(EventID::new)),
                "Getting existing variables should not fail");
    }

    @Test
    void constructEmptyList() throws QuestException {
        final Instruction instruction = instructionFromString("first ,,");
        final VariableList<EventID> list = assertDoesNotThrow(() -> instruction.get(IDArgument.ofList(EventID::new)),
                "Parsing an empty list should not fail");
        assertDoesNotThrow(() -> list.getValue(null), "Empty list should not fail getting values");
    }

    @Test
    void constructBackedListWithVariable() throws QuestException {
        final Instruction instruction = instructionFromString("first a,%bVar%,c");
        assertDoesNotThrow(() -> instruction.get(IDArgument.ofList(EventID::new)),
                "Validating existing variables should not fail");
    }

    @Test
    void getNonBackedVariableFromInstruction() throws QuestException {
        final Instruction instruction = instructionFromString("first a,%bVar%,z,c");
        assertThrows(QuestException.class, () -> instruction.get(IDArgument.ofList(EventID::new)),
                "Validating non-existing constant with also a variable should fail");
    }

    @Test
    void getListWithBackedVariable() throws QuestException {
        final Instruction instruction = instructionFromString("first a,%bVar%,c");
        final Variable variable = mock(Variable.class);
        when(variable.getValue(any())).thenReturn("b");
        when(variableProcessor.create(questPackage, "%bVar%")).thenReturn(variable);
        final VariableList<EventID> list = assertDoesNotThrow(() -> instruction.get(IDArgument.ofList(EventID::new)),
                "Validating existing variables should not fail");
        assertDoesNotThrow(() -> list.getValue(null), "Getting existing variable should not fail");
    }

    @Test
    void constructListWithNonBackedVariable() throws QuestException {
        when(variableProcessor.create(questPackage, "%otherVar%")).thenThrow(new QuestException("The variable does not exist"));
        final Instruction instruction = instructionFromString("first a,%otherVar%,c");
        assertThrows(QuestException.class, () -> instruction.get(IDArgument.ofList(EventID::new)),
                "Parsing non-existing variable should fail");
    }
}
