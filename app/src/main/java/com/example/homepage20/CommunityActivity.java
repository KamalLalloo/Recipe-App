package com.example.homepage20;

import android.os.Bundle;
import android.content.Intent;
import android.view.MenuItem;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class CommunityActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_community);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.container), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Setup BottomNavigationView
        BottomNavigationView navView = findViewById(R.id.navigation);
        navView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.navigation_home) {
                    startActivity(new Intent(CommunityActivity.this, MainActivity.class));
                } else if (id == R.id.navigation_meal_adder) {
                    startActivity(new Intent(CommunityActivity.this, MealAdder.class));
                } else if (id == R.id.navigation_grocery) {
                    startActivity(new Intent(CommunityActivity.this, GroceryActivity.class));
                } else if (id == R.id.navigation_community) {
                    // This is the current activity, do nothing or refresh
                }
                return true;
            }
        });
        navView.setSelectedItemId(R.id.navigation_community); // Highlight the current item

        // Setup ViewPager2 and TabLayout
        ViewPager2 viewPager = findViewById(R.id.view_pager);
        TabLayout tabLayout = findViewById(R.id.tabs);
        setupViewPager(viewPager, tabLayout);
    }

    private void setupViewPager(ViewPager2 viewPager, TabLayout tabLayout) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(this);
        viewPager.setAdapter(adapter);

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            if (position == 0) {
                tab.setText("Add Friends");
            } else if (position == 1) {
                tab.setText("Feed");
            } else if (position == 2) {
                tab.setText("Requests");
            }
        }).attach();
        viewPager.setCurrentItem(1, false);
    }

    private static class ViewPagerAdapter extends FragmentStateAdapter {

        public ViewPagerAdapter(AppCompatActivity appCompatActivity) {
            super(appCompatActivity);
        }

        @Override
        public Fragment createFragment(int position) {
            if (position == 0) {
                return new AddFriendsFragment();
            } else if (position == 1) {
                return new FeedFragment();
            } else if (position == 2) {
                return new RequestsFragment();
            } else {
                throw new IllegalStateException("Unexpected position: " + position);
            }
        }

        @Override
        public int getItemCount() {
            return 3; // We have three tabs
        }
    }
}