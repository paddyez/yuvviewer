package org.yuvViewer;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import static org.assertj.core.api.Assertions.assertThat;

public class MainTest {

    private static final Logger logger = LogManager.getLogger(MainTest.class);

    @BeforeAll
    static void initAll() {
        logger.info("@BeforeAll - executes once before all test methods in this class");
    }

    @BeforeEach
    void init() {
        logger.info("@BeforeEach - executes before each test method in this class");
    }

    @Test
    void createMainFrameTest() {
        Main.createMainFrame();
        assertThat(true).isTrue();
    }
}
