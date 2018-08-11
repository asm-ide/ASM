package com.mg.asm;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {

    private ImageView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = (ImageView) findViewById(R.id.textView); //find by id를 못하겠

        textView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()){
                    case MotionEvent.ACTION_UP:

                        Log.i(this.getClass().getName(), "Action_UP");
                        //여따가 그 쉐도우? 그거 둬야지이 그리고 그 터치 좌표 들고오는 코드 가지고 이동

                        return true;
                    case MotionEvent.ACTION_DOWN:

                        Log.i(this.getClass().getName(), "Action_Down");
                        //이제 여따가 놓았을 때 코드 두고

                        return true;

                }
                return false;
            }
        });

    }
}
