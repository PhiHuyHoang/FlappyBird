package com.rice.flappybird;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Handler;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;

import java.util.Random;


public class GameView extends View {

    Handler handler;
    Runnable runnable;
    final int UPDATE_MILLIS = 30;
    Bitmap background;
    Bitmap toptube, bottomtube;
    Display display;
    Point point;
    Rect rect;

    int tubeWidth = 300, tubeHeight = 2000;
    int birdWidth = 188, birdHeight = 144;

    int dWidth, dHeight;

    Bitmap[] birds;
    Bitmap Nyan;
    Bitmap Goku;

    int birdFrame = 0;
    int velocity = 0, gravity = 3;
    int birdX, birdY;

    boolean gameState = false;

    int gap = 400;
    int minTubeOffSet, maxTubeOffset;
    int numberOfTubes = 4;
    int distanceBetweenTubes;
    int[] tubeX = new int[numberOfTubes];
    int[] tubeY = new int[numberOfTubes];
    Random random;

    Paint paint;

    Paint stkPaint;


    int tubeVelocity = 8;

    int score = 0;
    int scoringTube = 0;

    public static Bitmap reSize(Bitmap original, int width, int height) {
        return Bitmap.createScaledBitmap(
                original, width, height, false);
    }

    public GameView(Context context) {
        super(context);
        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                invalidate();
            }
        };
        background = BitmapFactory.decodeResource(getResources(), R.drawable.background);
        display = ((Activity) getContext()).getWindowManager().getDefaultDisplay();

        point = new Point();
        display.getSize(point);
        dWidth = point.x;
        dHeight = point.y;
        rect = new Rect(0, 0, dWidth, dHeight);
        birds = new Bitmap[2];

        toptube = reSize(BitmapFactory.decodeResource(getResources(), R.drawable.pipedown), tubeWidth, tubeHeight);
        bottomtube = reSize(BitmapFactory.decodeResource(getResources(), R.drawable.pipeup), tubeWidth, tubeHeight);

        birds[0] = reSize(BitmapFactory.decodeResource(getResources(), R.drawable.birdresize), birdWidth, birdHeight);
        birds[1] = reSize(BitmapFactory.decodeResource(getResources(), R.drawable.birdresize2), birdWidth, birdHeight);

        Nyan = reSize(BitmapFactory.decodeResource(getResources(),R.drawable.nyancat),birdWidth+30,birdHeight+30);

        Goku = reSize(BitmapFactory.decodeResource(getResources(),R.drawable.goku),birdWidth+100,birdHeight+100);

        birdX = dWidth / 2 - birds[0].getWidth() / 2;
        birdY = dHeight / 2 - birds[0].getHeight() / 2;

        distanceBetweenTubes = dWidth * 3 / 4;
        minTubeOffSet = gap / 2;
        maxTubeOffset = dHeight - minTubeOffSet - gap;
        random = new Random();

        for (int i = 0; i < numberOfTubes; i++) {
            tubeX[i] = dWidth + i * distanceBetweenTubes;

            tubeY[i] = minTubeOffSet + random.nextInt(maxTubeOffset - minTubeOffSet + 1);
        }

        paint = new Paint();
        paint.setColor(Color.WHITE); // Text Color
        paint.setTextSize(300); // Text Size
        paint.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/flappy.ttf"));
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OVER));
        paint.setTextAlign(Paint.Align.CENTER);

        stkPaint = new Paint();
        stkPaint.setStyle(Paint.Style.STROKE);
        stkPaint.setStrokeWidth(8);
        stkPaint.setColor(Color.BLACK);
        stkPaint.setTextSize(300); // Text Size
        stkPaint.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/flappy.ttf"));
        stkPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OVER));
        stkPaint.setTextAlign(Paint.Align.CENTER);


    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawBitmap(background, null, rect, null);
        if (birdFrame == 0) {
            birdFrame = 1;
        } else {
            birdFrame = 0;
        }

        if (gameState) {
            if (birdY < dHeight - birds[0].getHeight() || velocity < 0) {
                velocity += gravity;
                birdY += velocity;


//                canvas.drawText(String.valueOf(birdY), dWidth/2,dHeight-200,paint);
            }
            if(birdY > dHeight - birds[0].getHeight())
            {
                gameState = false;
            }
            //canvas.drawText(String.valueOf(score), dWidth/2,dHeight/5,paint);
            for (int i = 0; i < numberOfTubes; i++) {

                tubeX[i] -= tubeVelocity;
                if (tubeX[i] < -toptube.getWidth()) {
                    tubeX[i] += numberOfTubes * distanceBetweenTubes;
                    tubeY[i] = minTubeOffSet + random.nextInt(maxTubeOffset - minTubeOffSet + 1);

                }

                canvas.drawBitmap(toptube, tubeX[i], tubeY[i] - toptube.getHeight(), null);
                canvas.drawBitmap(bottomtube, tubeX[i], tubeY[i] + gap, null);

            }

            canvas.drawText(String.valueOf(score), dWidth / 2, dHeight / 5, paint);

            if (birdY < 0) {
                birdY = 0;
            }

            if (tubeX[scoringTube] < dWidth / 2) {
                score++;
                //Gdx.app.log("Score", String.valueOf(score));
                if (scoringTube < numberOfTubes - 1) {
                    scoringTube++;
                } else {
                    scoringTube = 0;
                }
            }
            if(isCollistionDetected(birdX,birdY,birdX+birdWidth,birdY+birdHeight,tubeX[scoringTube],tubeY[scoringTube] - toptube.getHeight(),tubeX[scoringTube]+tubeWidth,tubeY[scoringTube]  - toptube.getHeight()+tubeHeight) ||
                    isCollistionDetected(birdX,birdY,birdX+birdWidth,birdY+birdHeight,tubeX[scoringTube],tubeY[scoringTube] +gap,tubeX[scoringTube]+tubeWidth,tubeY[scoringTube]+gap+tubeHeight)
            )
            {
                gameState = false;
            }


        } else {

            GameOverSceen();
            paint.setTextSize(300);
            stkPaint.setTextSize(300);
            canvas.drawText("flappy", rect.width() / 2, rect.height() / 5, paint);
            canvas.drawText("Rice", rect.width() / 2, rect.height() / 3, paint);
            canvas.drawText("flappy", rect.width() / 2, rect.height() / 5, stkPaint);
            canvas.drawText("Rice", rect.width() / 2, rect.height() / 3, stkPaint);

            stkPaint.setTextSize(100);
            paint.setTextSize(100);
            canvas.drawText("from Rice with love ♡ ", rect.width() / 2, rect.height() - 200, paint);
            paint.setTextSize(200);
        }

