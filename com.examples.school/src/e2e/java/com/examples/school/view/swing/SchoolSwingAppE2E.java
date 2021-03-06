package com.examples.school.view.swing;

import static org.assertj.core.api.Assertions.*;
import static org.assertj.swing.launcher.ApplicationLauncher.*;

import javax.swing.JFrame;

import org.assertj.swing.annotation.GUITest;
import org.assertj.swing.core.GenericTypeMatcher;
import org.assertj.swing.core.matcher.JButtonMatcher;
import org.assertj.swing.finder.WindowFinder;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.junit.runner.GUITestRunner;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.bson.Document;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.testcontainers.containers.GenericContainer;

import com.mongodb.MongoClient;

@RunWith(GUITestRunner.class)
public class SchoolSwingAppE2E extends AssertJSwingJUnitTestCase {

	@SuppressWarnings("rawtypes")
	@ClassRule
	public static final GenericContainer mongo =
		new GenericContainer("mongo:4.0.5") 
			.withExposedPorts(27017);

	private static final String DB_NAME = "test-db";
	private static final String COLLECTION_NAME = "test-collection";

	private static final String STUDENT_FIXTURE_1_ID = "1";
	private static final String STUDENT_FIXTURE_1_NAME = "first student";
	private static final String STUDENT_FIXTURE_2_ID = "2";
	private static final String STUDENT_FIXTURE_2_NAME = "second student";

	private MongoClient mongoClient;

	private FrameFixture window;

	@Override
	protected void onSetUp() {
		String containerIpAddress = mongo.getContainerIpAddress();
		Integer mappedPort = mongo.getMappedPort(27017);
		mongoClient = new MongoClient(containerIpAddress, mappedPort);
		// always start with an empty database
		mongoClient.getDatabase(DB_NAME).drop();
		// add some students to the database
		addTestStudentToDatabase(STUDENT_FIXTURE_1_ID, STUDENT_FIXTURE_1_NAME);
		addTestStudentToDatabase(STUDENT_FIXTURE_2_ID, STUDENT_FIXTURE_2_NAME);
		// start the Swing application
		application("com.examples.school.app.swing.SchoolSwingApp")
			.withArgs(
				"--mongo-host=" + containerIpAddress,
				"--mongo-port=" + mappedPort.toString(),
				"--db-name=" + DB_NAME,
				"--db-collection=" + COLLECTION_NAME
			)
			.start();
		// get a reference of its JFrame
		window = WindowFinder.findFrame(new GenericTypeMatcher<JFrame>(JFrame.class) {
			@Override
			protected boolean isMatching(JFrame frame) {
				return "Student View".equals(frame.getTitle()) && frame.isShowing();
			}
		}).using(robot());
	}

	@Override
	protected void onTearDown() {
		mongoClient.close();
	}

	@Test @GUITest
	public void testOnStartAllDatabaseElementsAreShown() {
		assertThat(window.list().contents())
			.anySatisfy(e -> assertThat(e).contains(STUDENT_FIXTURE_1_ID, STUDENT_FIXTURE_1_NAME))
			.anySatisfy(e -> assertThat(e).contains(STUDENT_FIXTURE_2_ID, STUDENT_FIXTURE_2_NAME));
	}

	@Test @GUITest
	public void testAddButtonSuccess() {
		window.textBox("idTextBox").enterText("10");
		window.textBox("nameTextBox").enterText("new student");
		window.button(JButtonMatcher.withText("Add")).click();
		assertThat(window.list().contents())
			.anySatisfy(e -> assertThat(e).contains("10", "new student"));
	}

	@Test @GUITest
	public void testAddButtonError() {
		window.textBox("idTextBox").enterText(STUDENT_FIXTURE_1_ID);
		window.textBox("nameTextBox").enterText("new one");
		window.button(JButtonMatcher.withText("Add")).click();
		assertThat(window.label("errorMessageLabel").text())
			.contains(STUDENT_FIXTURE_1_ID, STUDENT_FIXTURE_1_NAME);
	}

	private void addTestStudentToDatabase(String id, String name) {
		mongoClient
			.getDatabase(DB_NAME)
			.getCollection(COLLECTION_NAME)
			.insertOne(
				new Document()
					.append("id", id)
					.append("name", name));
	}

}
