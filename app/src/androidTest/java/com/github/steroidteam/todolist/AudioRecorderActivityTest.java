package com.github.steroidteam.todolist;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import com.github.steroidteam.todolist.view.AudioRecorderActivity;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class AudioRecorderActivityTest {

    @Rule
    public ActivityScenarioRule<AudioRecorderActivity> activityRule =
            new ActivityScenarioRule<>(AudioRecorderActivity.class);

    @Before
    public void before() {
        Intents.init();
    }

    @After
    public void after() {
        Intents.release();
    }

    @Test
    public void toastAndTextWhenRecordingShowsCorrectly() {
        onView(withId(R.id.record_button)).perform(click());
        onView(withText(R.string.is_recording))
                .inRoot(new ToastMatcher())
                .check(matches(isDisplayed()));
        onView(withId(R.id.record_text)).check(matches(withText(R.string.stop_record_button)));
        // Wait to avoid error with Toast messages that is still displayed
        waitFor(3000);
    }

    @Test
    public void toastWhenStopRecordingShowsCorrectly() {
        onView(withId(R.id.record_button)).perform(click());
        // Wait to avoid error that the first Toast message is still displayed
        waitFor(3000);
        onView(withId(R.id.record_button)).perform(click());
        onView(withText(R.string.stop_recording))
                .inRoot(new ToastMatcher())
                .check(matches(isDisplayed()));
        onView(withId(R.id.record_text)).check(matches(withText(R.string.record_button)));
    }

    @Test
    public void textWhenPlayShowsCorrectly() {
        onView(withId(R.id.play_text)).check(matches(withText(R.string.play_button)));
        onView(withId(R.id.play_button)).perform(click(), click());
        // Test that after the second click the text displays correctly "Play"
        onView(withId(R.id.play_text)).check(matches(withText(R.string.play_button)));
    }

    @Test
    public void textWhenPauseShowsCorrectly() {
        onView(withId(R.id.play_button)).perform(click());
        onView(withId(R.id.play_text)).check(matches(withText(R.string.pause_button)));
    }

    private void waitFor(int duration) {
        try {
            Thread.sleep(duration);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
