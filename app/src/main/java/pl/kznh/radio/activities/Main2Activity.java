package pl.kznh.radio.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;

import pl.kznh.radio.R;
import pl.kznh.radio.fragments.CalendarFragment;
import pl.kznh.radio.fragments.HomeFragment;
import pl.kznh.radio.fragments.NavigationDrawerFragment;
import pl.kznh.radio.fragments.RadioFragment;
import pl.kznh.radio.fragments.RecordsFragment;

public class Main2Activity extends AppCompatActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, getFragment(position))
                .commit();
    }

    private Fragment getFragment(int position) {
        switch(position){
            case 0:
                //home fragment
                return new HomeFragment();
            case 1:
                //calendar fragment
                return new CalendarFragment();
            case 2:
                //radio fragment
                return new RadioFragment();
            case 3:
                //records fragment
                return new RecordsFragment();
        }
        return null;
    }
}
