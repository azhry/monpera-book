package com.example.acer.monperabook;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.example.acer.monperabook.ImageSlider.FragmentSlider;
import com.example.acer.monperabook.ImageSlider.ShadowTransformer;
import com.example.acer.monperabook.ImageSlider.SliderIndicator;
import com.example.acer.monperabook.ImageSlider.SliderPagerAdapter;
import com.example.acer.monperabook.ImageSlider.SliderView;
import com.example.acer.monperabook.Singleton.AppSingleton;
import com.example.acer.monperabook.Slider.CardFragmentPagerAdapter;
import com.example.acer.monperabook.Slider.CardItem;
import com.example.acer.monperabook.Slider.CardPagerAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Azhary Arliansyah on 09/02/2018.
 */

public class OtherMenuActivity extends AppCompatActivity implements View.OnClickListener,
        CompoundButton.OnCheckedChangeListener {

    public static final String TAG = "OtherMenuActivity";

    private String images;
    private String mEndpoint;
    private SliderPagerAdapter mAdapter;
    private SliderIndicator mIndicator;
    private SliderView sliderView;
    private LinearLayout mLinearLayout;

    private Button mButton;
    private ViewPager mViewPager;

    private CardPagerAdapter mCardAdapter;
    private ShadowTransformer mCardShadowTransformer;
    private CardFragmentPagerAdapter mFragmentCardAdapter;
    private ShadowTransformer mFragmentCardShadowTransformer;

    private boolean mShowingFragments = false;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other_menu);

        mEndpoint       = getString(R.string.server_ip);
        sliderView      = (SliderView)findViewById(R.id.sliderView);
        mLinearLayout   = (LinearLayout)findViewById(R.id.pagesContainer);
        context         = this;

        setupSlider();

//        mViewPager = (ViewPager) findViewById(R.id.viewPager);
//        mCardAdapter = new CardPagerAdapter();
//        getMostFavoriteArtifact();

    }

    private void setupSlider() {
        sliderView.setDurationScroll(800);
        List<Fragment> fragments = new ArrayList<>();
        fragments.add(FragmentSlider.newInstance(mEndpoint + "img/MBK-1000.jpg"));
        fragments.add(FragmentSlider.newInstance(mEndpoint + "img/MBK-1001.jpg"));
        fragments.add(FragmentSlider.newInstance(mEndpoint + "img/Kunci pintu Kaabah.jpeg"));

        mAdapter = new SliderPagerAdapter(getSupportFragmentManager(), fragments);
        sliderView.setAdapter(mAdapter);
        mIndicator = new SliderIndicator(this, mLinearLayout, sliderView, R.drawable.indicator_circle);
        mIndicator.setPageCount(fragments.size());
        mIndicator.show();

        sliderView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;
            }
        });
    }

    private static float dpToPixels(int dp, Context context) {
        return dp * (context.getResources().getDisplayMetrics().density);
    }

    @Override
    public void onClick(View view) {
        if (!mShowingFragments) {
            mButton.setText("Views");
            mViewPager.setAdapter(mFragmentCardAdapter);
            mViewPager.setPageTransformer(false, mFragmentCardShadowTransformer);
        } else {
            mButton.setText("Fragments");
            mViewPager.setAdapter(mCardAdapter);
            mViewPager.setPageTransformer(false, mCardShadowTransformer);
        }

        mShowingFragments = !mShowingFragments;
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        mCardShadowTransformer.enableScaling(b);
        mFragmentCardShadowTransformer.enableScaling(b);
    }

    private void getMostFavoriteArtifact() {

        StringRequest getMostFavorite = new StringRequest(Request.Method.GET, mEndpoint + "artifak/most-favorite?limit=3",

                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            JSONArray data = jsonResponse.getJSONArray("data");
                            for (int i = 0; i < data.length(); i++) {

                                JSONObject artifact = data.getJSONObject(i);
                                JSONArray thumbnailUrl = new JSONArray(artifact.getString("foto"));
                                String imgUrl = mEndpoint + "img/" + thumbnailUrl.getString(0);
                                mCardAdapter.addCardItem(new CardItem(artifact.getString("nama"),
                                        artifact.getString("deskripsi"),
                                        imgUrl));

                            }
                            mFragmentCardAdapter = new CardFragmentPagerAdapter(getSupportFragmentManager(),
                                    dpToPixels(2, context));

                            mCardShadowTransformer = new ShadowTransformer(mViewPager, mCardAdapter);
                            mFragmentCardShadowTransformer = new ShadowTransformer(mViewPager, mFragmentCardAdapter);

                            mViewPager.setAdapter(mCardAdapter);
                            mViewPager.setPageTransformer(false, mCardShadowTransformer);
                            mViewPager.setOffscreenPageLimit(3);

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
                });

        AppSingleton.getInstance(getApplicationContext()).addToRequestQueue(getMostFavorite, TAG);
    }
}
