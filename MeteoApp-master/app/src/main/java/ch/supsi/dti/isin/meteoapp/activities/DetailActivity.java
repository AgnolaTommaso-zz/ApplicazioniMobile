package ch.supsi.dti.isin.meteoapp.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import java.util.List;
import java.util.UUID;

import ch.supsi.dti.isin.meteoapp.R;
import ch.supsi.dti.isin.meteoapp.fragments.DetailLocationFragment;
import ch.supsi.dti.isin.meteoapp.model.Location;
import ch.supsi.dti.isin.meteoapp.model.LocationsHolder;

public class DetailActivity extends AppCompatActivity {
    private static final String EXTRA_LOCATION_ID = "ch.supsi.dti.isin.meteoapp.location_id";
    private ViewPager viewPager;
    private List<Location> locationList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detailactivity);

        UUID uuid = (UUID) getIntent().getSerializableExtra(EXTRA_LOCATION_ID);

        locationList= LocationsHolder.get(this).getLocations();

        viewPager=findViewById(R.id.viewPager);

        FragmentManager fragmentManager=getSupportFragmentManager();
        viewPager.setAdapter(new FragmentStatePagerAdapter(fragmentManager) {
            @Override
            public Fragment getItem(int i) {
                Location location=locationList.get(i);
                setTitle(location.getName());
                return DetailLocationFragment.newInstance(location.getId());
            }

            @Override
            public int getCount() {
                return locationList.size();
            }
        });

        for(int i=0;i<locationList.size();i++){
            if(locationList.get(i).getId().equals(uuid)){
                viewPager.setCurrentItem(i);
                break;
            }
        }
    }

    public static Intent newIntent(Context packageContext, UUID locationId) {
        Intent intent = new Intent(packageContext, DetailActivity.class);
        intent.putExtra(EXTRA_LOCATION_ID, locationId);
        return intent;
    }

}
