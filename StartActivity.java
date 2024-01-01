package com.example.smartpark;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class StartActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        Button newButton = findViewById(R.id.newBtn);
        Button continueButton = findViewById(R.id.conBtn);



        // 새로하기 버튼을 누를 경우 작동하는 코드
        newButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 이미 데이터가 존재하는 경우
                if (hasSavedData()) {
                    // 이어하기
                    popup();
                } else {
                    //
                    resetData(); // 모든 데이터를 초기화한다.
                    //  다음 화면으로 화면전환
                    Intent intent = new Intent(StartActivity.this, SelectionActivity.class);
                    startActivity(intent);
                }
            }
        });

        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // "이어하기" 버튼을 눌렀을 때 실행될 코드 작성
                // 이어하기 관련 코드 등을 작성
                if (hasSavedData()) {
                    // 이어하기
                    Intent intent = new Intent(StartActivity.this, ProfileActivity.class);
                    startActivity(intent);
                } else {
                    // 저장된 데이터 없음
                    Toast.makeText(StartActivity.this, "저장된 데이터가 없습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private boolean hasSavedData() {
        // SharedPreferences에서 데이터가 있는지 확인하는 로직
        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPreferences", MODE_PRIVATE);
        // 예를 들어, 경험치나 펫의 이름이 있는지 확인하면 될 것입니다.
        return sharedPreferences.contains("characterNum") || sharedPreferences.contains("petName");
    }


    private void resetData() {
        // 데이터 초기화를 위한 로직
        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPreferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear(); // 모든 데이터 삭제
        editor.putInt("level", 1);
        editor.putInt("maxLimit", 100);
        editor.putInt("experience", 0);
        editor.putInt("normalItem", 0);
        editor.putInt("sptItem1", 0);
        editor.putInt("sptItem2", 0);
        editor.putInt("sptItem3", 0);
        editor.putInt("sptItem4", 0);
        editor.putInt("sptItem5", 0);
        editor.putInt("sptItem6", 0);
        editor.putInt("sptItem7", 0);
        editor.putInt("sptItem8", 0);
        editor.putInt("sptItem9", 0);
        editor.apply();

        // 화면 초기화 또는 초기화된 데이터로 설정하는 등의 작업 수행
        // ...
    }

    public void popup(){
        final Dialog popup_dialog = new Dialog(this);
        popup_dialog.setContentView(R.layout.popup_start);
        popup_dialog.setCanceledOnTouchOutside(false);
        popup_dialog.show();

        Button exitButton = popup_dialog.findViewById(R.id.exitBtn);
        Button newDataButton = popup_dialog.findViewById(R.id.newDataBtn);

        // 뒤로가기를 누르면 다시 초기화면으로 돌아간다.
        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { popup_dialog.dismiss();
            }
        });

        // 이전에 데이터가 있음에도 불구하고 새로하기를 누르면 데이터 초기화 진행
        // NextActivity로 이동.
        newDataButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popup_dialog.dismiss();
                resetData();
                Intent intent = new Intent(StartActivity.this, SelectionActivity.class);
                startActivity(intent);
            }
        });
    }
}
