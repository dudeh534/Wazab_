package com.ourincheon.wazap;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

public class showWeeklyActivity extends AppCompatActivity {
    Context context;
    NotoTextView Title,Total,Period,Host,Category,Target,Benefit,Prize,Homepage,Dday;
    ImageView img;
    Recycler_contestItem contestItem;
    Button jBefore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_weekly);
        context = this;

        Intent intent = getIntent();
        contestItem = (Recycler_contestItem) intent.getSerializableExtra("Item");

        Title = (NotoTextView)findViewById(R.id.sTitle);
        Total = (NotoTextView)findViewById(R.id.sTotal);
        Period = (NotoTextView)findViewById(R.id.sPeriod);
        Host = (NotoTextView)findViewById(R.id.sHost);
        Dday = (NotoTextView)findViewById(R.id.sDday);
        Category = (NotoTextView)findViewById(R.id.sCategory);
        Target = (NotoTextView)findViewById(R.id.sTarget);
        Benefit = (NotoTextView)findViewById(R.id.sBenefit);
        Prize = (NotoTextView)findViewById(R.id.sPrize);
        Homepage = (NotoTextView)findViewById(R.id.sHomepage);


        jBefore = (Button) findViewById(R.id.jBefore);
        jBefore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        img = (ImageView)findViewById(R.id.sImg);


        Title.setText(contestItem.getTitle());
        Total.setText(contestItem.getTotal());
        Period.setText(contestItem.getDate());
        Dday.setText(contestItem.getDday());
        Host.setText(contestItem.getHost());
        Target.setText(contestItem.getTarget());
        Benefit.setText(contestItem.getBenefit());
        Prize.setText(contestItem.getPrize());
        Homepage.setText(contestItem.getHomepage());
        Category.setText(contestItem.getCategory());

        Glide.with(context).load(contestItem.getImg()).error(R.drawable.testcontest).crossFade().into(img);
    }
}
