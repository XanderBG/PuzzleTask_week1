package com.example.xander.puzzletask;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.app.Activity;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.DragEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


public class PuzzleActivity extends Activity {
    private GridLayout gridLayout;
    private boolean solved = false;
    private ArrayList<Integer> randomId = new ArrayList<Integer>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_puzzle);

        TypedArray ta = this.getResources().obtainTypedArray(R.array.images);
        Map<Integer, Drawable> images = new HashMap<Integer, Drawable>();
        for (int i = 0; i < ta.length(); i++) {
            images.put(i, ta.getDrawable(i));
            randomId.add(i);
        }

        ta.recycle();

        Collections.shuffle(randomId);

        gridLayout = (GridLayout) findViewById(R.id.gridLayout);

        gridLayout.setOnDragListener(new View.OnDragListener() {
            @Override
            public boolean onDrag(View v, DragEvent event) {
                switch (event.getAction()) {
                    case DragEvent.ACTION_DRAG_STARTED:
                        return true;
                    case DragEvent.ACTION_DRAG_ENTERED:
                }
                return false;
            }
        });

        MyLongClickListener longClickListener = new MyLongClickListener();
        MyDragEventListener dragEventListener = new MyDragEventListener();
        for (int i = 0; i < images.size(); i++) {
            ImageView imageView = new ImageView(this);
            imageView.setTag(randomId.get(i));
            imageView.setImageDrawable(images.get(randomId.get(i)));
            imageView.setAdjustViewBounds(true);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            LinearLayout.LayoutParams imageParams = new LinearLayout.LayoutParams(200, 200);
            imageView.setLayoutParams(imageParams);
            imageView.setPadding(2, 2, 2, 2);
            imageView.setOnLongClickListener(longClickListener);
            imageView.setOnDragListener(dragEventListener);
            gridLayout.addView(imageView);
        }

//        checkIfSolved();
    }

    protected class MyLongClickListener implements View.OnLongClickListener {
        @Override
        public boolean onLongClick(View v) {
            View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(v);
            v.startDrag(null, shadowBuilder, v, 0);
            return true;
        }
    }

    protected void checkIfSolved() {
        solved = true;
        if (gridLayout != null) {
            for (int i = 0; i < gridLayout.getChildCount(); i++) {
                if (!gridLayout.getChildAt(i).getTag().equals(i)) {
                    solved = false;
                }
                System.out.println(gridLayout.getChildAt(i).getTag());
            }
        }
    }

    protected class MyDragEventListener implements View.OnDragListener {

        @Override
        public boolean onDrag(View v, DragEvent event) {
            // Defines a variable to store the action type for the incoming event
            final int action = event.getAction();
            if (solved)
                return false;
            // Handles each of the expected events
            switch (action) {

                case DragEvent.ACTION_DRAG_STARTED:
                    return true;
                case DragEvent.ACTION_DRAG_ENTERED:
                    v.setBackgroundColor(Color.RED);
                    v.invalidate();
                    return true;
                case DragEvent.ACTION_DRAG_EXITED:
                    v.setBackgroundColor(Color.WHITE);
                    v.invalidate();
                    return true;
                case DragEvent.ACTION_DROP:

                    v.setBackgroundColor(Color.WHITE);


                    PropertyValuesHolder fromX = PropertyValuesHolder.ofFloat("x", ((ImageView) event.getLocalState()).getX());
                    PropertyValuesHolder fromY = PropertyValuesHolder.ofFloat("y", ((ImageView) event.getLocalState()).getY());
                    Animator animFrom = ObjectAnimator.ofPropertyValuesHolder(v, fromX, fromY);
                    animFrom.setDuration(300);
                    animFrom.start();

                    PropertyValuesHolder toX = PropertyValuesHolder.ofFloat("x", v.getX());
                    PropertyValuesHolder toY = PropertyValuesHolder.ofFloat("y", v.getY());
                    Animator animTo = ObjectAnimator.ofPropertyValuesHolder(event.getLocalState(), toX, toY);
                    animTo.setDuration(300);
                    animTo.start();

                    int tag1 = (Integer)v.getTag();
                    int tag2 = (Integer)((ImageView) event.getLocalState()).getTag();
//                    v.setTag(tag2);
//                    ((ImageView) event.getLocalState()).setTag(tag1);

                    Collections.swap(randomId, randomId.indexOf(tag1), randomId.indexOf(tag2));

                    // Invalidates the view to force a redraw
//                    checkIfSolved();
                    v.invalidate();
                    // Returns true. DragEvent.getResult() will return true.
                    return true;
                case DragEvent.ACTION_DRAG_ENDED:

                    // Turns off any color tinting
                    v.setBackgroundColor(Color.WHITE);

                    // Invalidates the view to force a redraw
                    v.invalidate();

                    // returns true; the value is ignored.
                    return true;

            }

            return false;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.puzzle, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
