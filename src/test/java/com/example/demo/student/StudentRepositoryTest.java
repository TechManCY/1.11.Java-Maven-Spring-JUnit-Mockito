package com.example.demo.student;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class StudentRepositoryTest {

    @Autowired
    private StudentRepository underTest;

    @AfterEach // not meaningful when using in-memory h2 database
    void teardown(){
        underTest.deleteAll();
    }

    @Test
    void testSelectExistsEmail() {
        //Given
        String email = "john@gmail.com";
        Student student = new Student(
                "john",
                email,
                Gender.FEMALE
        );
        underTest.save(student);

        //When
        boolean exists = underTest.selectExistsEmail(email);

        //Then
        assertThat(exists).isTrue();
    }


    @Test
    void testNotExistsEmail() {
        //Given
        String email = "john@gmail.com";

        //When
        boolean exists = underTest.selectExistsEmail(email);

        //Then
        assertThat(exists).isFalse();
    }
}