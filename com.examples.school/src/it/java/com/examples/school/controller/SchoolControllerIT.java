package com.examples.school.controller;

import static org.mockito.Mockito.*;
import static java.util.Arrays.asList;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.examples.school.model.Student;
import com.examples.school.repository.StudentRepository;
import com.examples.school.repository.mongo.StudentMongoRepository;
import com.examples.school.view.StudentView;
import com.mongodb.MongoClient;

/**
 * Tests the integration of the controller with a MongoDB repository (the view
 * is still mocked).
 * 
 * Communicates with a MongoDB server on localhost; start MongoDB with Docker
 * with
 * 
 * <pre>
 * docker run -p 27017:27017 --rm mongo:4.0.5
 * </pre>
 * 
 * @author Lorenzo Bettini
 *
 */
public class SchoolControllerIT {

	@Mock
	private StudentView studentView;

	private StudentRepository studentRepository;

	private SchoolController schoolController;

	private static final String SCHOOL_DB_NAME = "school";
	private static final String STUDENT_COLLECTION_NAME = "student";

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		final MongoClient mongoClient = new MongoClient("localhost");
		studentRepository =
			new StudentMongoRepository(mongoClient,
					SCHOOL_DB_NAME, STUDENT_COLLECTION_NAME);
		// explicit empty the database
		mongoClient.getDatabase(SCHOOL_DB_NAME).drop();
		schoolController = new SchoolController(studentView, studentRepository);
	}

	@Test
	public void testAllStudents() {
		Student student = new Student("1", "test");
		studentRepository.save(student);
		schoolController.allStudents();
		verify(studentView)
			.showAllStudents(asList(student));
	}

	@Test
	public void testNewStudent() {
		Student student = new Student("1", "test");
		schoolController.newStudent(student);
		verify(studentView).studentAdded(student);
	}

}
