package ir.adonet.example;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import ir.adonet.analytics.ADAnalytics;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void txtClicked(View view) {
    }

    @Override
    protected void onStart() {
        super.onStart();
        ADAnalytics.countSession(this);
    }
}
