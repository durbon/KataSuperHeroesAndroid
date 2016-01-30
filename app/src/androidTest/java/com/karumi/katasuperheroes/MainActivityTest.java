/*
 * Copyright (C) 2015 Karumi.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.karumi.katasuperheroes;

import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.NoMatchingViewException;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;
import android.view.View;

import com.karumi.katasuperheroes.di.MainComponent;
import com.karumi.katasuperheroes.di.MainModule;
import com.karumi.katasuperheroes.matchers.ToolbarMatcher;
import com.karumi.katasuperheroes.model.SuperHero;
import com.karumi.katasuperheroes.model.SuperHeroesRepository;
import com.karumi.katasuperheroes.recyclerview.RecyclerViewInteraction;
import com.karumi.katasuperheroes.ui.view.MainActivity;
import com.karumi.katasuperheroes.ui.view.SuperHeroDetailActivity;

import it.cosenonjaviste.daggermock.DaggerMockRule;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasExtra;
import static android.support.test.espresso.matcher.ViewMatchers.hasDescendant;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.karumi.katasuperheroes.matchers.RecyclerViewItemsCountMatcher.recyclerViewHasItemCount;
import static org.hamcrest.core.IsNot.not;
import static org.mockito.Mockito.when;

@RunWith(AndroidJUnit4.class) @LargeTest public class MainActivityTest {

    public static final String DUMMY_SUPERHERO = "Scarlet Witch";
    @Rule public DaggerMockRule<MainComponent> daggerRule =
      new DaggerMockRule<>(MainComponent.class, new MainModule()).set(
          new DaggerMockRule.ComponentSetter<MainComponent>() {
            @Override public void setComponent(MainComponent component) {
              SuperHeroesApplication app =
                  (SuperHeroesApplication) InstrumentationRegistry.getInstrumentation()
                      .getTargetContext()
                      .getApplicationContext();
              app.setComponent(component);
            }
          });

  @Rule public IntentsTestRule<MainActivity> activityRule =
      new IntentsTestRule<>(MainActivity.class, true, false);

  @Mock SuperHeroesRepository repository;

  @Test public void showsEmptyCaseIfThereAreNoSuperHeroes() {
    givenThereAreNoSuperHeroes();

    startActivity();

    onView(withText("¯\\_(ツ)_/¯")).check(matches(isDisplayed()));
  }

    @Test public void showHeroeswithoutEmptyTest(){
        givenSuperHeroes();

        startActivity();

        onView(withText("¯\\_(ツ)_/¯")).check(matches(not(isDisplayed())));
    }


    @Test public void showSuperHeroes(){

        givenSuperHeroes();

        startActivity();

        onView(withText("Scarlet Witch")).check(matches(isDisplayed()));
    }

    @Test public void showOnlyOneAvenger(){

        givenSuperHeroes();

        startActivity();

        onView(withId(R.id.recycler_view)).check(matches(recyclerViewHasItemCount(1)));
        onView(withId(R.id.iv_avengers_badge)).check(matches(not(isDisplayed())));
    }

    @Test public void showSeveralAvenger(){

        final List<SuperHero> superHeros = givenBulkSuperHeroes(10000);

        startActivity();

        RecyclerViewInteraction.<SuperHero>onRecyclerView(withId(R.id.recycler_view))
                .withItems(superHeros)
                .check(new RecyclerViewInteraction.ItemViewAssertion<SuperHero>() {
                    @Override
                    public void check(SuperHero item, View view, NoMatchingViewException e) {
                        matches(hasDescendant(withText(item.getName()))).check(view, e);
                    }
                });
    }

    @Test public void opensSuperDetailActivityTapped(){

        List<SuperHero> superHeros = givenBulkSuperHeroes(10);
        int superHeroIndex = 0;

        startActivity();

        onView(withId(R.id.recycler_view))
                .perform(RecyclerViewActions.actionOnItemAtPosition(superHeroIndex, click()));

        SuperHero selectedSuperHero = superHeros.get(superHeroIndex);
        intended(hasComponent(SuperHeroDetailActivity.class.getCanonicalName()));
        intended(hasExtra("super_hero_name_key", selectedSuperHero.getName()));
        ToolbarMatcher.onToolbarWithTitle(selectedSuperHero.getName());

    }


  private void givenThereAreNoSuperHeroes() {
    when(repository.getAll()).thenReturn(Collections.<SuperHero>emptyList());
  }

    private List<SuperHero> givenSuperHeroes(){

        SuperHero dummy = new SuperHero(DUMMY_SUPERHERO,
                "https://i.annihil.us/u/prod/marvel/i/mg/9/b0/537bc2375dfb9.jpg", false,
                "Scarlet Witch was born at the Wundagore base of the High Evolutionary, she and her twin "
                        + "brother Pietro were the children of Romani couple Django and Marya Maximoff. The "
                        + "High Evolutionary supposedly abducted the twins when they were babies and "
                        + "experimented on them, once he was disgusted with the results, he returned them to"
                        + " Wundagore, disguised as regular mutants.");

        List<SuperHero> superHeroList = new ArrayList<>();
        superHeroList.add(dummy);

        when(repository.getAll()).thenReturn(superHeroList);

        return superHeroList;
    }

    private List<SuperHero> givenBulkSuperHeroes(int count){
        List<SuperHero> superHeroList = new LinkedList<>();
        for (int i = 0; i <= count; i++) {
            SuperHero superHero = new SuperHero("name " + i, "https://i.annihil.us/u/prod/marvel/i/mg/9/b0/537bc2375dfb9.jpg", false,
                    "Scarlet Witch was born at the Wundagore base of the High Evolutionary, she and her twin "
                            + "brother Pietro were the children of Romani couple Django and Marya Maximoff. The "
                            + "High Evolutionary supposedly abducted the twins when they were babies and "
                            + "experimented on them, once he was disgusted with the results, he returned them to"
                            + " Wundagore, disguised as regular mutants.");
            superHeroList.add(superHero);

            when(repository.getByName("name " + i)).thenReturn(superHero);
        }
        when(repository.getAll()).thenReturn(superHeroList);
        return superHeroList;

    }

  private MainActivity startActivity() {
    return activityRule.launchActivity(null);
  }
}