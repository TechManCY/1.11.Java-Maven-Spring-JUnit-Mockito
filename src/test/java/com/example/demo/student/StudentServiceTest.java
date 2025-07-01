package com.example.demo.student;

import com.example.demo.student.exception.BadRequestException;
import com.example.demo.student.exception.StudentNotFoundException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class StudentServiceTest {

    @Mock // This will create a mock instance of StudentRepository
    private StudentRepository studentRepository;
    //private AutoCloseable autoCloseable;// instead use @ExtendWith(MockitoExtension.class)
    private StudentService underTest;

    @BeforeEach
    void setUp(){
        // Initialize mocks created with @Mock annotations
        //autoCloseable = MockitoAnnotations.openMocks(this); // instead use @ExtendWith(MockitoExtension.class)
        // Pass the mock repository to the service under test
        underTest = new StudentService(studentRepository);
    }

    @AfterEach
    void tearDown() throws Exception {
        //autoCloseable.close(); // instead use @ExtendWith(MockitoExtension.class)
    }

    @Test
    void testGetAllStudents() {
        //when
        underTest.getAllStudents();
        //then
        verify(studentRepository).findAll();
        //verify(studentRepository).deleteAll(); -- this will fail
        // since testing from main.java: public List<Student> getAllStudents() {return studentRepository.findAll();}
        // this verify is to test flow from service to repository and since underTest.getAllStudents() == studentRepository).findAll(), this is not meaningful test;
        // more meaningful to test business logics in the service layer with assertThat
    }

    @Test
    void testAddStudent() {
        //Given
        Student student = new Student(
                "john",
                "john@gmail.com",
                Gender.FEMALE
        );
        //when
        underTest.addStudent(student);

        //then
        ArgumentCaptor<Student> studentArgumentCaptor =
                ArgumentCaptor.forClass(Student.class);
        verify(studentRepository)
                .save(studentArgumentCaptor.capture());
        Student captureStudent = studentArgumentCaptor.getValue();
        assertThat(captureStudent).isEqualTo(student);
    }

    @Test
    void willThrowWhenEmailsTaken() {
        //Given
        Student student = new Student(
                "john",
                "john@gmail.com",
                Gender.FEMALE
        );
        // Intercept the call to studentRepository.selectExistsEmail(student.getEmail()) and return true
        given(studentRepository.selectExistsEmail(anyString()))
                .willReturn(true);
        //when
        //then
        assertThatThrownBy(() ->underTest.addStudent(student))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Email " + student.getEmail() + " taken");

        // just to check that studentRepository.save is never done
        verify(studentRepository, never()).save(any());

    }

    @Test
    void testDeleteStudent() {
        //Given
        Long studentId = 1L;
        given(studentRepository.existsById(studentId))
                .willReturn(true);
        //when
        underTest.deleteStudent(studentId);
        //then
        verify(studentRepository).deleteById(studentId);

    }


    @Test
    void testNoStudentId() {
        //Given
        Long studentId = 1L;
        given(studentRepository.existsById(anyLong()))
                .willReturn(false);
        //when
        //then
        assertThatThrownBy(() ->underTest.deleteStudent(studentId))
                .isInstanceOf(StudentNotFoundException.class)
                .hasMessageContaining("Student with id " + studentId + " does not exists");

        verify(studentRepository, never()).deleteById(anyLong());

       }
}