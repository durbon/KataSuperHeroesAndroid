package com.karumi.katasuperheroes;

import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import com.karumi.katasuperheroes.di.MainComponent;
import com.karumi.katasuperheroes.di.MainModule;
import com.karumi.katasuperheroes.model.SuperHero;
import com.karumi.katasuperheroes.model.SuperHeroesRepository;
import com.karumi.katasuperheroes.ui.view.SuperHeroDetailActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import it.cosenonjaviste.daggermock.DaggerMockRule;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.karumi.katasuperheroes.matchers.ToolbarMatcher.onToolbarWithTitle;
import static org.hamcrest.CoreMatchers.allOf;
import static org.mockito.Mockito.when;

/**
 * Created by txema on 30/1/16.
 */
@RunWith(AndroidJUnit4.class) @LargeTest
public class SuperHeroDetailsTest {

    @Rule
    public DaggerMockRule<MainComponent> daggerRule =
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

    @Rule public IntentsTestRule<SuperHeroDetailActivity> activityRule =
            new IntentsTestRule<>(SuperHeroDetailActivity.class, true, false);

    @Mock
    SuperHeroesRepository repository;


    @Test
    public void showSuperHeroNameAsToolbarTitle(){
        SuperHero superHero = givenThereIsASuperHero(true);

        startActivity(superHero);

        onToolbarWithTitle(superHero.getName()).check(matches(isDisplayed()));
    }


    @Test
    public void showSuperHeroName(){
        SuperHero superHero = givenThereIsASuperHero(true);

        startActivity(superHero);

    }

    @Test
    public void showSuperHeroNameDescription(){
        SuperHero superHero = givenThereIsASuperHero(true);

        startActivity(superHero);

        onView(withText(superHero.getDescription())).check(matches(isDisplayed()));

    }

    private SuperHeroDetailActivity startActivity(SuperHero superHero) {
            Intent intent = new Intent();
            intent.putExtra("super_hero_name_key", superHero.getName());
            return activityRule.launchActivity(intent);
    }

    private SuperHero givenThereIsASuperHero(boolean isAvenger) {
        String superHeroName = "SuperHero";
        String superHeroPhoto = "https://i.annihil.us/u/prod/marvel/i/mg/c/60/55b6a28ef24fa.jpg";
        String superHeroDescription = "Super Hero Description";
        SuperHero superHero =
                new SuperHero(superHeroName, superHeroPhoto, isAvenger, superHeroDescription);
        when(repository.getByName(superHeroName)).thenReturn(superHero);
        return superHero;
    }

}
