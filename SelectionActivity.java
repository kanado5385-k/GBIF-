package com.example.smartpark;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;

public class SelectionActivity extends AppCompatActivity {
    int flag = 0;
    ImageButton[] buttons = new ImageButton[4];
    Animation scaleUp;
    Animation scaleDown;
    boolean[] isButtonExpanded = new boolean[4];

    // 현재 확대된 버튼의 인덱스를 저장할 변수
    int expandedButtonIndex = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selection);
        buttons[0] = findViewById(R.id.petBtn1);
        buttons[1] = findViewById(R.id.petBtn2);
        buttons[2] = findViewById(R.id.petBtn3);
        buttons[3] = findViewById(R.id.petBtn4);

        scaleUp = AnimationUtils.loadAnimation(this, R.anim.scale_up);
        scaleDown = AnimationUtils.loadAnimation(this, R.anim.scale_down);

        // 모든 버튼 초기 상태 설정
        for (int i = 0; i < 4; i++) {
            final int buttonIndex = i;
            buttons[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    toggleButton(buttonIndex);
                }
            });
        }


        Button nextButton = findViewById(R.id.nextBtn);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPreferences = getSharedPreferences("MyAppPreferences", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                // 캐릭터를 선택하지않고 다음 버튼을 눌렀을 경우
                if (flag == -1){
                    Toast.makeText(SelectionActivity.this, "캐릭터를 다시 선택하고 눌러주세요.", Toast.LENGTH_SHORT).show();
                    // 선택한 캐릭터의 정보를 저장한 다음 NamingActivity로 넘어갑니다.
                } else {
                    if (flag == 0){
                        // 펭귄
                        editor.putInt("characterNum", 0);
                        editor.apply();
                    } else if (flag == 1){
                        // 코
                        editor.putInt("characterNum", 1);
                        editor.apply();

                    } else if (flag == 2){
                        editor.putInt("characterNum", 2);
                        editor.apply();
                    } else {
                        editor.putInt("characterNum", 3);
                        editor.apply();
                    }
                    Intent intent = new Intent(SelectionActivity.this, NamingActivity.class);
                    startActivity(intent);
                }
            }
        });
    }


    private void toggleButton(int buttonIndex) {
        for (int i = 0; i < 4; i++) {
            if (i == buttonIndex) {
                // 클릭한 버튼을 확대
                buttons[i].setScaleX(1.2f);
                buttons[i].setScaleY(1.2f);
                flag = i;
            } else {
                // 다른 버튼들을 축소
                buttons[i].setScaleX(1.0f);
                buttons[i].setScaleY(1.0f);
            }
        }

    }
}