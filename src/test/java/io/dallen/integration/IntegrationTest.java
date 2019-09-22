package io.dallen.integration;

import static org.junit.Assert.assertEquals;

public class IntegrationTest {

    @org.junit.Test
    public void helloWorld() {
        IntegrationTestHarness harness = new IntegrationTestHarness("HelloWorld");

        IntegrationTestHarness.TestResult actual = harness.run();
        IntegrationTestHarness.TestResult expected = new IntegrationTestHarness.TestResult(
                0, "Hello World!\n", ""
        );
        assertEquals(expected, actual);
    }

    @org.junit.Test
    public void loops() {
        IntegrationTestHarness harness = new IntegrationTestHarness("Loops");

        IntegrationTestHarness.TestResult actual = harness.run();
        IntegrationTestHarness.TestResult expected = new IntegrationTestHarness.TestResult(
                2, "while\nwhile\nloop\nloop\nloop\nloop\nloop\n", ""
        );
        assertEquals(expected, actual);
    }

    @org.junit.Test
    public void classes() {
        IntegrationTestHarness harness = new IntegrationTestHarness("Classes");

        IntegrationTestHarness.TestResult actual = harness.run();
        IntegrationTestHarness.TestResult expected = new IntegrationTestHarness.TestResult(
                16, "Dave\n", ""
        );
        assertEquals(expected, actual);
    }

    @org.junit.Test
    public void generics() {
        IntegrationTestHarness harness = new IntegrationTestHarness("Generics");

        IntegrationTestHarness.TestResult actual = harness.run();
        IntegrationTestHarness.TestResult expected = new IntegrationTestHarness.TestResult(
                5, "My name Dave!\n", ""
        );
        assertEquals(expected, actual);
    }

    @org.junit.Test
    public void tryCatch() {
        IntegrationTestHarness harness = new IntegrationTestHarness("TryCatch");

        IntegrationTestHarness.TestResult actual = harness.run();
        IntegrationTestHarness.TestResult expected = new IntegrationTestHarness.TestResult(
                1, "Oh no!\nError message\nWe resume here!\nTop Level Error Caught! Message: Invalid access to storage!\n", ""
        );
        assertEquals(expected, actual);
    }

    @org.junit.Test
    public void optional() {
        IntegrationTestHarness harness = new IntegrationTestHarness("Optional");

        IntegrationTestHarness.TestResult actual = harness.run();
        IntegrationTestHarness.TestResult expected = new IntegrationTestHarness.TestResult(
                5, "", ""
        );
        assertEquals(expected, actual);
    }

    @org.junit.Test
    public void imports() {
        IntegrationTestHarness harness = new IntegrationTestHarness("Import");

        IntegrationTestHarness.TestResult actual = harness.run();
        IntegrationTestHarness.TestResult expected = new IntegrationTestHarness.TestResult(
                5, "", ""
        );
        assertEquals(expected, actual);
    }

    @org.junit.Test
    public void deconstruction() {
        IntegrationTestHarness harness = new IntegrationTestHarness("Deconstruction");

        IntegrationTestHarness.TestResult actual = harness.run();
        IntegrationTestHarness.TestResult expected = new IntegrationTestHarness.TestResult(
                15, "Name:James\nJames\n10th\n", ""
        );
        assertEquals(expected, actual);
    }

    @org.junit.Test
    public void flowControl() {
        IntegrationTestHarness harness = new IntegrationTestHarness("FlowControl");

        IntegrationTestHarness.TestResult actual = harness.run();
        IntegrationTestHarness.TestResult expected = new IntegrationTestHarness.TestResult(
                0, "i is gt 5!\ni is lt 5!\ni is 10!\ni is 15 or 14\n", ""
        );
        assertEquals(expected, actual);
    }
}
