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

import com.google.gson.annotations.SerializedName;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Query;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import java.nio.LongBuffer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QuizActivity2 extends AppCompatActivity {

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
        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
            }
            @Override
            public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {
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
                                // Assuming 'onCompleted' is where you call 'updateQuiz'
                                listener.onCompleted(translatedText); // replace 'listener' with whatever your callback variable is
                            }
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }







    //GBIF API를 호출 코드
    String GBIF_API_ENDPOINT = "https://api.gbif.org/v1/";
    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(GBIF_API_ENDPOINT)
            .addConverterFactory(GsonConverterFactory.create())
            .build();
    GBIFService service = retrofit.create(GBIFService.class);

    //선택된 동물이 가장 많이 발견 되는 지ㅣ역을 찾아주는 함수
    private String findMostObservedRegion(List<GBIFResult> results) {
        if (results == null || results.isEmpty()) {
            return "No data available";
        }
        Map<String, Integer> regionCountMap = new HashMap<>();
        for (GBIFResult result : results) {
            String region = result.getRegion();
            if (region != null) {
                regionCountMap.put(region, regionCountMap.getOrDefault(region, 0) + 1);
            }
        }
        return regionCountMap.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("No data available");
    }
    public interface GBIFService {
        @GET("occurrence/search")
        Call<GBIFResponse> searchOccurrencesByTaxon(@Query("scientificName") String scientificName);
    }
    public class GBIFResponse {
        @SerializedName("results")
        private List<GBIFResult> results;
        public List<GBIFResult> getResults() {

            return results;
        }
    }
    public class GBIFResult {
        @SerializedName("country")
        private String region;
        public String getRegion() {return region;}
    }

    private Button choiceButton1;
    private Button choiceButton2;
    private Button choiceButton3;
    private Button choiceButton4;
    private TextView ox; //팝업을 위한 정/오답 변수
    private TextView exp; //팝업을 위한 해설 변수
    private ImageView gift; //팝업을 위한 상품 변수
    private TextView quiz;
    private ImageView imageView;
    private String animalName = " ";
    private String explan = " "; //해설
    private String selectedbtn; //클릭 된 버튼의 텍스트
    private String selectedAnimal; //선택된 동물(영어)
    private String ans; //퀴즈 정답
    private String rewardName;
    private int rewardExp = 0;

    private ImageView reward;

    private List<String> animalList;
    private List<String> regionList;
    private Random random = new Random();

    private String choisedAnimal;
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
        String[] Animals = {"Giraffa camelopardalis camelopardalis","Macropus rufus", "Canis lupus arctos", "Vulpes cana", "Ambystoma mexicanum",
                 "Aptenodytes forsteri", "Diceros bicornis", "Passer domesticus", "Cygnus buccinator", "Pica pica", "Haliaeetus leucocephalus",
                "Bubo bubo"};

        //동물들의 과학적이름을 그대로 PAPAGO API로 번역이 안되기 때문에 Animals 배열에 대응 되는 AnimalsOnKr 배열에 직접 동물들의 한국이름을 선언
        String[] AnimalsOnKr = {"누비아 기린", "빨간 캥거루", "북극늑대", "회색 여우", "악솔로틀", "황제 펭귄", "흑코뿔소", "참새", "트럼페터 백조", "까치", "흰머리독수리",
                "여우올빼미"};

        //답지로 나올만한 80개의 지역을 미리 선언
        String[] Region = {"남극", "유엔 중립지대", "팔레스타인 지역", "콜롬비아", "마다가스카르", "인도네시아", "호주", "코스타리카", "콩고", "남극대륙", "미국", "캐나다",
                "프랑스", "독일", "영국", "이탈리아", "일본", "브라질", "인도", "러시아", "그레이트브리튼 북아일랜드 연합왕국", "사우디 아라비아", "남아프리카 공화국", "아르헨티나", "멕시코", "터키", "스페인", "이란",
                "포르투갈", "스웨덴", "노르웨이", "핀란드", "덴마크", "아이슬란드", "벨기에", "네덜란드", "룩셈부르크", "스위스", "오스트리아", "그리스", "폴란드", "체코", "슬로바키아",
                "헝가리", "슬로베니아", "크로아티아", "보스니아 헤르체고비나", "세르비아", "몬테네그로", "코소보", "북마케도니아", "알바니아", "루마니아", "불가리아", "몰도바", "우크라이나",
                "리투아니아", "라트비아", "에스토니아", "아일랜드", "말타", "페루", "베네수엘라", "에콰도르", "볼리비아", "파푸아뉴기니", "필리핀", "타이", "베트남", "말레이시아", "캄보디아",
                "라오스", "방글라데시", "네팔", "스리랑카", "몽골", "카자흐스탄", "우즈베키스탄", "키르기스스탄", "타지키스탄", "투르크메니스탄", "이란", "이라크", "시리아", "요르단", "레바논",
                "이스라엘", "예멘", "오만", "아랍에미리트", "카타르", "바레인", "쿠웨이트"};

        animalList = new ArrayList<>(Arrays.asList(AnimalsOnKr));
        regionList = new ArrayList<>(Arrays.asList(Region));

        //Animals배열에서 랜덤으로 동물 선택
        int selectedIndex2 = random.nextInt(Animals.length);
        selectedAnimal = Animals[selectedIndex2];
        animalName = selectedAnimal;
        choisedAnimal = animalList.get(selectedIndex2);
        animalList.remove(selectedIndex2);



        // 선택된 동물이 가장 많이 나타나는 지역을 구해주는 함수 실행
        Call<GBIFResponse> call = service.searchOccurrencesByTaxon(animalName);
        call.enqueue(new Callback<GBIFResponse>() {
            @Override
            public void onResponse(Call<GBIFResponse> call, Response<GBIFResponse> response) {
                if (response.isSuccessful()) {
                    GBIFResponse gbifResponse = response.body();
                    String mostObservedRegion = findMostObservedRegion(gbifResponse.getResults());

                    //GBIF API로 가져온 데이터를 번역하기 위해 PAPAGO API 코드호출
                    editor.putString("fromGBIF", mostObservedRegion);
                    editor.apply();
                    translateText(new OnTranslationCompleteListener() {
                        @Override
                        public void onCompleted(String translatedText) {
                            ans = translatedText;
                            String quiz1 = choisedAnimal + "가(이) 가장 많이 발견 되는 지역은 뭘까?~";
                            updateQuiz(quiz1, translatedText);
                            explan = choisedAnimal + "가(이) 가장 많이 발견 되는 지역은 " + translatedText + "이지~";
                        }
                    });

                }
            }
            @Override
            public void onFailure(Call<GBIFResponse> call, Throwable t) {
                quiz.setText("오류");
            }
        });
    }


    void updateQuiz(String question, String region) {
        quiz.setText(question);
        setImageView(choisedAnimal);

        //정답과 오답3개를 랜덥으로 선택지 4개에 배치
        Random random = new Random();
        int randomNumber = random.nextInt(4) + 1;
        switch (randomNumber) {
            case 1:
                setButtonTranslation(choiceButton1, region);
                break;
            case 2:
                setButtonTranslation(choiceButton2, region);
                break;
            case 3:
                setButtonTranslation(choiceButton3, region);
                break;
            case 4:
                setButtonTranslation(choiceButton4, region);
                break;
        }

        List<Button> remainingButtons = new ArrayList<>();
        remainingButtons.add(choiceButton1);
        remainingButtons.add(choiceButton2);
        remainingButtons.add(choiceButton3);
        remainingButtons.add(choiceButton4);
        remainingButtons.remove(randomNumber - 1);

        for (Button btn : remainingButtons) {
            int randomIndex = random.nextInt(regionList.size());
            if(!region.equals(regionList.get(randomIndex))){
                setButtonTranslation(btn, regionList.get(randomIndex));
                regionList.remove(randomIndex);
            }
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

    //선택된 선택지버튼에 텍스트를 set
    private void setButtonTranslation(Button Button, String animal) {
        Button.setText(animal);
    }

    //클릭한 버튼을 저장 함수
    public void clickbtn(View view) {
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
    public void submitBtnClick(View view) {
        final Dialog popup_dialog = new Dialog(this);
        popup_dialog.setContentView(R.layout.popup_quiz);

        sharedPreferences = getSharedPreferences("MyAppPreferences", MODE_PRIVATE);
        editor = sharedPreferences.edit();
        reward = popup_dialog.findViewById(R.id.gift);

        ox = popup_dialog.findViewById(R.id.ox);
        exp = popup_dialog.findViewById(R.id.explan);
        gift = popup_dialog.findViewById(R.id.gift);

        popup_dialog.setCanceledOnTouchOutside(false);
        popup_dialog.show();

        exp.setText(explan);

        int currentExp = sharedPreferences.getInt("experience", 0);
        if(selectedbtn.equals(ans)){
            ox.setText("정답!");
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
