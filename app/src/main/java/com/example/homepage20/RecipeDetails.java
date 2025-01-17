package com.example.homepage20;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class RecipeDetails extends AppCompatActivity {
    private String recipeName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_details);

        ViewPager2 viewPager = findViewById(R.id.view_pager);
        TabLayout tabLayout = findViewById(R.id.tabs);
        setupViewPager(viewPager, tabLayout);
    }

    private void setupViewPager(ViewPager2 viewPager, TabLayout tabLayout) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(this, recipeName); // Pass the recipe name to the adapter
        viewPager.setAdapter(adapter);

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            tab.setText(position == 0 ? "Recipe" : "Comments");
        }).attach();
    }

    private static class ViewPagerAdapter extends FragmentStateAdapter {
        private String recipeName;

        public ViewPagerAdapter(AppCompatActivity appCompatActivity, String recipeName) {
            super(appCompatActivity);
            this.recipeName = recipeName;
        }

        @Override
        public Fragment createFragment(int position) {
            Fragment fragment = position == 0 ? new RecipeFragment() : new CommentsFragment();
            if (position == 0) {
                Bundle args = new Bundle();
                args.putString("recipeName", recipeName);
                fragment.setArguments(args);
            }
            return fragment;
        }


        @Override
        public int getItemCount() {
            return 2; // We have two tabs
        }
    }
}