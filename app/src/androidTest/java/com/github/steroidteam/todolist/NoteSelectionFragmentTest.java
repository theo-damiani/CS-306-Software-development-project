package com.github.steroidteam.todolist;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.swipeLeft;
import static androidx.test.espresso.action.ViewActions.swipeRight;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static androidx.test.espresso.contrib.RecyclerViewActions.scrollToPosition;
import static androidx.test.espresso.matcher.RootMatchers.isDialog;
import static androidx.test.espresso.matcher.ViewMatchers.assertThat;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.github.steroidteam.todolist.CustomMatchers.ItemCountIs;
import static com.github.steroidteam.todolist.CustomMatchers.atPositionCheckText;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;

import androidx.fragment.app.testing.FragmentScenario;
import androidx.navigation.Navigation;
import androidx.navigation.testing.TestNavHostController;
import androidx.test.core.app.ApplicationProvider;
import com.github.steroidteam.todolist.database.Database;
import com.github.steroidteam.todolist.database.DatabaseFactory;
import com.github.steroidteam.todolist.model.notes.Note;
import com.github.steroidteam.todolist.model.todo.TodoList;
import com.github.steroidteam.todolist.model.todo.TodoListCollection;
import com.github.steroidteam.todolist.view.NoteSelectionFragment;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class NoteSelectionFragmentTest {

    private final int NOTE_TITLE_LAYOUT_ID = R.id.layout_note_title;

    private final String NOTE_TITLE_1 = "Some random title";
    private final String NOTE_TITLE_2 = "ANOTHER TITLE";

    private FragmentScenario<NoteSelectionFragment> scenario;
    @Mock Database databaseMock;

    @Before
    public void init() {
        List<UUID> notes = Collections.singletonList(UUID.randomUUID());
        CompletableFuture<List<UUID>> notesFuture = new CompletableFuture<>();
        notesFuture.complete(notes);

        Note note = new Note(NOTE_TITLE_1);
        CompletableFuture<Note> noteFuture = new CompletableFuture<>();
        noteFuture.complete(note);

        TodoListCollection collection = new TodoListCollection();
        TodoList todoList = new TodoList("Some random title");
        collection.addUUID(UUID.randomUUID());
        collection.addUUID(UUID.randomUUID());

        CompletableFuture<TodoListCollection> todoListCollectionFuture = new CompletableFuture<>();
        CompletableFuture<TodoList> todoListFuture = new CompletableFuture<>();
        todoListCollectionFuture.complete(collection);
        todoListFuture.complete(todoList);

        CompletableFuture<Void> future = new CompletableFuture<>();
        future.complete(null);
        doReturn(future).when(databaseMock).removeNote(any(UUID.class));

        doReturn(notesFuture).when(databaseMock).getNotesList();
        doReturn(noteFuture).when(databaseMock).getNote(any(UUID.class));
        doReturn(noteFuture).when(databaseMock).putNote(any(UUID.class), any(Note.class));
        doReturn(noteFuture).when(databaseMock).updateNote(any(UUID.class), any(Note.class));
        doReturn(todoListFuture).when(databaseMock).getTodoList(any(UUID.class));
        doReturn(todoListCollectionFuture).when(databaseMock).getTodoListCollection();

        DatabaseFactory.setCustomDatabase(databaseMock);

        scenario =
                FragmentScenario.launchInContainer(
                        NoteSelectionFragment.class, null, R.style.Theme_Asteroid);
    }

    @Test
    public void openListWorks() {
        // Set a test NavController in the fragment to check the navigation flow.
        TestNavHostController navController =
                new TestNavHostController(ApplicationProvider.getApplicationContext());

        scenario.onFragment(
                fragment -> {
                    navController.setGraph(R.navigation.mobile_navigation);
                    Navigation.setViewNavController(fragment.requireView(), navController);
                });

        onView(withId(R.id.activity_noteselection_recycler))
                .perform(actionOnItemAtPosition(0, click()));

        // Check that we are now in the note display view.
        assertThat(navController.getCurrentDestination().getId(), equalTo(R.id.nav_note_display));
    }

    @Test
    public void cannotRenameNoteWithoutText() {
        onView(withId(R.id.activity_noteselection_recycler)).perform(scrollToPosition(0));

        onView(withId(R.id.activity_noteselection_recycler))
                .perform(actionOnItemAtPosition(0, swipeRight()));

        onView(withText(R.string.rename_note_suggestion)).check(matches(isDisplayed()));

        Note note = new Note(NOTE_TITLE_2);
        CompletableFuture<Note> noteFuture = new CompletableFuture<>();
        noteFuture.complete(note);

        // Change the title of the note that will be returned:
        doReturn(noteFuture).when(databaseMock).getNote(any(UUID.class));

        // because there shouldn't be a call to the database as we change nothing
        onView(withId(R.id.alert_dialog_edit_text)).inRoot(isDialog()).perform(clearText());
        onView(withId(android.R.id.button1)).inRoot(isDialog()).perform(click());

        onView(withText(R.string.rename_note_suggestion)).check(doesNotExist());

        onView(withId(R.id.activity_noteselection_recycler))
                .check(matches(atPositionCheckText(0, NOTE_TITLE_1, NOTE_TITLE_LAYOUT_ID)));
    }

    @Test
    public void cancelRenamingWorks() {
        onView(withId(R.id.activity_noteselection_recycler)).perform(scrollToPosition(0));

        onView(withId(R.id.activity_noteselection_recycler))
                .perform(actionOnItemAtPosition(0, swipeRight()));

        onView(withText(R.string.rename_note_suggestion)).check(matches(isDisplayed()));

        Note note = new Note(NOTE_TITLE_2);
        CompletableFuture<Note> noteFuture = new CompletableFuture<>();
        noteFuture.complete(note);

        // Change the title of the note that will be returned:
        doReturn(noteFuture).when(databaseMock).getNote(any(UUID.class));
        doReturn(noteFuture).when(databaseMock).updateNote(any(UUID.class), any(Note.class));

        // Pressing the negative button the title shouldn't change,
        // because there shouldn't be a call to the database as we change nothing (cancel renaming)
        // button2 = negative button
        onView(withId(R.id.alert_dialog_edit_text))
                .inRoot(isDialog())
                .perform(clearText(), typeText(NOTE_TITLE_2));
        onView(withId(android.R.id.button2)).inRoot(isDialog()).perform(click());

        onView(withText(R.string.rename_note_suggestion)).check(doesNotExist());

        onView(withId(R.id.activity_noteselection_recycler))
                .check(matches(atPositionCheckText(0, NOTE_TITLE_1, NOTE_TITLE_LAYOUT_ID)));
    }

    @Test
    public void renamingNoteWorks() {

        onView(withId(R.id.activity_noteselection_recycler))
                .perform(actionOnItemAtPosition(0, swipeRight()));

        onView(withId(R.id.alert_dialog_edit_text)).check(matches(isDisplayed()));

        Note note = new Note(NOTE_TITLE_2);
        CompletableFuture<Note> noteFuture = new CompletableFuture<>();
        noteFuture.complete(note);

        // Change the title of the note that will be returned:
        doReturn(noteFuture).when(databaseMock).getNote(any(UUID.class));
        doReturn(noteFuture).when(databaseMock).updateNote(any(UUID.class), any(Note.class));

        // there should be a call to the database as we change the title
        // button1 = positive button
        onView(withId(R.id.alert_dialog_edit_text))
                .inRoot(isDialog())
                .perform(clearText(), typeText(NOTE_TITLE_2));

        onView(withId(android.R.id.button1)).perform(click());

        onView(withId(R.id.alert_dialog_edit_text)).check(doesNotExist());

        onView(withId(R.id.activity_noteselection_recycler))
                .check(matches(atPositionCheckText(0, NOTE_TITLE_2, NOTE_TITLE_LAYOUT_ID)));
    }

    @Test
    public void cancelCreateNoteWorks() {

        onView(withId(R.id.create_note_button)).perform(click());

        onView(withText(R.string.add_note_suggestion)).check(matches(isDisplayed()));

        Note note = new Note(NOTE_TITLE_2);
        CompletableFuture<Note> noteFuture = new CompletableFuture<>();
        noteFuture.complete(note);

        // Change the title of the note that will be returned:
        doReturn(noteFuture).when(databaseMock).getNote(any(UUID.class));
        doReturn(noteFuture).when(databaseMock).putNote(any(UUID.class), any(Note.class));

        // button2 = negative button
        onView(withId(android.R.id.button2)).inRoot(isDialog()).perform(click());

        onView(withText(R.string.add_note_suggestion)).check(doesNotExist());

        // Check that the title didn't change
        onView(withId(R.id.activity_noteselection_recycler))
                .check(matches(atPositionCheckText(0, NOTE_TITLE_1, NOTE_TITLE_LAYOUT_ID)));

        // Check that it doesn't add a to-do
        onView(withId(R.id.activity_noteselection_recycler)).check(matches(ItemCountIs(1)));
    }

    @Test
    public void createNoteWorks() {

        onView(withId(R.id.create_note_button)).perform(click());

        onView(withText(R.string.add_note_suggestion)).check(matches(isDisplayed()));

        Note note = new Note(NOTE_TITLE_2);
        CompletableFuture<Note> noteFuture = new CompletableFuture<>();
        noteFuture.complete(note);

        // Change the title of the note that will be returned:
        doReturn(noteFuture).when(databaseMock).getNote(any(UUID.class));
        doReturn(noteFuture).when(databaseMock).putNote(any(UUID.class), any(Note.class));

        // Return 2 Notes
        List<UUID> notes = Arrays.asList(UUID.randomUUID(), UUID.randomUUID());
        CompletableFuture<List<UUID>> notesFuture = new CompletableFuture<>();
        notesFuture.complete(notes);
        doReturn(notesFuture).when(databaseMock).getNotesList();

        // button1 = positive button
        onView(withId(R.id.alert_dialog_edit_text))
                .inRoot(isDialog())
                .perform(clearText(), typeText(NOTE_TITLE_2));
        onView(withId(android.R.id.button1)).inRoot(isDialog()).perform(click());

        onView(withText(R.string.add_note_suggestion)).check(doesNotExist());

        onView(withId(R.id.activity_noteselection_recycler)).check(matches(ItemCountIs(2)));

        onView(withId(R.id.activity_noteselection_recycler))
                .check(matches(atPositionCheckText(0, NOTE_TITLE_2, NOTE_TITLE_LAYOUT_ID)));
    }

    @Test
    public void cancelDeleteNoteWorks() {

        onView(withId(R.id.activity_noteselection_recycler))
                .perform(actionOnItemAtPosition(0, swipeLeft()));

        onView(withText(R.string.delete_note_suggestion)).check(matches(isDisplayed()));

        // Return zero note (as if the button doesn't work and indeed delete note)
        List<UUID> notes = Collections.emptyList();
        CompletableFuture<List<UUID>> notesFuture = new CompletableFuture<>();
        notesFuture.complete(notes);
        doReturn(notesFuture).when(databaseMock).getNotesList();

        // button2 = negative button
        onView(withId(android.R.id.button2)).inRoot(isDialog()).perform(click());

        onView(withText(R.string.delete_note_suggestion)).check(doesNotExist());

        onView(withId(R.id.activity_noteselection_recycler)).check(matches(ItemCountIs(1)));
        onView(withId(R.id.activity_noteselection_recycler))
                .check(matches(atPositionCheckText(0, NOTE_TITLE_1, NOTE_TITLE_LAYOUT_ID)));
    }

    @Test
    public void deleteNoteWorks() {

        onView(withId(R.id.activity_noteselection_recycler))
                .perform(actionOnItemAtPosition(0, swipeLeft()));

        onView(withText(R.string.delete_note_suggestion)).check(matches(isDisplayed()));

        // Return zero note
        List<UUID> notes = Collections.emptyList();
        CompletableFuture<List<UUID>> notesFuture = new CompletableFuture<>();
        notesFuture.complete(notes);
        doReturn(notesFuture).when(databaseMock).getNotesList();

        // button1 = positive button
        onView(withId(android.R.id.button1)).inRoot(isDialog()).perform(click());

        onView(withText(R.string.delete_note_suggestion)).check(doesNotExist());

        onView(withId(R.id.activity_noteselection_recycler)).check(matches(ItemCountIs(0)));
    }
}
