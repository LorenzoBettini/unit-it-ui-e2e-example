package com.examples.school.view.swing;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;

import org.assertj.swing.annotation.GUITest;
import org.assertj.swing.core.matcher.JButtonMatcher;
import org.assertj.swing.core.matcher.JLabelMatcher;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.fixture.JTextComponentFixture;
import org.assertj.swing.junit.runner.GUITestRunner;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.examples.school.controller.SchoolController;
import com.examples.school.model.Student;

@RunWith(GUITestRunner.class)
public class StudentSwingViewTest extends AssertJSwingJUnitTestCase {

	private FrameFixture window;

	private StudentSwingView studentSwingView;

	@Mock
	private SchoolController schoolController;

	@Override
	protected void onSetUp() {
		MockitoAnnotations.initMocks(this);
		GuiActionRunner.execute(() -> {
			studentSwingView = new StudentSwingView();
			studentSwingView.setSchoolController(schoolController);
			return studentSwingView;
		});
		window = new FrameFixture(robot(), studentSwingView);
		window.show(); // shows the frame to test
	}

	@Test @GUITest
	public void testControlsInitialStates() {
		window.label(JLabelMatcher.withText("id"));
		window.textBox("idTextBox").requireEnabled();
		window.label(JLabelMatcher.withText("name"));
		window.textBox("nameTextBox").requireEnabled();
		window.button(JButtonMatcher.withText("Add")).requireDisabled();
		window.list("studentList");
	}

	@Test @GUITest
	public void testWhenIdAndNameAreNonEmptyThenAddButtonShouldBeEnabled() {
		window.textBox("idTextBox").enterText("1");
		window.textBox("nameTextBox").enterText("test");
		window.button(JButtonMatcher.withText("Add")).requireEnabled();
	}

	@Test
	public void testWhenEitherIdOrNameAreBlankThenAddButtonShouldBeDisabled() {
		JTextComponentFixture idTextBox = window.textBox("idTextBox");
		JTextComponentFixture nameTextBox = window.textBox("nameTextBox");

		idTextBox.enterText("1");
		nameTextBox.enterText(" ");
		window.button(JButtonMatcher.withText("Add")).requireDisabled();

		idTextBox.setText("");
		nameTextBox.setText("");

		idTextBox.enterText(" ");
		nameTextBox.enterText("test");
		window.button(JButtonMatcher.withText("Add")).requireDisabled();
	}

	@Test
	public void testsShowAllStudentsShouldAddStudentDescriptionsToTheList() {
		Student student1 = new Student("1", "test1");
		Student student2 = new Student("2", "test2");

		// exercise
		studentSwingView.showAllStudents(
			Arrays.asList(student1, student2));

		// verify
		String[] listContents = window.list().contents();
		assertThat(listContents)
			.containsExactly(student1.toString(),
				student2.toString());
	}

	@Test
	public void testShowErrorShouldShowTheMessageInTheErrorLabel() {
		Student student = new Student("1", "test1");
		studentSwingView.showError("error message", student);
		window.label("errorMessageLabel")
			.requireText("error message: " + student);
	}

	@Test
	public void testStudentAddedShouldAddTheStudentToTheListAndResetTheErrorLabel() {
		// exercise
		final Student student = new Student("1", "test1");
		studentSwingView.studentAdded(student);
		// verify
		String[] listContents = window.list().contents();
		assertThat(listContents).containsExactly(student.toString());
		window.label("errorMessageLabel").requireText(" ");
	}

	@Test
	public void testAddButtonShouldDelegateToSchoolControllerNewStudent() {
		// setup
		window.textBox("idTextBox").enterText("1");
		window.textBox("nameTextBox").enterText("test");
		// exercise
		window.button(JButtonMatcher.withText("Add")).click();
		// verify
		verify(schoolController).newStudent(new Student("1", "test"));
	}
}
