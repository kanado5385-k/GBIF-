package com.example.smartpark;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class NamingActivity extends AppCompatActivity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_naming);

        Intent intent = getIntent();
        // (이전 화면 변수이름, 전달 실패 시 기본값) 기본값에 대한 대처가 필요할지도 ex) 이전화면으로 돌아간다던가.

        // 저장된 캐릭터의 정보를 불러와서 적절한 이미지를 삽입한다.
        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPreferences", MODE_PRIVATE);
        int chNum = sharedPreferences.getInt("characterNum", 0);
        ImageView character_pic = findViewById(R.id.imageView);
        Log.d("Naming", String.valueOf(chNum));
        if (chNum == 0) {
            character_pic.setImageResource(R.drawable.egg1);
        } else if (chNum == 1) {
            character_pic.setImageResource(R.drawable.egg2);
        } else if (chNum == 2) {
            character_pic.setImageResource(R.drawable.egg3);
        } else {
            character_pic.setImageResource(R.drawable.egg4);
        }

        Button Button5 = findViewById(R.id.button5);
        EditText editText = findViewById(R.id.editTextText); // editText는 해당 EditText의 ID에 맞게 변경해야 합니다.

        Button5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = sharedPreferences.edit();

                String enteredName = editText.getText().toString();

                editor.putString("petName", enteredName);
                editor.putInt("experience", 0);
                editor.apply();
                Intent intent = new Intent(NamingActivity.this, ProfileActivity.class);
                startActivity(intent);
            }
        });
    }

    public void onBackButtonClick(View view) {
        onBackPressed(); // 뒤로가기 동작 실행
    }
}