package com.example.smartpark;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import java.io.IOException;
import android.content.SharedPreferences;

public class QuizActivity1 extends AppCompatActivity {
    private final String CLIENT_ID = "1wNi4KyoRB7OanOthQke";
    private final String CLIENT_SECRET = "AuHfWgrj1c";
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    interface OnTranslationCompleteListener {
        void onCompleted(String translatedText);
    }

    //GBIF API로 받은 (영어)데이터를 한국어로 번역하기 위한 PAPAGO API 호출 코드
    public void translateText(OnTranslationCompleteListener listener) {
        String apiUrl = "https://openapi.naver.com/v1/papago/n2mt";
        String text = sharedPreferences.getString("fromGBIF", "");

        OkHttpClient client = new OkHttpClient();

        RequestBody requestBody = new FormBody.Builder()
                .add("source", "en")
                .add("target", "ko")
                .add("text", text)
                .build();

        okhttp3.Request request = new okhttp3.Request.Builder()
                .url(apiUrl)
                .post(requestBody)
                .addHeader("X-Naver-Client-Id", CLIENT_ID)
                .addHeader("X-Naver-Client-Secret", CLIENT_SECRET)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // 오류 처리
            }
            @Override
            public void onResponse(Call call, okhttp3.Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseData = response.body().string();
                    try {
                        JSONObject jsonObject = new JSONObject(responseData);
                        final String translatedText = jsonObject.getJSONObject("message").getJSONObject("result").getString("translatedText");
                        editor.putString("fromPAPAGO", translatedText);
                        editor.apply();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                listener.onCompleted(translatedText);
                            }
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
    //GBIF API호출 함수
    void APIGet(String url, String Parameter, final OnResponseListener listener){
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String result = response.getString(Parameter);
                            listener.onResponseReceived(result);
                        } catch (Exception e) {
                            // 에러 처리
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // 에러 처리
            }
        });
        Volley.newRequestQueue(this).add(request);
    }
    interface OnResponseListener {
        void onResponseReceived(String result);
    }

    private TextView quiz;
    private ImageView gift; //팝업을 위한 보상이미지 변수
    private TextView ox; //팝업을 위한 정/오답 변수
    private  TextView exp; //팝업을 위한 해설 변수
    private Button choiceButton1;
    private Button choiceButton2;
    private Button choiceButton3;
    private Button choiceButton4;
    private ImageView reward;

    private String explan = " "; //해설
    private String selectedbtn; //클릭 된 버튼의 텍스트
    private String ans; //퀴즈 정답
    private String selectedAnimal; //선택된 동물(영어)
    private String translateAnimal;
    private List<String> choices = new ArrayList<>();

    private String rewardName;
    private int rewardExp = 0;

    private ImageView imageView;
    private Random random = new Random();

    String[] specialItemData = {
            "normalItem", "spItem1", "spItem2", "spItem3", "spItem4", "spItem5", "spItem6", "spItem7", "spItem8", "spItem9"
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz1);

        quiz = findViewById(R.id.quiz);
        choiceButton1 = findViewById(R.id.choiceButton1);
        choiceButton2 = findViewById(R.id.choiceButton2);
        choiceButton3 = findViewById(R.id.choiceButton3);
        choiceButton4 = findViewById(R.id.choiceButton4);

        sharedPreferences = getSharedPreferences("MyAppPreferences", MODE_PRIVATE);
        editor = sharedPreferences.edit();


        //퀴즈에 해당 동물들(GBIF API에서 등록되어 있는것 처럼 과학적 이름) 배열 선언
        String[] Animals = {"Mammuthus", "Giraffa camelopardalis camelopardalis","Macropus rufus", "Canis lupus arctos", "Vulpes cana", "Ambystoma mexicanum", "Aptenodytes forsteri" };

        //동물들의 과학적이름을 그대로 PAPAGO API로 번역이 안되기 때문에 Animals 배열에 대응 되는 AnimalsOnKr 배열에 직접 동물들의 한국이름을 선언
        String[] AnimalsOnKr = {"매머드", "누비아 기린", "빨간 캥거루", "북극늑대", "회색 여우", "악솔로틀", "황제 펭귄"};

        //Animals배열에서 랜덤으로 동물 선택
        int selectedIndex2 = random.nextInt(Animals.length);
        selectedAnimal = Animals[selectedIndex2];
        translateAnimal = AnimalsOnKr[selectedIndex2];

        //랜덤으로 선택된 동물의 urlAPI
        String url = "https://api.gbif.org/v1/species/match?name=" + Uri.encode(selectedAnimal);

        //GBIF API에서 제공해주는 (영어)데이터와 비교 하기위해 선택지로 나올 수 있는 단어들을 영어로 선언
        choices.add("Elephantidae"); //코끼리과
        choices.add("Giraffidae"); //기라피과
        choices.add("Macropodidae"); //황각목과
        choices.add("Canidae"); //개과
        choices.add("Muridae"); //쥐과
        choices.add("Ambystomatidae"); //암비스토마티과
        choices.add("Spheniscidae"); //스페니스과

        //GBIF API 호출 메소드
        APIGet(url, "family", new OnResponseListener() {
            @Override
            public void onResponseReceived(String res) {
                String quiz1 = translateAnimal + "은(는) 어떤 분류군에 속하는걸까요?";
                updateQuiz(quiz1, res);

                //GBIF API로 가져온 데이터를 번역하기 위해 PAPAGO API 코드호출
                editor.putString("fromGBIF", res);
                editor.apply();
                translateText(new OnTranslationCompleteListener() {
                    @Override
                    public void onCompleted(String translatedText) {
                        explan = translateAnimal + "은(는)" + translatedText + "에 해당돼!!!";
                        ans = translatedText;
                    }
                });
            }
        });
    }

    void updateQuiz(String question, String answer) {
        quiz.setText(question);
        setImageView(translateAnimal);

        //정답과 오답3개를 랜덥으로 선택지 4개에 배치
        for (int i = 0; i < choices.size(); i++) {
            if (choices.get(i).equals(answer)) {
                choices.remove(i);
                break;
            }
        }
        int randomNumber = random.nextInt(4) + 1;
        switch (randomNumber) {
            case 1:
                setButtonTranslation(choiceButton1, answer);
                break;
            case 2:
                setButtonTranslation(choiceButton2, answer);
                break;
            case 3:
                setButtonTranslation(choiceButton3, answer);
                break;
            case 4:
                setButtonTranslation(choiceButton4, answer);
                break;
        }

        List<Button> remainingButtons = new ArrayList<>();
        remainingButtons.add(choiceButton1);
        remainingButtons.add(choiceButton2);
        remainingButtons.add(choiceButton3);
        remainingButtons.add(choiceButton4);
        remainingButtons.remove(randomNumber - 1);
        for (Button btn : remainingButtons) {
            int randomIndex = random.nextInt(choices.size());
            setButtonTranslation(btn, choices.get(randomIndex));
            choices.remove(randomIndex);
        }
    }

    private void setImageView(String animal){
        imageView = findViewById(R.id.imageView);
        if (animal == "누비아 기린"){
            imageView.setImageResource(R.drawable.gir);
        } else if (animal == "빨간 캥거루"){
            imageView.setImageResource(R.drawable.kangaroo);
        }else if (animal == "북극늑대"){
            imageView.setImageResource(R.drawable.wolf);
        }else if (animal == "회색 여우"){
            imageView.setImageResource(R.drawable.finja);
        }else if (animal == "악솔로틀"){
            imageView.setImageResource(R.drawable.acsol);
        }else if (animal == "황제 펭귄"){
            imageView.setImageResource(R.drawable.penguin);
        }else if (animal == "매머드"){
            imageView.setImageResource(R.drawable.mammoth);
        }
    }

    //선택된 선택지버튼에 텍스트를 번역하고 set
    void setButtonTranslation(Button button, String text) {
        editor.putString("fromGBIF", text);
        editor.apply();
        translateText(new OnTranslationCompleteListener() {
            @Override
            public void onCompleted(String translatedText) {
                button.setText(translatedText);
            }
        });
    }

    //클릭한 버튼을 저장 함수
    public void clickbtn(View view){
        Button clickedButton = (Button) view;
        toggleButton(clickedButton);
        String buttonText = clickedButton.getText().toString();
        selectedbtn = buttonText;
    }


    //선택지 클릭시 확대 함수
    private void toggleButton(Button selectedButton) {
        Button[] buttons = {choiceButton1, choiceButton2, choiceButton3, choiceButton4};

        for (Button button : buttons) {
            float scale = (button == selectedButton) ? 1.3f : 1.0f;
            button.setScaleX(scale);
            button.setScaleY(scale);
        }
    }

    //제출버튼 함수
    public void submitBtnClick(View view){
        final Dialog popup_dialog = new Dialog(this);
        popup_dialog.setContentView(R.layout.popup_quiz);
        reward = popup_dialog.findViewById(R.id.gift);

        sharedPreferences = getSharedPreferences("MyAppPreferences", MODE_PRIVATE);
        editor = sharedPreferences.edit();

        ox = popup_dialog.findViewById(R.id.ox);
        exp = popup_dialog.findViewById(R.id.explan);

        popup_dialog.setCanceledOnTouchOutside(false);
        popup_dialog.show();

        exp.setText(explan);

        int currentExp = sharedPreferences.getInt("experience", 0);
        if(selectedbtn.equals(ans)){
            ox.setText("맞지맞지~");
            rewardRoulette();
            editor.putInt(rewardName, 1);
            editor.putInt("experience", currentExp+rewardExp);
        } else {
            ox.setText("틀렸다잉");
            reward.setImageResource(R.drawable.basicitem);
            editor.putInt("experience", currentExp+10);
        }
        editor.apply();
        view.setEnabled(false);

        //닫기버튼을 위한 코드
        Button button1 = popup_dialog.findViewById(R.id.ext_btn);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popup_dialog.dismiss();
            }
        });

        //해당 퀴즈 사이트로 바로 이동 코드
        Button openBrowserButton = popup_dialog.findViewById(R.id.openBrowserButton);
        openBrowserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 인터넷 브라우저 열기
                String url = "https://www.gbif.org/species/search?q=" + selectedAnimal; // 여기에 열고자 하는 웹페이지 URL을 입력하세요.
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                // 인터넷 브라우저를 열기 위한 액션을 지정하고 URL을 설정한 Intent를 실행합니다.
                startActivity(intent);
            }
        });

    }

    //보상 획득을 위한 확률 함수
    void rewardRoulette() {

        int randomNumber = random.nextInt(100) + 1;
        //randomNumber = 83;

        if (randomNumber <= 64) {
            rewardName = specialItemData[0];
            rewardExp = 20;
            reward.setImageResource(R.drawable.basicitem);
        } else if (randomNumber <= 68) {
            rewardName = specialItemData[1];
            rewardExp = 40;
            reward.setImageResource(R.drawable.spitem1);
        } else if (randomNumber <= 72) {
            rewardName = specialItemData[2];
            rewardExp = 40;
            reward.setImageResource(R.drawable.spitem2);
        } else if (randomNumber <= 76) {
            rewardName = specialItemData[3];
            rewardExp = 40;
            reward.setImageResource(R.drawable.spitem3);
        } else if (randomNumber <= 80) {
            rewardName = specialItemData[4];
            rewardExp = 40;
            reward.setImageResource(R.drawable.spitem4);
        } else if (randomNumber <= 84) {
            rewardName = specialItemData[5];
            rewardExp = 40;
            reward.setImageResource(R.drawable.spitem5);
        } else if (randomNumber <= 88) {
            rewardName = specialItemData[6];
            rewardExp = 40;
            reward.setImageResource(R.drawable.spitem6);
        } else if (randomNumber <= 92) {
            rewardName = specialItemData[7];
            rewardExp = 40;
            reward.setImageResource(R.drawable.spitem7);
        } else if (randomNumber <= 96) {
            rewardName = specialItemData[8];
            rewardExp = 40;
            reward.setImageResource(R.drawable.spitem8);
        } else {
            rewardName = specialItemData[9];
            rewardExp = 40;
            reward.setImageResource(R.drawable.spitem9);
        }
    }

}