package com.filip.managementapp;

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@TestPropertySource(
        locations = "classpath:application-test.properties"
)
@AutoConfigureMockMvc
public abstract class AbstractControllerITest {
}
