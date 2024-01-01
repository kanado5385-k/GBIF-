package com.example.smartpark;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import java.util.Calendar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Random;

public class QuizActivity3 extends AppCompatActivity {

    interface OnResponseListener {
        void onResponseReceived(String result);
    }

    private TextView quiz;
    private Button choiceButton1;
    private Button choiceButton2;
    private Button submitButton;
    private TextView ox; //팝업을 위한 정/오답 변수
    private TextView exp; //팝업을 위한 해설 변수
    private ImageView gift; //팝업을 위한 상품 변수
    private int currentExp; //현재 경험치
    private String selectedAnimal; //선택된 동물(영어)
    private ImageView imageView;
    private ImageView reward;
    private int submit = 1;
    private int ans = 1;
    private int currentYear = Calendar.getInstance().get(Calendar.YEAR);
    private int tenYearsAgo = currentYear - 10;
    private String selectedAnimalOnKr; //선택된 동물(한국어)
    private String rewardName;
    private int rewardExp = 0;
    private Random random = new Random();

    String[] specialItemData = {
            "normalItem", "spItem1", "spItem2", "spItem3", "spItem4", "spItem5", "spItem6", "spItem7", "spItem8", "spItem9"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz2);

        quiz = findViewById(R.id.quiz);
        choiceButton1 = findViewById(R.id.choiceButton1);
        choiceButton2 = findViewById(R.id.choiceButton2);
        submitButton = findViewById(R.id.submit);



        //감소 or 증가 두 질문 중 하나 랜덤으로 선택
        int[] QuizNum = {1, 2};
        int selectedIndex = random.nextInt(QuizNum.length);
        int selectedQuiz = QuizNum[selectedIndex];

