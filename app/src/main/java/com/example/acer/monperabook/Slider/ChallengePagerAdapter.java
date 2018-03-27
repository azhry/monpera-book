package com.example.acer.monperabook.Slider;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.acer.monperabook.CustomAdapter.Answer;
import com.example.acer.monperabook.CustomAdapter.Challenge;
import com.example.acer.monperabook.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Azhary Arliansyah on 26/02/2018.
 */

public class ChallengePagerAdapter extends PagerAdapter {

    private static String TAG = "PROJECT.ChallengePagerAdapter";
    private List<Challenge> listChallenges;
    private List<CardView> mCardView;
    private Context context;

    public ChallengePagerAdapter(Context context) {
        listChallenges = new ArrayList<>();
        mCardView = new ArrayList<>();
        this.context = context;
    }

    public void addChallenge(Challenge challenge) {
        listChallenges.add(challenge);
        mCardView.add(null);
    }

    @Override
    public int getCount() {
        return listChallenges.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        final View v = LayoutInflater.from(container.getContext())
                .inflate(R.layout.card_challenge, container, false);
        bind(listChallenges.get(position), v, position);
        container.addView(v);
        CardView cardView = (CardView) v.findViewById(R.id.cardView);
        mCardView.set(position, cardView);
        return v;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View)object);
        mCardView.set(position, null);
    }

    private void bind(final Challenge item, View view, final int position) {
        TextView questionTextView = (TextView) view.findViewById(R.id.question);
        questionTextView.setText(item.getQuestion());
        List<Answer> answers = item.getAnswers();
        RadioButton radioButton1 = (RadioButton) view.findViewById(R.id.answer_1);
        RadioButton radioButton2 = (RadioButton) view.findViewById(R.id.answer_2);
        RadioButton radioButton3 = (RadioButton) view.findViewById(R.id.answer_3);
        RadioButton radioButton4 = (RadioButton) view.findViewById(R.id.answer_4);
        radioButton1.setText(answers.get(0).getAnswer());
        radioButton2.setText(answers.get(1).getAnswer());
        radioButton3.setText(answers.get(2).getAnswer());
        radioButton4.setText(answers.get(3).getAnswer());

        radioButton1.setTag(item.getId());
        radioButton2.setTag(item.getId());
        radioButton3.setTag(item.getId());
        radioButton4.setTag(item.getId());

    }

}
