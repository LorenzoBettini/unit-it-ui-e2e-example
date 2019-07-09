package com.examples.school.view.swing;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.InetSocketAddress;

import org.assertj.swing.annotation.GUITest;
import org.assertj.swing.core.matcher.JButtonMatcher;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.junit.runner.GUITestRunner;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.examples.school.controller.SchoolController;
import com.examples.school.model.Student;
import com.examples.school.repository.mongo.StudentMongoRepository;
import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;

import de.bwaldvogel.mongo.MongoServer;
import de.bwaldvogel.mongo.backend.memory.MemoryBackend;

@RunWith(GUITestRunner.class)
public class StudentSwingViewIT extends AssertJSwingJUnitTestCase {
	private static MongoServer server;
	private static InetSocketAddress serverAddress;

	private MongoClient mongoClient;

	private FrameFixture window;
	private StudentSwingView studentSwingView;
	private SchoolController schoolController;
	private StudentMongoRepository studentRepository;

	private static final String SCHOOL_DB_NAME = "school";
	private static final String STUDENT_COLLECTION_NAME = "student";

	@BeforeClass
	public static void setupServer() {
		server = new MongoServer(new MemoryBackend());
		// bind on a random local port
		serverAddress = server.bind();
	}

	@AfterClass
	public static void shutdownServer() {
		server.shutdown();
	}

	@Override
	protected void onSetUp() {
		mongoClient = new MongoClient(new ServerAddress(serverAddress));
		studentRepository =
			new StudentMongoRepository(mongoClient, SCHOOL_DB_NAME, STUDENT_COLLECTION_NAME);
		// explicit empty the database
		mongoClient.getDatabase(SCHOOL_DB_NAME).drop();
		GuiActionRunner.execute(() -> {
			studentSwingView = new StudentSwingView();
			schoolController = new SchoolController(studentSwingView, studentRepository);
			studentSwingView.setSchoolController(schoolController);
			return studentSwingView;
		});
		window = new FrameFixture(robot(), studentSwingView);
		window.show(); // shows the frame to test
	}

	@Override
	protected void onTearDown() {
		mongoClient.close();
	}

	@Test @GUITest
	public void testAllStudents() {
		// use the repository to add students to the database
		Student student1 = new Student("1", "test1");
		Student student2 = new Student("2", "test2");
		studentRepository.save(student1);
		studentRepository.save(student2);
		// use the controller's allStudents
		schoolController.allStudents();
		// and verify that the view's list is populated
		assertThat(window.list().contents())
			.containsExactly(student1.toString(), student2.toString());
	}

	@Test @GUITest
	public void testAddButton() {
		window.textBox("idTextBox").enterText("1");
		window.textBox("nameTextBox").enterText("test");
		window.button(JButtonMatcher.withText("Add")).click();
		final Student expectedStudent = new Student("1", "test");
		assertThat(window.list().contents())
			.containsExactly(expectedStudent.toString());
		// also verify the student is added to the database
		assertThat(studentRepository.findById("1"))
			.isEqualTo(expectedStudent);
	}

}