        //어떤 선택지 버튼이 클릭됐는지 저장 코드
        choiceButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submit = 1;
                toggleButton(choiceButton1);
            }
        });
        choiceButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submit = 2;
                toggleButton(choiceButton2);
            }
        });

        //퀴즈에 해당 동물들(GBIF API에서 등록되어 있는것 처럼 과학적 이름) 배열 선언
        String[] Animals = {"Giraffa camelopardalis camelopardalis","Macropus rufus", "Canis lupus arctos", "Vulpes cana", "Ambystoma mexicanum",
                 "Aptenodytes forsteri", "Diceros bicornis", "Passer domesticus", "Cygnus buccinator", "Pica pica", "Haliaeetus leucocephalus",
                "Bubo bubo"};

        //동물들의 과학적이름을 그대로 PAPAGO API로 번역이 안되기 때문에 Animals 배열에 대응 되는 AnimalsOnKr 배열에 직접 동물들의 한국이름을 선언
        String[] AnimalsOnKr = {"누비아 기린", "빨간 캥거루", "북극늑대", "회색 여우", "악솔로틀", "황제 펭귄", "흑코뿔소", "참새", "트럼페터 백조", "까치", "흰머리독수리",
                "여우올빼미"};

        switch (selectedQuiz) { //퀴즈 랜덤으로 선택된다
            case 1:
                //랜덤으로 동물 선택
                int selectedIndex1 = random.nextInt(Animals.length);
                selectedAnimal = Animals[selectedIndex1];
                selectedAnimalOnKr = AnimalsOnKr[selectedIndex1];
                // GBIF API URL(10년 전 년도를 계산)
                String url1 = "https://api.gbif.org/v1/occurrence/search?scientificName=" + Uri.encode(selectedAnimal) +
                        "&eventDate=" + tenYearsAgo + "-01-01," + currentYear + "-12-31" +
                        "&limit=0&facet=year";

                //GBIF API호출
                APIGet(url1, new OnResponseListener() {
                    @Override
                    public void onResponseReceived(String res) {
                        String quiz1 = selectedAnimalOnKr + "은(는) 10년간 증가하는 추세일까요?";
                        int totalCount = Integer.parseInt(res);

                        //퀴즈 결과를 업데이트
                        updateQuiz(quiz1, totalCount);
                    }
                });
                break;

            case 2:
                //랜덤으로 동물 선택
                int selectedIndex2 = random.nextInt(Animals.length);
                selectedAnimal = Animals[selectedIndex2];
                selectedAnimalOnKr = AnimalsOnKr[selectedIndex2];
                // 증감 데이터를 불러올 동물의 종 불러오기
                int currentYear2 = Calendar.getInstance().get(Calendar.YEAR);



                // GBIF API URL(10년 전 년도를 계산)
                String url2 = "https://api.gbif.org/v1/occurrence/search?scientificName=" + Uri.encode(selectedAnimal) +
                        "&eventDate=" + tenYearsAgo + "-01-01," + currentYear + "-12-31" +
                        "&limit=0&facet=year";

                //GBIF API호출
                APIGet(url2, new OnResponseListener() {
                    @Override
                    public void onResponseReceived(String res) {
                        String quiz1 = selectedAnimalOnKr + "은(는) 10년간 감소하는 추세일까?";
                        int totalCount = Integer.parseInt(res);
                        // 퀴즈 결과를 업데이트
                        updateQuiz(quiz1, totalCount);
                    }
                });
                break;
        }

        //제출버튼이 클릭시 함수
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitButton.setEnabled(false);
                SharedPreferences sharedPreferences = getSharedPreferences("MyAppPreferences", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                final Dialog popup_dialog = new Dialog(QuizActivity3.this);

                popup_dialog.setContentView(R.layout.popup_quiz);
                reward = popup_dialog.findViewById(R.id.gift);

                ox = popup_dialog.findViewById(R.id.ox);
                exp = popup_dialog.findViewById(R.id.explan);
                gift = popup_dialog.findViewById(R.id.gift);

                popup_dialog.setCanceledOnTouchOutside(false);
                popup_dialog.show();

                currentExp = sharedPreferences.getInt("experience", 0);


                if (ans == submit) {
                    ox.setText("이거지^0^");
                    rewardRoulette();
                    editor.putInt(rewardName, 1);
                    editor.putInt("experience", rewardExp+currentExp);
                    if (selectedQuiz == 1) {
                        exp.setText("10년 동안 " + selectedAnimalOnKr + "의 숫자가 계속 늘었어!");
                    } else {
                        exp.setText("10년 동안 " + selectedAnimalOnKr + "의 숫자가 계속 감소했어!");
                    }
                } else {
                    ox.setText("틀렸어ㅠㅠ");
                    reward.setImageResource(R.drawable.basicitem);
                    editor.putInt("experience", 10+currentExp);
                    if (selectedQuiz == 1) {
                        exp.setText("10년 동안 " + selectedAnimalOnKr + "의 숫자가 계속 감소했어!");
                    } else {
                        exp.setText("10년 동안 " + selectedAnimalOnKr + "의 숫자가 계속 늘었어!");
                    }
                }
                editor.apply();
                Button openBrowserButton = popup_dialog.findViewById(R.id.openBrowserButton);
                openBrowserButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // 인터넷 브라우저 열기
                        String url = "https://www.gbif.org/species/search?q=" + selectedAnimal;
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        // 인터넷 브라우저를 열기 위한 액션을 지정하고 URL을 설정한 Intent를 실행
                        startActivity(intent);
                    }
                });

                Button button1 = popup_dialog.findViewById(R.id.ext_btn);
                button1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        popup_dialog.dismiss();
                    }
                });
            }
        });
    }

    //GBIF API호출 알고리즘
    void APIGet(String url, final OnResponseListener listener) {
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray facets = response.getJSONArray("facets");
                            if (facets.length() > 0) {
                                JSONObject facet = facets.getJSONObject(0);
                                JSONArray counts = facet.getJSONArray("counts");

                                int totalCount = 0;
                                int count2023 = 0; // 2023년 개체수를 저장하는 변수
                                int recent_year = Calendar.getInstance().get(Calendar.YEAR);

                                for (int i = 0; i < counts.length(); i++) {
                                    JSONObject countObject = counts.getJSONObject(i);
                                    int count = countObject.getInt("count");
                                    totalCount += count;

                                    String name = countObject.getString("name");
                                    if (name.equals("2023")) {
                                        count2023 = count;
                                    }
                                }
                                if (totalCount > count2023) {
                                    totalCount = -1;
                                } else {
                                    totalCount = 1;
                                }
                                listener.onResponseReceived(String.valueOf(totalCount));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
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
    private void toggleButton(Button selectedButton) {
        Button[] buttons = {choiceButton1, choiceButton2};

        for (Button button : buttons) {
            float scale = (button == selectedButton) ? 1.3f : 1.0f;
            button.setScaleX(scale);
            button.setScaleY(scale);
        }
    }


    void updateQuiz(String question, int cnt) {
        quiz.setText(question);
        setImageView(selectedAnimalOnKr);
        choiceButton1.setText("O");
        choiceButton2.setText("X");
        if (cnt == 1) {
            ans = 1;
        } else {
            ans = 2;
        }
    }

    private void setImageView(String animal){
        imageView = findViewById(R.id.imageView5);
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
        }else if (animal == "흑코뿔소"){
            imageView.setImageResource(R.drawable.rhino);
        }else if (animal == "참새"){
            imageView.setImageResource(R.drawable.sparrow);
        }else if (animal == "트럼페터 백조"){
            imageView.setImageResource(R.drawable.bird);
        }else if (animal == "까치"){
            imageView.setImageResource(R.drawable.mag);
        }else if (animal == "흰머리독수리"){
            imageView.setImageResource(R.drawable.bald);
        }else if (animal == "여우올빼미"){
            imageView.setImageResource(R.drawable.eared);
        }
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