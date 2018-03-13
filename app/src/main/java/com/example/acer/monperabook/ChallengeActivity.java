package com.example.acer.monperabook;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.acer.monperabook.CustomAdapter.Challenge;
import com.example.acer.monperabook.Singleton.AppSingleton;
import com.example.acer.monperabook.Slider.CardFragmentPagerAdapter;
import com.example.acer.monperabook.Slider.ChallengePagerAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Azhary Arliansyah on 25/02/2018.
 */

public class ChallengeActivity extends AppCompatActivity {

    public static final String TAG = "ChallengeActivity";
    private ViewPager mViewPager;
    private ChallengePagerAdapter mChallengePagerAdapter;
    private CardFragmentPagerAdapter mCardFragmentPagerAdapter;
    private Toolbar toolbar;
    private Button prevButton;
    private Button nextButton;
    private Button submitButton;
    private String mEndpoint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_challenge);
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.colorActionBarContent));
        mEndpoint = getString(R.string.server_ip);
        mViewPager = (ViewPager) findViewById(R.id.viewPager);
        mChallengePagerAdapter = new ChallengePagerAdapter(this);

        getQuestion();

    }

    private void getQuestion() {

        final String REQUEST_TAG = "GET_QUESTION";
        StringBuilder requestURL = new StringBuilder();
        requestURL.append(mEndpoint);
        requestURL.append("pertanyaan/get_pertanyaan");
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, requestURL.toString(), null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            JSONArray questions = response.getJSONArray("data");
                            for (int i = 0; i < questions.length(); i++) {
                                JSONArray answers = questions.getJSONObject(i).getJSONArray("jawaban");
                                List<String> answerList = new ArrayList<>();
                                for (int j = 0; j < answers.length(); j++) {
                                    answerList.add(answers.getJSONObject(i).getString("jawaban"));
                                }
                                mChallengePagerAdapter.addChallenge(new Challenge(questions.getJSONObject(i)
                                        .getString("pertanyaan"), answerList));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        mViewPager.setAdapter(mChallengePagerAdapter);
                        mViewPager.setOffscreenPageLimit(3);
                        mChallengePagerAdapter.notifyDataSetChanged();

                        final int viewPagerSize = mChallengePagerAdapter.getCount();
                        prevButton = (Button) findViewById(R.id.prevButton);
                        nextButton = (Button) findViewById(R.id.nextButton);
                        submitButton = (Button) findViewById(R.id.submitButton);

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

                        submitButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent challengeFinishIntent = new Intent(ChallengeActivity.this, ChallengeFinishActivity.class);
                                startActivity(challengeFinishIntent);
                            }
                        });
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        String msg = error.getMessage();
                        if (msg != null) {
                            Log.e(TAG, msg);
                        }
                    }
                }
        );
        AppSingleton.getInstance(getApplicationContext()).addToRequestQueue(request, REQUEST_TAG);

    }

}
