package com.github.steroidteam.todolist;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.assertThat;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.espresso.web.assertion.WebViewAssertions.webMatches;
import static androidx.test.espresso.web.sugar.Web.onWebView;
import static androidx.test.espresso.web.webdriver.DriverAtoms.clearElement;
import static androidx.test.espresso.web.webdriver.DriverAtoms.findElement;
import static androidx.test.espresso.web.webdriver.DriverAtoms.getText;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

import android.os.Bundle;
import androidx.fragment.app.testing.FragmentScenario;
import androidx.navigation.Navigation;
import androidx.navigation.testing.TestNavHostController;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.web.webdriver.Locator;
import com.github.steroidteam.todolist.customviewactions.RichEditorGetHtml;
import com.github.steroidteam.todolist.database.Database;
import com.github.steroidteam.todolist.database.DatabaseFactory;
import com.github.steroidteam.todolist.model.notes.Note;
import com.github.steroidteam.todolist.view.NoteDisplayFragment;
import com.github.steroidteam.todolist.view.NoteSelectionFragment;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import org.hamcrest.MatcherAssert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class NoteDisplayFragmentTest {
    private FragmentScenario<NoteDisplayFragment> scenario;
    private final String FIXTURE_DEFAULT_NOTE_TITLE = "My note";
    private final String FIXTURE_DEFAULT_NOTE_CONTENT =
            "Lorem ipsum:"
                    + "<br>"
                    + "<ul>"
                    + "<li>dolor</li>"
                    + "<li>sit</li>"
                    + "</ul>"
                    + "<div>amet, <b>consectetur</b> adipiscing <strike>elyt</strike> elit.</div>"
                    + "<div><ol>"
                    + "<li><i>Duis</i></li>"
                    + "<li>eu</li>"
                    + "<li>velit</li>"
                    + "</ol>"
                    + "<div>porttitor, <u>varius quam quis</u>, suscipit erat.</div>"
                    + "</div>";

    @Mock Database databaseMock;

    @Before
    public void init() {
        Note note = new Note(FIXTURE_DEFAULT_NOTE_TITLE);
        note.setContent(FIXTURE_DEFAULT_NOTE_CONTENT);
        CompletableFuture<Note> noteFuture = new CompletableFuture<>();
        noteFuture.complete(note);
        doReturn(noteFuture).when(databaseMock).getNote(any(UUID.class));

        DatabaseFactory.setCustomDatabase(databaseMock);

        Bundle bundle = new Bundle();
        bundle.putString(NoteSelectionFragment.NOTE_ID_KEY, UUID.randomUUID().toString());
        scenario =
                FragmentScenario.launchInContainer(
                        NoteDisplayFragment.class, bundle, R.style.Theme_Asteroid);
    }

    @Test
    public void openMapsViewWorks() {
        // Set a test NavController in the fragment to check the navigation flow.
        TestNavHostController navController =
                new TestNavHostController(ApplicationProvider.getApplicationContext());

        scenario.onFragment(
                fragment -> {
                    navController.setGraph(R.navigation.mobile_navigation);
                    Navigation.setViewNavController(fragment.requireView(), navController);
                });

        // Scroll to the button in the toolbar (as it can be hidden if the screen is too narrow),
        // and click it.
        onView(withId(R.id.location_button)).perform(click());

        // Check that we are now in the map view.
        assertThat(navController.getCurrentDestination().getId(), equalTo(R.id.nav_map));
    }

    @Test
    public void openDrawingViewWorks() {
        // Set a test NavController in the fragment to check the navigation flow.
        TestNavHostController navController =
                new TestNavHostController(ApplicationProvider.getApplicationContext());

        scenario.onFragment(
                fragment -> {
                    navController.setGraph(R.navigation.mobile_navigation);
                    Navigation.setViewNavController(fragment.requireView(), navController);
                });

        // Scroll to the button in the toolbar (as it can be hidden if the screen is too narrow),
        // and click it.
        onView(withId(R.id.editor_action_drawing_btn)).perform(scrollTo(), click());

        // Check that we are now in the drawing view.
        assertThat(navController.getCurrentDestination().getId(), equalTo(R.id.nav_drawing));
    }

    @Test
    public void openAudioViewWorks() {
        // Set a test NavController in the fragment to check the navigation flow.
        TestNavHostController navController =
                new TestNavHostController(ApplicationProvider.getApplicationContext());

        scenario.onFragment(
                fragment -> {
                    navController.setGraph(R.navigation.mobile_navigation);
                    Navigation.setViewNavController(fragment.requireView(), navController);
                });

        // Scroll to the button in the toolbar (as it can be hidden if the screen is too narrow),
        // and click it.
        onView(withId(R.id.audio_button)).perform(click());

        // Check that we are now in the map view.
        assertThat(navController.getCurrentDestination().getId(), equalTo(R.id.nav_audio));
    }

    @Test
    public void noteIsRenderedProperly() {
        onView(withId(R.id.note_title)).check(matches(withText(FIXTURE_DEFAULT_NOTE_TITLE)));
        RichEditorGetHtml getHtml = new RichEditorGetHtml();
        onView(withId(R.id.notedisplay_text_editor)).perform(getHtml);
        MatcherAssert.assertThat(getHtml.contents, equalTo(FIXTURE_DEFAULT_NOTE_CONTENT));
    }

    @Test
    public void saveNoteWorks() {
        final String FIXTURE_MODIFIED_NOTE_CONTENT = "Some text";

        // Clear the text field.
        onWebView().withElement(findElement(Locator.ID, "editor")).perform(clearElement());

        // Tap the text field so it has the keyboard's focus, type the new contents of the note
        // and close the keyboard.
        onView(withId(R.id.notedisplay_text_editor))
                .perform(click())
                .perform(typeText(FIXTURE_MODIFIED_NOTE_CONTENT))
                .perform(closeSoftKeyboard());

        // Make sure that the note now contains the new body.
        onWebView()
                .withElement(findElement(Locator.ID, "editor"))
                .check(webMatches(getText(), containsString(FIXTURE_MODIFIED_NOTE_CONTENT)));

        // Hit the "save" button.
        onView(withId(R.id.notedisplay_save_btn)).perform(click());

        // Make sure that the note is updated in the database.
        ArgumentCaptor<Note> captor = ArgumentCaptor.forClass(Note.class);
        verify(databaseMock).putNote(any(), captor.capture());
        Note updatedNote = captor.getValue();
        assertThat(updatedNote.getTitle(), equalTo(FIXTURE_DEFAULT_NOTE_TITLE));
        assertThat(updatedNote.getContent(), equalTo(FIXTURE_MODIFIED_NOTE_CONTENT));
    }
}