//        canvas.drawRect(birdX, birdY, birdX+birdWidth, birdY+birdHeight, paint);
//        canvas.drawRect(tubeX[scoringTube], tubeY[scoringTube] - toptube.getHeight(), tubeX[scoringTube]+tubeWidth, tubeY[scoringTube] - toptube.getHeight()+tubeHeight, paint);
//        canvas.drawRect(tubeX[scoringTube], tubeY[scoringTube] +gap, tubeX[scoringTube]+tubeWidth, tubeY[scoringTube] +gap+tubeHeight, paint);
        if(score < 3) {
            canvas.drawBitmap(birds[birdFrame], birdX, birdY, null);
        }else if(score >= 3 && score < 5)
        {
            canvas.drawBitmap(Nyan,birdX,birdY,null);
        }
        else
        {
            canvas.drawBitmap(Goku,birdX,birdY,null);
        }
        handler.postDelayed(runnable, UPDATE_MILLIS);

    }

    public void GameOverSceen() {
        score = 0;
        scoringTube = 0;
        birdX = dWidth / 2 - birds[0].getWidth() / 2;
        birdY = dHeight / 2 - birds[0].getHeight() / 2;
        for (int i = 0; i < numberOfTubes; i++) {
            tubeX[i] = dWidth + i * distanceBetweenTubes;

            tubeY[i] = minTubeOffSet + random.nextInt(maxTubeOffset - minTubeOffSet + 1);
        }

    }



    public  boolean isCollistionDetected(int bottom1, int top1, int left1, int right1, int bottom2, int top2, int left2, int right2)
    {
        Rect bounds1 = new Rect(bottom1, top1, left1, right1);
        Rect bounds2 = new Rect(bottom2, top2, left2, right2);
        return bounds1.intersect(bounds2);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        if(action == MotionEvent.ACTION_DOWN)
        {
            velocity = -30;
            gameState = true;
        }
        return true;
    }
}
