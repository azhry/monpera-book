package com.example.acer.monperabook;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.example.acer.monperabook.CustomAdapter.Challenge;
import com.example.acer.monperabook.Slider.CardFragmentPagerAdapter;
import com.example.acer.monperabook.Slider.ChallengePagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Azhary Arliansyah on 25/02/2018.
 */

public class ChallengeActivity extends AppCompatActivity {

    public static final String TAG = "PROJECT.ChallengeActivity";
    private ViewPager mViewPager;
    private ChallengePagerAdapter mChallengePagerAdapter;
    private CardFragmentPagerAdapter mCardFragmentPagerAdapter;

    private Button prevButton;
    private Button nextButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_challenge);

        mViewPager = (ViewPager) findViewById(R.id.viewPager);
        mChallengePagerAdapter = new ChallengePagerAdapter(this);

        List<String> answers = new ArrayList<>();
        answers.add("Answer 1");
        answers.add("Answer 2");
        answers.add("Answer 3");
        answers.add("Answer 4");
        mChallengePagerAdapter.addChallenge(new Challenge("Question 1", answers));
        mChallengePagerAdapter.addChallenge(new Challenge("Question 2", answers));

        mViewPager.setAdapter(mChallengePagerAdapter);
        mViewPager.setOffscreenPageLimit(3);
        mChallengePagerAdapter.notifyDataSetChanged();

        final int viewPagerSize = mChallengePagerAdapter.getCount();
        prevButton = (Button) findViewById(R.id.prevButton);
        nextButton = (Button) findViewById(R.id.nextButton);

        prevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mViewPager.getCurrentItem() - 1 >= 0) {
                    mViewPager.setCurrentItem(mViewPager.getCurrentItem() - 1, true);
                }
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mViewPager.getCurrentItem() + 1 < viewPagerSize) {
                    mViewPager.setCurrentItem(mViewPager.getCurrentItem() + 1, true);
                }
            }
        });
    }

}
