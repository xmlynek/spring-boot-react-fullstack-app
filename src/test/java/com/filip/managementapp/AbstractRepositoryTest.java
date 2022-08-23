package com.filip.managementapp;

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest(properties = {"application-test.properties"})
public abstract class AbstractRepositoryTest {
}
