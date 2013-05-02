package org.roboguice.astroboy.provided;

import org.roboguice.astroboy.R;

import roboguice.activity.RoboActivity;
import roboguice.inject.ContentView;
import roboguice.inject.InjectFragment;

/**
 * An activity representing a list of Asteroids. 
 * <p>
 * The activity makes use of fragments. The list of items is a
 * {@link AsteroidListFragment}.
 */
@ContentView(R.layout.activity_asteroid_list)
public class AsteroidListActivity extends RoboActivity {
	@InjectFragment(R.id.asteroid_list) public AsteroidListFragment asteroidList;
	@InjectFragment(tag="fragtag") public AsteroidListFragment taggedAsteroidList;
}
