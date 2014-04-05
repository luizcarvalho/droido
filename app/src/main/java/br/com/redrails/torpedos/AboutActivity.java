package br.com.redrails.torpedos;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;


public class AboutActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        ImageView logo = (ImageView) findViewById(R.id.bigLogo);
        TextView autor = (TextView) findViewById(R.id.app_author);
        TextView site = (TextView) findViewById(R.id.red_rails);

        logo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentRating = new Intent(Intent.ACTION_VIEW);
                intentRating.setData(Uri.parse("http://www.redrails.com.br"));
                startActivity(intentRating);        }
        });


        autor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentRating = new Intent(Intent.ACTION_VIEW);
                intentRating.setData(Uri.parse("http://www.twitter.com/LuizCarvalho"));
                startActivity(intentRating);        }
        });

        site.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentRating = new Intent(Intent.ACTION_VIEW);
                intentRating.setData(Uri.parse("http://www.redrails.com.br"));
                startActivity(intentRating);        }
        });



    }




}
