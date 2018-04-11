package com.example.acer.monperabook;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.example.acer.monperabook.CustomAdapter.Answer;
import com.example.acer.monperabook.CustomAdapter.Challenge;
import com.example.acer.monperabook.SQLite.DBHelper;
import com.example.acer.monperabook.Singleton.AppSingleton;
import com.example.acer.monperabook.Slider.CardFragmentPagerAdapter;
import com.example.acer.monperabook.Slider.ChallengePagerAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Azhary Arliansyah on 25/02/2018.
 */

public class ChallengeActivity extends AppCompatActivity {

    public static final String TAG = "ChallengeActivity";
    private ViewPager mViewPager;
    private ChallengePagerAdapter mChallengePagerAdapter;
    private Toolbar toolbar;
    private Button prevButton;
    private Button nextButton;
    private Button submitButton;
    private String mEndpoint;
    private Context mContext;
    private DBHelper db;
    private RelativeLayout noChallengeLayout;
    private Button scanButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_challenge);
        mContext = this;
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.colorActionBarContent));
        mEndpoint = getString(R.string.server_ip);
        mViewPager = (ViewPager) findViewById(R.id.viewPager);
        mChallengePagerAdapter = new ChallengePagerAdapter(this);
        db = new DBHelper(mContext);
        prevButton = (Button) findViewById(R.id.prevButton);
        nextButton = (Button) findViewById(R.id.nextButton);
        submitButton = (Button) findViewById(R.id.submitButton);

        noChallengeLayout = (RelativeLayout) findViewById(R.id.noChallengeLayout);
        scanButton = (Button) findViewById(R.id.scanButton);
        scanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent scanIntent = new Intent(ChallengeActivity.this, CameraActivity.class);
                startActivity(scanIntent);
            }
        });
        noChallengeLayout.setVisibility(View.GONE);
        submitButton.setVisibility(View.GONE);

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
                            final JSONArray questions = response.getJSONArray("data");
                            int numQuestion = 0;
                            for (int i = 0; i < questions.length(); i++) {
                                List<Answer> answerList = new ArrayList<>();
                                Cursor record = db.select("question",
                                        "kode_artifak='"
                                                + questions.getJSONObject(i).getJSONObject("pertanyaan").getString("kode_artifak") + "'");
                                if (record.moveToFirst()) {
                                    noChallengeLayout.setVisibility(View.VISIBLE);
                                    JSONArray answers = questions.getJSONObject(i).getJSONArray("jawaban");
                                    for (int j = 0; j < answers.length(); j++) {
                                        answerList.add(new Answer(answers.getJSONObject(j).getInt("id_pertanyaan"),
                                                answers.getJSONObject(j).getString("jawaban"),
                                                answers.getJSONObject(j).getInt("status")));
                                    }
                                    JSONObject question = new JSONObject(questions.getJSONObject(i)
                                            .getString("pertanyaan"));
                                    mChallengePagerAdapter.addChallenge(new Challenge(question.getInt("id_pertanyaan"),
                                            question.getString("pertanyaan"),
                                            answerList));
                                    numQuestion++;

                                } else {
                                    noChallengeLayout.setVisibility(View.GONE);
                                }
                            }

                            mViewPager.setAdapter(mChallengePagerAdapter);
                            mViewPager.setOffscreenPageLimit(3);
                            mChallengePagerAdapter.notifyDataSetChanged();

                            final int viewPagerSize = mChallengePagerAdapter.getCount();

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

                            attachCheckEvent(numQuestion);
                            final int finalNumQuestion = numQuestion;

                            submitButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                    final ArrayList<String> selectedRadioButton = getSelectedRadioButton();
                                    final ArrayList<Object> questionId = getSelectedQuestion();

                                    if (selectedRadioButton.size() == finalNumQuestion) {
                                        String requestUrl = mEndpoint + "pertanyaan/submit-jawaban";
                                        StringRequest submitAnswers =  new StringRequest(Request.Method.POST, requestUrl,
                                                new Response.Listener<String>() {
                                                    @Override
                                                    public void onResponse(String response) {
                                                        try {
                                                            JSONObject res = new JSONObject
                                                                        (response);
                                                            boolean error = res.getBoolean("error");
                                                            if (!error) {
                                                                JSONObject data = res.getJSONObject("data");
                                                                int correct = data.getInt("correct");
                                                                float score = ((float)correct/(float)questions.length()) * 100;
                                                                Log.e(TAG, data.toString());
                                                                Intent challengeFinishIntent =
                                                                        new Intent(ChallengeActivity.this,
                                                                        ChallengeFinishActivity.class);
                                                                challengeFinishIntent.putExtra("score", score);
                                                                startActivity(challengeFinishIntent);
                                                            }
                                                        } catch (JSONException e) {
                                                            e.printStackTrace();
                                                        }
                                                    }
                                                },
                                                new Response.ErrorListener() {
                                                    @Override
                                                    public void onErrorResponse(VolleyError error) {
                                                        String msg = error.getMessage();
                                                        if (msg != null) {
                                                            Log.e(TAG, msg);
                                                        }
                                                        Toast.makeText(mContext, "Unknown error", Toast.LENGTH_SHORT).show();
                                                    }
                                                }) {

                                            @Override
                                            protected Map<String, String> getParams() {
                                                Map<String, String> params = new HashMap<>();
                                                params.put("id_pertanyaan", new JSONArray(questionId).toString());
                                                params.put("jawaban", new JSONArray(selectedRadioButton).toString());
                                                return params;
                                            }

                                            @Override
                                            public Map<String, String> getHeaders() throws AuthFailureError {
                                                Map<String, String> params = new HashMap<>();
                                                params.put("id_pertanyaan", new JSONArray(questionId).toString());
                                                params.put("jawaban", new JSONArray(selectedRadioButton).toString());
                                                return params;
                                            }
                                        };

                                        AppSingleton.getInstance(getApplicationContext())
                                                .addToRequestQueue(submitAnswers, "SUBMIT_ANSWERS");
                                    }
                                }
                            });
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
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

    private ArrayList<String> getSelectedRadioButton() {
        int count = mViewPager.getChildCount();
        ArrayList<String> selectedRadioButton = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            View cardView = mViewPager.getChildAt(i);
            View linearLayoutView = ((ViewGroup)cardView).getChildAt(0);
            View questionView = ((ViewGroup)linearLayoutView).getChildAt(1);
            for (int j = 0; j < ((ViewGroup)questionView).getChildCount(); j++) {
                View o = ((ViewGroup)questionView).getChildAt(j);
                if (o instanceof RadioButton) {
                    if (((RadioButton)o).isChecked()) {
                        selectedRadioButton.add(((RadioButton) o).getText().toString());
                    }
                }
            }
        }

        return selectedRadioButton;
    }

    private ArrayList<Object> getSelectedQuestion() {
        int count = mViewPager.getChildCount();
        ArrayList<Object> questionId = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            View cardView = mViewPager.getChildAt(i);
            View linearLayoutView = ((ViewGroup)cardView).getChildAt(0);
            View questionView = ((ViewGroup)linearLayoutView).getChildAt(1);
            for (int j = 0; j < ((ViewGroup)questionView).getChildCount(); j++) {
                View o = ((ViewGroup)questionView).getChildAt(j);
                if (o instanceof RadioButton) {
                    if (((RadioButton)o).isChecked()) {
                        questionId.add(((RadioButton) o).getTag());
                    }
                }
            }
        }

        return questionId;
    }

    private void attachCheckEvent(final int numQuestion) {
        int count = mViewPager.getChildCount();
        for (int i = 0; i < count; i++) {
            View cardView = mViewPager.getChildAt(i);
            View linearLayoutView = ((ViewGroup)cardView).getChildAt(0);
            RadioGroup questionView = (RadioGroup)((ViewGroup)linearLayoutView).getChildAt(1);
            questionView.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup radioGroup, int i) {
                    if (getSelectedRadioButton().size() == numQuestion) {
                        submitButton.setVisibility(View.VISIBLE);
                    }
                }
            });
        }
    }


}
