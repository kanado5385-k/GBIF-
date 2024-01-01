package com.example.smartpark;


import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.graphics.Color;
import java.util.Random;

public class ProfileActivity extends AppCompatActivity {
    private Button quizButton1;
    private TextView nameView;
    private ImageView imageView;
    private ImageView backGround;

    private ProgressBar experienceBar; //경험치 바
    private int currentExp; //현재 경험치
    private int currentLevel; //현재 레벨

    static final String PREFS_NAME = "MyPrefs";
    static final String BUTTON_CLICK_TIME = "buttonClickTime";

    public void onBackPressed() {
        //초기화면(메인 액티비티)으로 이동하는 코드
        Intent intent = new Intent(this, StartActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // 내부저장소를 사용하기 위한 세팅
        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPreferences", MODE_PRIVATE);
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();

        // 데이터에 저장된 펫의 경험치를 불러오는 코드
        currentExp = sharedPreferences.getInt("experience", 0);
        currentLevel = sharedPreferences.getInt("level", 0);
        int limit = sharedPreferences.getInt("maxLimit", 0);
        experienceBar = findViewById(R.id.experienceProgressBar);
        Log.d("exp", String.valueOf(currentExp));
        Log.d("limit", String.valueOf(limit));

        if (currentExp >= limit) {
            editor.putInt("experience", 0);
            editor.putInt("level", currentLevel + 1);
            editor.putInt("maxLimit", limit * 2);
            experienceBar.setMax(limit * 2);
            experienceBar.setProgress(currentExp);
            currentExp = 0;
            currentLevel += 1;
        }
        editor.apply();
        experienceBar.setProgress(currentExp);

        // 데이터에 저장된 펫의 종류를 불러와서 적절한 이미지를 삽입하는 코드
        imageView = findViewById(R.id.MypetImage);
        backGround = findViewById(R.id.backgroundImageView);
        int chNum = sharedPreferences.getInt("characterNum", 0);
        if (chNum == 0) {
            backGround.setImageResource(R.drawable.ice);
            if (currentLevel == 1) {
                imageView.setImageResource(R.drawable.egg1);
            } else if (currentLevel == 2) {
                imageView.setImageResource(R.drawable.penguin);
            } else {
                imageView.setImageResource(R.drawable.penguin2);
            }
        } else if (chNum == 1) {
            backGround.setImageResource(R.drawable.desert);
            if (currentLevel == 1) {
                imageView.setImageResource(R.drawable.egg2);
            } else if (currentLevel == 2) {
                imageView.setImageResource(R.drawable.elephant1);
            } else {
                imageView.setImageResource(R.drawable.elephant2);
            }
        } else if (chNum == 2) {
            backGround.setImageResource(R.drawable.bgi_profile);
            if (currentLevel == 1) {
                imageView.setImageResource(R.drawable.egg3);
            } else if (currentLevel == 2) {
                imageView.setImageResource(R.drawable.cat1);
            } else {
                imageView.setImageResource(R.drawable.cat2);
            }
        } else {
            backGround.setImageResource(R.drawable.moutain);
            if (currentLevel == 1) {
                imageView.setImageResource(R.drawable.egg4);
            } else if (currentLevel == 2) {
                imageView.setImageResource(R.drawable.kangaroo1);
            } else {
                imageView.setImageResource(R.drawable.kangaroo2);
            }
        }


        // 데이터에 저장된 펫의 이름을 불러와서 화면에 표시해주는 코드
        String name;
        nameView = findViewById(R.id.textView);
        name = sharedPreferences.getString("petName", "buddy");
        nameView.setText("이름: " + name);

        quizButton1 = findViewById(R.id.quizButton);
        checkButtonStatus();


        quizButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                disableButtonFor24Hours();
                int[] QuizNum = {1, 2, 3};
                Random random = new Random();
                int selectedIndex = random.nextInt(QuizNum.length);
                if(selectedIndex == 1){
                    // 4지선다 문제
                    Intent intent = new Intent(getApplicationContext(), QuizActivity1.class);
                    startActivity(intent);
                } else if(selectedIndex == 2){
                    // 4지선다 문제
                    Intent intent = new Intent(getApplicationContext(), QuizActivity2.class);
                    startActivity(intent);
                } else {
                    // 2지선다 문제
                    Intent intent = new Intent(getApplicationContext(), QuizActivity3.class);
                    startActivity(intent);
                }

            }
        });
    }

    // 스페셜 먹이도감 팝업창을 띄우는 버튼 코드
    public void popup(View v){
        final Dialog popup_dialog = new Dialog(this);
        popup_dialog.setContentView(R.layout.popup_profile);
        popup_dialog.setCanceledOnTouchOutside(false);
        popup_dialog.show();
        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPreferences", MODE_PRIVATE);
        int spItem1 = sharedPreferences.getInt("spItem1", 0);
        int spItem2 = sharedPreferences.getInt("spItem2", 0);
        int spItem3 = sharedPreferences.getInt("spItem3", 0);
        int spItem4 = sharedPreferences.getInt("spItem4", 0);
        int spItem5 = sharedPreferences.getInt("spItem5", 0);
        int spItem6 = sharedPreferences.getInt("spItem6", 0);
        int spItem7 = sharedPreferences.getInt("spItem7", 0);
        int spItem8 = sharedPreferences.getInt("spItem8", 0);
        int spItem9 = sharedPreferences.getInt("spItem9", 0);

        ImageView img1 = popup_dialog.findViewById(R.id.specialItem1);
        ImageView img2 = popup_dialog.findViewById(R.id.specialItem2);
        ImageView img3 = popup_dialog.findViewById(R.id.specialItem3);
        ImageView img4 = popup_dialog.findViewById(R.id.specialItem4);
        ImageView img5 = popup_dialog.findViewById(R.id.specialItem5);
        ImageView img6 = popup_dialog.findViewById(R.id.specialItem6);
        ImageView img7 = popup_dialog.findViewById(R.id.specialItem7);
        ImageView img8 = popup_dialog.findViewById(R.id.specialItem8);
        ImageView img9 = popup_dialog.findViewById(R.id.specialItem9);

        if(spItem1 == 1){img1.setImageResource(R.drawable.spitem1);}
        if(spItem2 == 1){img2.setImageResource(R.drawable.spitem2);}
        if(spItem3 == 1){img3.setImageResource(R.drawable.spitem3);}
        if(spItem4 == 1){img4.setImageResource(R.drawable.spitem4);}
        if(spItem5 == 1){img5.setImageResource(R.drawable.spitem5);}
        if(spItem6 == 1){img6.setImageResource(R.drawable.spitem6);}
        if(spItem7 == 1){img7.setImageResource(R.drawable.spitem7);}
        if(spItem8 == 1){img8.setImageResource(R.drawable.spitem8);}
        if(spItem9 == 1){img9.setImageResource(R.drawable.spitem9);}



        Button button1 = popup_dialog.findViewById(R.id.closeButton);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { popup_dialog.dismiss();
            }
        });
    }

    //퀴즈를 하루에 한번만 풀수 있게 퀴즈 버튼을 클릭시 24시간동안 잠금 상태 만드는 코드
    private void disableButtonFor24Hours() {
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        long currentTime = System.currentTimeMillis();
        editor.putLong(BUTTON_CLICK_TIME, currentTime);
        editor.apply();
        quizButton1.setBackgroundColor(R.drawable.red_roundbtn);
        quizButton1.setEnabled(false);
    }

    //퀴즈버튼을 클릭한지 24시간이 지났는지 계산하는 코드
    private void checkButtonStatus() {
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        long lastClickTime = settings.getLong(BUTTON_CLICK_TIME, 0);
        // 원래는 1이 아닌 86400000(24시간)으로 설정해야함.
        // 해커톤 제출본은 시연을 편하게 하기 위해 1초 단위로 문제를 새로 풀 수 있음
        if (System.currentTimeMillis() - lastClickTime < 1) {  // 86400000ms is 24 hours
            quizButton1.setEnabled(false);
        } else {
            //다시 원래 색
            quizButton1.setEnabled(true);
        }
    }
}