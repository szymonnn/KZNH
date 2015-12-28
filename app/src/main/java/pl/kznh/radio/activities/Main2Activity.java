package pl.kznh.radio.activities;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

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

    private Fragment mCurrentFragment;

    private FragmentManager mFragmentManager;

    private int mBackCounter = 0;

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
        mFragmentManager = getSupportFragmentManager();
        mFragmentManager.beginTransaction()
                .replace(R.id.container, getFragment(position))
                .commit();
    }

    private Fragment getFragment(int position) {
        switch(position){
            case 0:
                //home fragment
                mCurrentFragment = new HomeFragment();
                break;
            case 1:
                //calendar fragment
                mCurrentFragment = new CalendarFragment();
                break;
            case 2:
                //radio fragment
                mCurrentFragment = new RadioFragment();
                break;
            case 3:
                //records fragment
                mCurrentFragment = new RecordsFragment();
                break;
        }
        return mCurrentFragment;
    }

    @Override
    public void onBackPressed() {
        if (mCurrentFragment instanceof  RecordsFragment && mBackCounter == 0){
                ((RecordsFragment) mCurrentFragment).setEmptySearchParameters();
                mBackCounter++;
                Toast.makeText(Main2Activity.this, R.string.press_back_again, Toast.LENGTH_SHORT).show();
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mBackCounter = 0;
                    }
                }, 5000);
        } else {
            super.onBackPressed();
        }
    }
}
