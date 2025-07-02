package com.example.demo.student;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;


import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.mockito.ArgumentMatchers.argThat;

@ExtendWith(SpringExtension.class)
@WebMvcTest(StudentController.class)
class StudentControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private StudentService studentService;

    @Autowired
    private ObjectMapper objectMapper;

    // Since integration testing , no need for: private StudentController underTest

    @Test
    void canGetAllStudents() throws Exception {
        //given
        List<Student> students = Arrays.asList(
                new Student("John Doe", "john@example.com", Gender.MALE),
                new Student("Jane Smith", "jane@example.com", Gender.FEMALE)
        );

        //when
        when(studentService.getAllStudents()).thenReturn(students);

        //then
        RequestBuilder request = MockMvcRequestBuilders.get("/api/v1/students");
        MvcResult result = mvc.perform(request).andReturn();

        String json = result.getResponse().getContentAsString();
        ObjectMapper mapper = new ObjectMapper();
        List<Student> actual = mapper.readValue(json, new TypeReference<List<Student>>() {});
        assertThat(actual).isEqualTo(students);
    }



    @Test
    void canAddStudent() throws Exception {
        //given
        Student student =
                new Student("Jane Smith", "jane@example.com", Gender.FEMALE);
        String studentJson = objectMapper.writeValueAsString(student);

        //when & then
        RequestBuilder request = MockMvcRequestBuilders.post("/api/v1/students")
                .contentType(APPLICATION_JSON)
                .content(studentJson);
        MvcResult result = mvc.perform(request).andExpect(status().isOk()).andReturn();

        verify(studentService).addStudent(argThat(s ->
                s.getName().equals(student.getName()) &&
                        s.getEmail().equals(student.getEmail()) &&
                        s.getGender() == student.getGender()
        ));
    }

    @Test
    void canDeleteStudent() throws Exception {
        //given
        Long studentId = 123L;

        //when & then
        RequestBuilder request = MockMvcRequestBuilders.delete("/api/v1/students/{studentId}", studentId);
        MvcResult result = mvc.perform(request).andExpect(status().isOk()).andReturn();
        verify(studentService).deleteStudent(studentId);

    }
}