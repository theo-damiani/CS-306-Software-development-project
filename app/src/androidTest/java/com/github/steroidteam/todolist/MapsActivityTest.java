package com.github.steroidteam.todolist;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import com.github.steroidteam.todolist.view.MapsActivity;
import org.junit.Rule;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class MapsActivityTest {
    @Rule
    public ActivityScenarioRule<MapsActivity> activityRule =
            new ActivityScenarioRule<>(MapsActivity.class);

    /**
     * @Test public void MarkerIsCorrectlyPlacedAtDefaultLocation() throws UiObjectNotFoundException
     * { UiDevice device = UiDevice.getInstance(getInstrumentation()); UiObject marker =
     * device.findObject(new UiSelector().descriptionContains("Sydney :)")); marker.click(); }
     */
}
