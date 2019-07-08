package com.examples.school.controller;

import static java.util.Arrays.*;
import static org.mockito.Mockito.*;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.examples.school.model.Student;
import com.examples.school.repository.StudentRepository;
import com.examples.school.view.StudentView;

public class SchoolControllerTest {

	@Mock
	private StudentRepository studentRepository;

	@Mock
	private StudentView studentView;

	@InjectMocks
	private SchoolController schoolController;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void testAllStudents() {
		List<Student> students = asList(new Student());
		when(studentRepository.findAll())
			.thenReturn(students);
		schoolController.allStudents();
		verify(studentView)
			.showAllStudents(students);
	}
}
