package com.mg.asm;

import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.DragEvent;
import android.view.View;
import android.widget.ImageView;
import android.view.View.OnDragListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;
import com.mg.asm.R;

public class MainActivity extends AppCompatActivity {

    private ImageView textView;
    private static final String IMAGEVIEW_TAG = "드래그 이미지";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = (ImageView) findViewById(R.id.textView); //find by id를 못하겠

        textView.setOnLongClickListener(new View.OnTouchListener() {
            @Override
            public boolean onLongClick(View v) {
                // 태그 생성
                ClipData.Item item = new ClipData.Item((CharSequence) textView.getTag());
                String[] mimeTypes = {ClipDescription.MIMETYPE_TEXT_PLAIN};
                ClipData data = new ClipData(textView.getTag().toString(), mimeTypes, item);
                View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(textView);
                textView.startDrag(data, shadowBuilder, textView, 0);
                textView.setVisibility(View.INVISIBLE);

                class DragListener implements OnDragListener {
                    Drawable normalShape = getResources().getDrawable(R.drawable.textview_widget);
                    Drawable targetShape = getResources().getDrawable(R.drawable.textview_widget);

                    public boolean onDrag(View v, DragEvent event) {

                        // 이벤트 시작
                        switch (event.getAction()) {

                            // 이미지를 드래그 시작될때
                            case DragEvent.ACTION_DRAG_STARTED:
                                Log.d("DragClickListener", "ACTION_DRAG_STARTED");
                                break;

                            // 드래그한 이미지를 옮길려는 지역으로 들어왔을때
                            case DragEvent.ACTION_DRAG_ENTERED:
                                Log.d("DragClickListener", "ACTION_DRAG_ENTERED");
                                // 이미지가 들어왔다는 것을 알려주기 위해 배경이미지 변경
                                v.setBackground(targetShape);
                                break;

                            // 드래그한 이미지가 영역을 빠져 나갈때
                            case DragEvent.ACTION_DRAG_EXITED:
                                Log.d("DragClickListener", "ACTION_DRAG_EXITED");
                                v.setBackground(normalShape);
                                break;

                            // 이미지를 드래그해서 드랍시켰을때
                            case DragEvent.ACTION_DROP:
                                Log.d("DragClickListener", "ACTION_DROP");

                                if (v == findViewById(R.id.view)) {
                                    View view = (View) event.getLocalState();
                                    ViewGroup viewgroup = (ViewGroup) view
                                            .getParent();
                                    viewgroup.removeView(view);

                                    LinearLayout containView = (LinearLayout) v;
                                    containView.addView(view);
                                    view.setVisibility(View.VISIBLE);

                                }else if (v == findViewById(R.id.bar)) {
                                    View view = (View) event.getLocalState();
                                    ViewGroup viewgroup = (ViewGroup) view
                                            .getParent();
                                    viewgroup.removeView(view);

                                    LinearLayout containView = (LinearLayout) v;
                                    containView.addView(view);
                                    view.setVisibility(View.VISIBLE);

                                }else {
                                    View view = (View) event.getLocalState();
                                    view.setVisibility(View.VISIBLE);
                                    Context context = getApplicationContext();
                                    Toast.makeText(context, "이미지를 다른 지역에 드랍할수 없습니다.", Toast.LENGTH_LONG).show();
                                    break;
                                }
                                break;

                            case DragEvent.ACTION_DRAG_ENDED:
                                Log.d("DragClickListener", "ACTION_DRAG_ENDED");
                                v.setBackground(normalShape);

                            default:
                                break;
                        }
                return true;
            }
            }
            }
        }
    });
}