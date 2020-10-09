package com.kgbrussia7.a7exegerussian;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.AdRequest;


import java.util.ArrayList;
import java.util.Collections;

public class GameActivity extends AppCompatActivity {
    private TextView textViewQuestion;
    private TextView textViewDescription;
    private TextView textViewFalse;
    private TextView textViewTrue;
    private Button buttonNext;
    private ArrayList<String> rightQuestions;
    private ArrayList<String> wrongAnswers;
    private ArrayList<String> listOfAnswers;
    private ArrayList<String> listOfDescriptions;
    private ArrayList<String> listOfNumbers;
    private EditText editTextAnswer;
    private String Answer;
    private int levelUpWrong = 0;
    private int levelUpRight;
    private int score;
    private InterstitialAd mInterstitialAd;


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, com.kgbrussia7.a7exegerussian.MainActivity.class);
        startActivity(intent);
    }

    public ArrayList<String> fullFillListOfNumbers(ArrayList<String> list){
        ArrayList<String> newList = new ArrayList<>();
        for(int i = 0; i < list.size(); i++){
            newList.add(Integer.toString(i));
        }
        return newList;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getSupportActionBar().hide();
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-2659140064224348/2995445495");
        mInterstitialAd.loadAd(new AdRequest.Builder().build());

        textViewFalse = findViewById(R.id.textViewFalse);
        buttonNext = findViewById(R.id.buttonNext);
        textViewTrue = findViewById(R.id.textViewTrue);
        textViewQuestion = findViewById(R.id.textViewQuestion);
        textViewDescription = findViewById(R.id.textViewDescription);
        textViewDescription.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/font.ttf"));
        textViewQuestion.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/font.ttf"));
        textViewFalse.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/font.ttf"));
        textViewTrue.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/font.ttf"));
        buttonNext.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/font.ttf"));
        editTextAnswer = findViewById(R.id.editTextAnswer);
        editTextAnswer.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/font.ttf"));
        editTextAnswer.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER){
                    checkAnswer();
                    return true;
                }
                return false;
            }
        });
        rightQuestions = fullFillArrayOfRigthStatements();
        wrongAnswers = fullFillArrayOfWrongStatements();
        listOfAnswers = fullFillArrayOfAnswers();
        listOfDescriptions = fullFillArrayOfDescriptions();

        textViewQuestion.setText(createQuestionText(rightQuestions, wrongAnswers));

        listOfNumbers = fullFillListOfNumbers(listOfAnswers);
        //скрываем textView
        textViewTrue.setVisibility(View.INVISIBLE);
        textViewFalse.setVisibility(View.INVISIBLE);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        score = preferences.getInt("score", 0);
        if (score == 0){
            Collections.shuffle(rightQuestions);
            Collections.shuffle(listOfNumbers);
            editTextAnswer.setText(listOfAnswers.get(Integer.parseInt(listOfNumbers.get(0))));
        }





    }

    public void onClickNext(View view) {
        checkAnswer();
    }

    public void newQuestion(){
        textViewQuestion.setText(createQuestionText(rightQuestions, wrongAnswers));
    }

    public void checkAnswer(){
        Answer = editTextAnswer.getText().toString();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        boolean answerIsRight = false;
        levelUpWrong = preferences.getInt("levelWrong", levelUpWrong);
        answerIsRight = Answer.equals(getStatement(Integer.parseInt(listOfNumbers.get(levelUpWrong)), listOfAnswers));
        if(answerIsRight){
            levelUpWrong++;
            levelUpRight += 4;
            preferences.edit().putInt("levelWrong", levelUpWrong).apply();
            preferences.edit().putInt("levelRight", levelUpRight).apply();
            if(levelUpWrong >= wrongAnswers.size()){
                preferences.edit().putInt("levelWrong", 0).apply();
            }
            if(levelUpRight >= rightQuestions.size()){
                preferences.edit().putInt("levelRight", -1).apply();
            }
            newQuestion();
            Answer = "";
            editTextAnswer.selectAll();
            editTextAnswer.clearComposingText();
            score++;
            preferences.edit().putInt("score", score).apply();
            textViewQuestion.setVisibility(View.INVISIBLE);
            textViewTrue.setVisibility(View.VISIBLE);
            textViewTrue.postDelayed(new Runnable() {
                @Override
                public void run() {
                    textViewQuestion.setVisibility(View.VISIBLE);
                    textViewTrue.setVisibility(View.INVISIBLE);
                }
            },500);
            if(score % 5 == 1){
                mInterstitialAd = new InterstitialAd(this);
                mInterstitialAd.setAdUnitId("ca-app-pub-2659140064224348/2995445495");
                mInterstitialAd.loadAd(new AdRequest.Builder().build());
            }
            if(mInterstitialAd.isLoaded() && score != 0 && score % 5 == 2){
                mInterstitialAd.show();
            }

        }else{
            textViewQuestion.setVisibility(View.INVISIBLE);
            textViewFalse.setVisibility(View.VISIBLE);
            textViewFalse.postDelayed(new Runnable() {
                @Override
                public void run() {
                    textViewQuestion.setVisibility(View.VISIBLE);
                    textViewFalse.setVisibility(View.INVISIBLE);
                }
            },500);
        }
    }
    public void onClickShowDescriptions(View view) {
        String result = getStatement(Integer.parseInt(listOfNumbers.get(levelUpWrong)),listOfDescriptions);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(String.format("Ответ: %s\n",getStatement(Integer.parseInt(listOfNumbers.get(levelUpWrong)),listOfAnswers)))
                .setMessage(result)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();


    }

    public String createQuestionText(ArrayList<String> arrayListRight,ArrayList<String> arrayListWrong){
        int wrongPos = (int)(Math.random() * 5);
        StringBuilder builder = new StringBuilder();
        String result = "";
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        levelUpRight = preferences.getInt("levelRight", -1);
        for(int i = 0; i <= 4;i++){
            if (i != wrongPos){
                if (levelUpRight < arrayListRight.size()){
                    levelUpRight++;
                }else{
                    levelUpRight = 0;
                }

                builder.append(getStatement(levelUpRight, arrayListRight) + "\n" + "\n");

            }else {
                levelUpWrong = preferences.getInt("levelWrong",levelUpWrong);

                builder.append(getStatement(levelUpWrong, arrayListWrong)+ "\n" + "\n");

            }
        }
        result = builder.toString();
        return result;
    }

    public String getStatement(int pos, ArrayList<String> arrayList){
        String result = "";
        if (pos < arrayList.size()){
            result = arrayList.get(pos);
        }
        return result;
    }





    public ArrayList<String> fullFillArrayOfRigthStatements(){
        ArrayList<String> rightQuestions = new ArrayList<>();
        rightQuestions.add("ИДЯ по бульвару");
        rightQuestions.add("ПРИВЕДШИЙ к успеху");
        rightQuestions.add("несколько ДЖИНСОВ");
        rightQuestions.add("бежит намного БЫСТРЕЕ");
        rightQuestions.add("столы для КУХОНЬ");
        rightQuestions.add("ЛАЗИТ по заборам");
        rightQuestions.add("НАПОИВ чаем");
        rightQuestions.add("более ПОЛУТОРАСТА подписчиков");
        rightQuestions.add("КРЕПЧАЙШЕЕ рукопожатие");
        rightQuestions.add("ДВОЕ очков");
        rightQuestions.add("несколько КЕГЛЕЙ");
        rightQuestions.add("ЧЕТЫРЬМЯСТАМИ рублями");
        rightQuestions.add("несколько СОЛДАТ");
        rightQuestions.add("быть ВЫШЕ всех");
        rightQuestions.add("АЖЖЁТСЯ огонь");
        rightQuestions.add("разные ВОЗРАСТЫ");
        rightQuestions.add("ЗАКУТАВШИСЬ в одеяло");
        rightQuestions.add("ОКРЕПШИЙ организм");
        rightQuestions.add("ОБГРЫЗЕННАЯ корка хлеба");
        rightQuestions.add("находится в АЭРОПОРТУ");
        rightQuestions.add("на ДВЕСТИ шестой странице");
        rightQuestions.add("это яблоко намного КРУПНЕЕ");
        rightQuestions.add("земные НЕДРА");
        rightQuestions.add("НАТЕРЕВ солью");
        rightQuestions.add("более НУЖНЫЙ элемент");
        rightQuestions.add("несколько АБЗАЦЕВ");
        rightQuestions.add("в ОБЕИХ колоннах");
        rightQuestions.add("ЛЯГТЕ на диван");
        rightQuestions.add("звучит не менее ГРОМКО");
        rightQuestions.add("ПОЕЗЖАЙТЕ вперёд");
        rightQuestions.add("нет ТУФЕЛЬ");
        rightQuestions.add("опытные ТРЕНЕРЫ");
        rightQuestions.add("оказался БОЙЧЕ всех");
        rightQuestions.add("с ПЯТИСОТЫМ конкурсантом");
        rightQuestions.add("им ДВИЖЕТ чувство долга");
        rightQuestions.add("бархатных ПЛАТЬЕВ");
        rightQuestions.add("ЧУДЕСНЕЙШИЙ момент");
        rightQuestions.add("СБЕРЕЖЁТ здоровье");
        rightQuestions.add("закупили ПОЛТОРАСТА учебников");
        rightQuestions.add("объяснил более ДОХОДЧИВО");
        rightQuestions.add("ЛОЖИТЕСЬ на пол");
        rightQuestions.add("ИХ вещи");
        rightQuestions.add("ПРОПОЛОЩИ бельё");
        rightQuestions.add("у ОБОИХ солдат");
        rightQuestions.add("с ЧЕТЫРЬМЯ детьми");
        rightQuestions.add("порванный КЕД");
        rightQuestions.add("ЕЗДИТ в клуб");
        rightQuestions.add("окно занавешено ТЮЛЕМ");
        rightQuestions.add("ШЕСТЬЮДЕСЯТЬЮ попытками");
        rightQuestions.add("несколько ПОМЕСТИЙ");
        rightQuestions.add("ДВОЕ саней");
        rightQuestions.add("он ВСЕХ БЫСТРЕЕ");
        rightQuestions.add("не ЛЕЗЬ наверх");
        rightQuestions.add("в АЭРОПОРТУ");
        rightQuestions.add("новые СОРТА");
        rightQuestions.add("на ОБЕИХ сторонах");
        rightQuestions.add("килограмм ТОМАТОВ");
        rightQuestions.add("ПЯТЬЮДЕСЯТЬЮ листами");
        rightQuestions.add("стирка ПОЛОТЕНЕЦ");
        rightQuestions.add("СЛОЖНЕЙШИЙ текст");

        rightQuestions.add("СМОТРЯ вдаль");
        rightQuestions.add("опытные ДОКТОРА");
        rightQuestions.add("в ИХ произведениях");
        rightQuestions.add("в ОБОИХ случаях");
        rightQuestions.add("в ТРЁХСТАХ метрах");
        rightQuestions.add("ЛОПНУВШИЙ воздушный шарик");
        rightQuestions.add("выступил наиболее УДАЧНО");
        rightQuestions.add("ЛЯГТЕ на бок");
        rightQuestions.add("более ЧЕТЫРЁХСОТ картин");
        rightQuestions.add("килограмм МАНДАРИНОВ");
        rightQuestions.add("для ОБЕИХ участниц");
        rightQuestions.add("ПОЕЗЖАЙ вперёд");
        rightQuestions.add("кожа ТУФЕЛЬ");
        rightQuestions.add("из ТРИСТА пятой аудитории");
        rightQuestions.add("приглашённые ПРОФЕССОРА");
        rightQuestions.add("с ЧЕТЫРЬМЯ тысячами");
        rightQuestions.add("открывается ВНУТРЬ");
        rightQuestions.add("разделить ПОПОЛАМ");
        rightQuestions.add("около ПЯТИСОТ рублей");
        rightQuestions.add("из ОБОИХ кранов");
        rightQuestions.add("в ПОЛУТОРАСТА метрах от дома");
        rightQuestions.add("две ЗОЛОТЫЕ медали");
        rightQuestions.add("ГОРЯЧИЙ кофе");
        rightQuestions.add("опытные ИНЖЕНЕРЫ");
        rightQuestions.add("лучшие ПОВАРА");
        rightQuestions.add("пользуется новым ШАМПУНЕМ");
        rightQuestions.add("до ДВЕ тысячи двадцатого года");
        rightQuestions.add("банка КОНСЕРВОВ");
        rightQuestions.add("голос УМОЛК");
        rightQuestions.add("много СПЛЕТЕН");
        rightQuestions.add("наиболее ОТВЕТСТВЕННЫЙ");
        rightQuestions.add("СЕМИСОТ семидесяти экземпляров");
        rightQuestions.add("образ ИСЧЕЗ");
        rightQuestions.add("ТРОЕ ножниц");
        rightQuestions.add("КРОШЕЧНЫЙ домишко");
        rightQuestions.add("пятьсот ПЯТИДЕСЯТАЯ страница");
        rightQuestions.add("ВЫГЛЯДИТ более ЭСТЕТИЧНО");
        rightQuestions.add("ПЛЕЩУТСЯ в реке");
        rightQuestions.add("в ОБОИХ ящиках");
        rightQuestions.add("килограмм МАНДАРИНОВ");
        rightQuestions.add("ЧЁРНЫЙ кофе");
        rightQuestions.add("порвала ТАПОЧКУ");
        rightQuestions.add("проездил до СУМЕРЕК");
        rightQuestions.add("из обоих БЛЮДЕЦ");
        rightQuestions.add("сдайте ПАСПОРТА");
        rightQuestions.add("несколько ГНЕЗДОВИЙ");
        rightQuestions.add("пара ТУФЕЛЬ");
        rightQuestions.add("сказал БОЛЕЕ УВЕРЕННО");
        rightQuestions.add("из свежих АБРИКОСОВ");
        rightQuestions.add("клуб для БАРЫШЕНЬ");
        rightQuestions.add("о ШЕСТИСТАХ жильцах");
        rightQuestions.add("в ИХ квартире");
        rightQuestions.add("дожить до ДЕВЯНОСТА лет");
        rightQuestions.add("ГЛУБОЧАЙШЕЕ озеро");
        rightQuestions.add("вязаные СВИТЕРЫ");
        rightQuestions.add("коробка КОНСЕРВОВ");
        rightQuestions.add("менее СТРОГИЙ тон");
        rightQuestions.add("шесть ЩУПАЛЕЦ");
        rightQuestions.add("СВЕЖАЯ салями");
        rightQuestions.add("ПРОМОК под дождём");

        rightQuestions.add("сорта АБРИКОСОВ");
        rightQuestions.add("много ДЕЛ");
        rightQuestions.add("около ШЕСТИСОТ метров");
        rightQuestions.add("левая КРОССОВКА");
        rightQuestions.add("более ТЕРПЕЛИВ");
        rightQuestions.add("ПЯТЬЮСТАМИ конвертами");
        rightQuestions.add("испанские ВЕЕРА");
        rightQuestions.add("наиболее ЧЕТКО");
        rightQuestions.add("разные ВОЗРАСТЫ");
        rightQuestions.add("восемь АПЕЛЬСИНОВ");
        rightQuestions.add("десять КИЛОГРАММОВ апельсинов");
        rightQuestions.add("лампочка ЗАЖЖЕТСЯ");
        rightQuestions.add("уважаемые СЛЕСАРИ");
        rightQuestions.add("спелых ПОМИДОРОВ");
        rightQuestions.add("ящик МАКАРОН");
        rightQuestions.add("ШЕСТЬЮСТАМИ рублями");
        rightQuestions.add("модных ТУФЕЛЬ");
        rightQuestions.add("двух КОЧЕРЁГ");
        rightQuestions.add("ВОСЕМЬЮСТАМИ тридцатью");
        rightQuestions.add("нет двух ПРОСТЫНЬ");
        rightQuestions.add("ЛАЖУ по деревьям");
        rightQuestions.add("ДИРЕКТОРА школ");
        rightQuestions.add("триста КИЛОГРАММОВ");
        rightQuestions.add("без ПОГОН");
        rightQuestions.add("более ПЯТИСОТ долларов");
        rightQuestions.add("опытные ИНЖЕНЕРЫ");
        rightQuestions.add("розовый КАКАДУ");
        rightQuestions.add("более ПРОСТО");
        rightQuestions.add("заслуженные ДИРЕКТОРА");
        rightQuestions.add("отряд ПАРТИЗАН");
        rightQuestions.add("до ДВЕ тысячи пятого года");
        rightQuestions.add("несколько ПОЛОТЕНЕЦ");
        rightQuestions.add("пара теплых ЧУЛОК");
        rightQuestions.add("ОБЕИХ сторон");
        rightQuestions.add("ДВУМЯСТАМИ книгами");
        rightQuestions.add("идти ДАЛЬШЕ");
        rightQuestions.add("ПОЕЗЖАЙ на юг");
        rightQuestions.add("ТРОЕ мужчин");
        rightQuestions.add("СТРАШНАЯ цеце");
        rightQuestions.add("пять ПЕЧЕНИЙ");
        rightQuestions.add("ребёнок ОКРЕП");
        rightQuestions.add("руки были СЛАБЕЕ");
        rightQuestions.add("нет ПОЛОТЕНЕЦ");
        rightQuestions.add("НЕГЛАСНЫМ табу");
        rightQuestions.add("полкилограмма ЯБЛОК");
        rightQuestions.add("СЕМИДЕСЯТИ лет");
        rightQuestions.add("наши ПАСПОРТА");
        rightQuestions.add("сделать ЛУЧШЕ");

        rightQuestions.add("сто ГРАММОВ");
        rightQuestions.add("искать ПАРТИЗАН");
        rightQuestions.add("ОБОИХ компонентов");
        rightQuestions.add("варенье СЛАЩЕ");
        rightQuestions.add("КРЕМЫ для лица");
        rightQuestions.add("несколько ПОЛОТЕНЕЦ");
        rightQuestions.add("ПОЕЗЖАЙ в город");
        rightQuestions.add("в ДВЕ ТЫСЯЧИ ПЕРВОМ году");
        rightQuestions.add("ИХ дом");
        rightQuestions.add("вскарабкаться на ПОДМОСТКИ");
        rightQuestions.add("нет МАНЖЕТ");
        rightQuestions.add("самый ВЕСЁЛЫЙ");
        rightQuestions.add("более ЯРОСТНО");
        rightQuestions.add("задача ЛЕГЧЕ");
        rightQuestions.add("пара ТУФЕЛЬ");
        rightQuestions.add("СЛОЖНОЕ ралли");
        rightQuestions.add("спелых ВИШЕН");
        rightQuestions.add("на ширину ПЛЕЧ");
        rightQuestions.add("яркого ПЛАМЕНИ");
        rightQuestions.add("на ОБЕИХ сторонах");
        rightQuestions.add("ЯРЧАЙШИЙ цвет");
        rightQuestions.add("ЛУЧШЕ всего");
        rightQuestions.add("ДВУМЯСТАМИ страницами");
        rightQuestions.add("заслуженные ДИРЕКТОРА");
        rightQuestions.add("килограмм ЯБЛОК");
        rightQuestions.add("пара ТУФЕЛЬ");
        rightQuestions.add("пара НОСКОВ");
        rightQuestions.add("несколько ТАДЖИКОВ");
        rightQuestions.add("много ТУРОК");
        rightQuestions.add("на ОБЕИХ стенах");
        rightQuestions.add("говорите менее БЫСТРО");
        rightQuestions.add("ПОЕЗЖАЙТЕ дальше");
        rightQuestions.add("ИХ паспорта");
        rightQuestions.add("лучшие ЛЕКТОРЫ");
        rightQuestions.add("ТРЁМСТАМ участникам");
        rightQuestions.add("НАПОИВ чаем");
        rightQuestions.add("в БЛИЖАЙШИЕ дни");
        rightQuestions.add("сменить ПРОСТЫНЮ");
        rightQuestions.add("около ПЯТИСОТ экспонатов");
        rightQuestions.add("двое ГУСАР");
        rightQuestions.add("ВАЖНЕЙШИЙ вопрос");
        rightQuestions.add("чистых ПОЛОТЕНЕЦ");
        rightQuestions.add("поделить ПОПОЛАМ");
        rightQuestions.add("около СЕМИСОТ рублей");
        rightQuestions.add("звучит не менее ГРОМКО");
        rightQuestions.add("ПОЕЗЖАЙТЕ вперёд");
        rightQuestions.add("нет ТУФЕЛЬ");
        rightQuestions.add("опытные ТРЕНЕРЫ");
        rightQuestions.add("сдайте ПАСПОРТА");
        rightQuestions.add("несколько ГНЕЗДОВИЙ");
        rightQuestions.add("пара ТУФЕЛЬ");
        rightQuestions.add("сказал БОЛЕЕ УВЕРЕННО");
        rightQuestions.add("клуб для БАРЫШЕНЬ");
        rightQuestions.add("о ШЕСТИСТАХ жильцах");
        rightQuestions.add("в ИХ квартире");
        rightQuestions.add("из свежих АБРИКОСОВ");
        rightQuestions.add("ПРОБУЯ на вкус");
        rightQuestions.add("никому не ЗАВИДУЮ");
        rightQuestions.add("прочитал более ВЫРАЗИТЕЛЬНО");
        rightQuestions.add("богатые КНЯЗЬЯ");


        rightQuestions.add("более КРЕПКИЙ");
        rightQuestions.add("ОТКРЫВ книгу");
        rightQuestions.add("несколько СПАЛЕН");
        rightQuestions.add("ШЕСТЬЮДЕСЯТЬЮ сотрудниками");
        rightQuestions.add("шесть ПАДЕЖЕЙ");
        rightQuestions.add("пой ЗВОНЧЕ");
        rightQuestions.add("ЛЯГ на кушетку");
        rightQuestions.add("благодаря ИМ");
        rightQuestions.add("забрать из ЯСЛЕЙ");
        rightQuestions.add("наиболее УМНЫЙ");
        rightQuestions.add("мебель для КУХОНЬ");
        rightQuestions.add("в тысяча ВОСЬМИСОТОМ году");
        rightQuestions.add("голос ЗВОНЧЕ");
        rightQuestions.add("стебли ГЕОРГИНОВ");
        rightQuestions.add("к ВОСЬМИСОТОМУ году");
        rightQuestions.add("НАПОИВ чаем");
        rightQuestions.add("несколько БАШЕН");
        rightQuestions.add("шерстяные СВИТЕРЫ");
        rightQuestions.add("НАИСЛОЖНЕЙШАЯ операция");
        rightQuestions.add("возьми нитку ПОТОЛЩЕ");
        rightQuestions.add("более ПОЛУТОРАСТА тетрадей");
        rightQuestions.add("заговорил БОЛЕЕ ВЕСЕЛО");
        rightQuestions.add("мать ЧЕТВЕРЫХ детей");
        rightQuestions.add("килограмм БАКЛАЖАНОВ");
        rightQuestions.add("ПРИОБРЕТШИЙ дом");
        rightQuestions.add("ДЕВЯНОСТА саженцам");
        rightQuestions.add("произнёс более ТИХО");
        rightQuestions.add("ПОЕЗЖАЙ в город");
        rightQuestions.add("ДВЕ ТЫСЯЧИ ДЕСЯТЫЙ год");
        rightQuestions.add("коробка СВЕЧЕЙ");
        rightQuestions.add("наши ИНЖЕНЕРЫ");
        rightQuestions.add("КЛАДИ на стол");
        rightQuestions.add("заслуженные ДИРЕКТОРА");
        rightQuestions.add("полкило ЯБЛОК");
        rightQuestions.add("фабричные ПРЕССЫ");
        rightQuestions.add("ЧЕТВЕРО ребят");
        rightQuestions.add("БЛИЖАЙШАЯ деревня");
        rightQuestions.add("четверо КАДЕТ");
        rightQuestions.add("ДЕВЯНОСТА рублями");
        rightQuestions.add("отблески ПЛАМЕНИ");
        rightQuestions.add("две пары НОСКОВ");
        rightQuestions.add("СЕМЬ девочек");
        rightQuestions.add("ПОЕЗЖАЙ в Подмосковье");
        rightQuestions.add("она стала более ПРИЯТНОЙ");
        rightQuestions.add("СЕМЬЮДЕСЯТЬЮ годами");
        rightQuestions.add("пара САПОГ");
        rightQuestions.add("(нет) ПОЛУТОРАСТА книг");
        rightQuestions.add("килограмм ПОМИДОРОВ");

        return rightQuestions;
    }

    public ArrayList<String> fullFillArrayOfWrongStatements(){

        ArrayList<String> list = new ArrayList<>();
        list.add("на ДВУХСОТ третьем километре");
        list.add("забрать из ЯСЕЛЬ");
        list.add("ЛОЖИ аккуратно");
        list.add("в ДВУХ тысячи шестой раз");
        list.add("к ВОСЕМЬСОТОМУ году");
        list.add("много ВРЕМЯ");
        list.add("о СЕМИСТА километрах");
        list.add("по ОБОИМ сторонам");
        list.add("ИСПЕКЁТ булочки");
        list.add("ответственные СТОРОЖИ");
        list.add("до ПОЛТОРЫ сотен");
        list.add("ОБГРЫЗАННАЯ зайцами кора дерева");
        list.add("ЗАЖГЁШЬ свечу");
        list.add("с ВОСЬМИСТАМИ блюдами");
        list.add("ПОПРОБОВАЕМ изучить");

        list.add("с ШЕСТИСТАМИ рублями");
        list.add("салат из ПОМИДОР");
        list.add("ОБГРЫЗАННОЕ яблоко");
        list.add("говори более ЯСНЕЕ");
        list.add("ящик АПЕЛЬСИН");
        list.add("прибавить к ТРЁХСТАМ");
        list.add("старше НЕГО");
        list.add("потеряла ТУФЕЛЬ");
        list.add("пирог с ПОВИДЛОЙ");
        list.add("в ДВУХСОТ пятой аудитории");
        list.add("СЪЕЗДИЕТ на дачу");
        list.add("ПОЛТОРАСТАМИ произведениями");
        list.add("совсем ОЗЯБНУЛ");
        list.add("стоять около РОЯЛИ");
        list.add("в ШЕСТИСТА шагах");

        list.add("более ТОЧНЕЕ (ответить)");
        list.add("САМЫЙ КРЕПЧАЙШИЙ чай");
        list.add("с ШЕСТИСТАМИ рублями");
        list.add("менее СЕМИСТА километров");
        list.add("самый ВКУСНЕЙШИЙ");
        list.add("отряд ПАРТИЗАНОВ");
        list.add("более ПРОСТЕЙШИЙ");
        list.add("ЕХАЙТЕ осторожно");
        list.add("по ОБОИМ сторонам");
        list.add("ДВОЕ подруг");
        list.add("на ВОСЬМИСТА гектарах");
        list.add("несколько ЧУЛКОВ");
        list.add("с МИРА по нитке");
        list.add("около ДВУХСТА");
        list.add("ПОКЛАДИ книгу");

        list.add("ВЕЧЕРНЕЕ Сочи");
        list.add("самый ЯРЧАЙШИЙ представитель");
        list.add("ПЯТИСТА рублей");
        list.add("ДВУХСТАМ килограммам");
        list.add("группа ЦЫГАНОВ");
        list.add("на ОБОИХ дорожках");
        list.add("ЕЗДИЮТ на автобусе");
        list.add("вкусные КРЕНДЕЛЯ");
        list.add("ЗДОРОВШЕ других");
        list.add("ДВУХСОТ второй кабинет");
        list.add("МАХАЕТ платком");
        list.add("по ОБОИМ сторонам");
        list.add("ПОЛТОРАСТАМИ произведениями");
        list.add("совсем ОЗЯБНУЛ");
        list.add("занавесить ТЮЛЬЮ");

        list.add("ВЗМОКНУЛ от стараний");
        list.add("ВОСЬМИДЕСЯТЬЮ копейками");
        list.add("в ПОЛУТОРАХ метрах");
        list.add("в ДВУХСТА экземплярах");
        list.add("в ПОЛУТОРАСТАХ милях");
        list.add("НЕДРЫ земли");
        list.add("несколько БЛЮДЦЕВ");
        list.add("выглядишь БОЛЕЕ КРАСИВЕЕ");
        list.add("ПЯТЬСОТЫЙ заказ");
        list.add("ЕЗЖАЙ домой");
        list.add("о СТАХ щенках");
        list.add("пой более ЗВУЧНЕЕ");



        return list;
    }

    public ArrayList<String> fullFillArrayOfAnswers(){
        ArrayList<String> list = new ArrayList<>();
        list.add("двести");
        list.add("яслей");
        list.add("клади");
        list.add("две");
        list.add("восьмисотому");
        list.add("времени");
        list.add("семистах");
        list.add("обеим");
        list.add("испечёт");
        list.add("сторожа");
        list.add("полутора");
        list.add("обгрызенная");
        list.add("зажжёшь");
        list.add("восемьюстами");
        list.add("попробуем");

        list.add("шестьюстами");
        list.add("помидоров");
        list.add("обгрызенное");
        list.add("ясно");
        list.add("апельсинов");
        list.add("трёмстам");
        list.add("его");
        list.add("туфлю");
        list.add("повидлом");
        list.add("двести");
        list.add("съездит");
        list.add("полутораста");
        list.add("озяб");
        list.add("рояля");
        list.add("шестистах");

        list.add("точно");
        list.add("самыйкрепкий");
        list.add("шестьюстами");
        list.add("семисот");
        list.add("самыйвкусный");
        list.add("партизан");
        list.add("болеепростой");
        list.add("поезжайте");
        list.add("обеим");
        list.add("двеподруги");
        list.add("восьмистах");
        list.add("чулок");
        list.add("миру");
        list.add("двухсот");
        list.add("положи");

        list.add("вечерний");
        list.add("яркий");
        list.add("пятисот");
        list.add("двумстам");
        list.add("цыган");
        list.add("обеих");
        list.add("ездят");
        list.add("крендели");
        list.add("здоровее");
        list.add("двести");
        list.add("машет");
        list.add("обеим");
        list.add("полуторастами");
        list.add("озяб");
        list.add("тюлем");

        list.add("взмок");
        list.add("восьмьюдесятью");
        list.add("полутора");
        list.add("двухстах");
        list.add("полутораста");
        list.add("недра");
        list.add("блюдец");
        list.add("болеекрасиво");
        list.add("пятисотый");
        list.add("поезжай");
        list.add("ста");
        list.add("звучно");

        return list;
    }

    public ArrayList<String> fullFillArrayOfDescriptions(){
        ArrayList<String> list = new ArrayList<>();
        list.add("В порядковых числительных склоняется только последнее слово. Соответственно, «двести третий, двести третьего, двести третьему» и т. д.");
        list.add("Имя существительное ясли имеет основу, заканчивающуюся на мягкий согласный. Традиционно в таких именах существительных выбирается окончание -ей в род. пад. мн. ч. Соответственно, форма родительного падежа – яслей.");
        list.add("Глагол положить в русском языке не имеет нормативной бесприставочной формы, необходима замена либо на форму, содержащую приставку (положи), либо на форму синонимичного глагола класть (клади).");
        list.add("В порядковом числительном склоняется только последняя часть, поэтому правильной будет форма две тысячи шестой раз.");
        list.add("Восьмисотый – следующий за семьсот девяносто девятым. Порядковые числительные трёхсотый, четырёхсотый и т.д. образуются от формы родительного падежа количественных числительных – трёхсот, четырёхсот… восьмисот (но: триста первый, четыреста четвёртый, восемьсот шестнадцатый…). Соответственно, при склонении порядковых числительных изменяется только последняя часть корня: сотый, сотого, сотому, а остальные корни имени числительного остаются неизменными: восьмисотого, восьмисотому, восьмисотым.");
        list.add("В данном словосочетании слово «много» предполагает форму родительного падежа зависимого слова. Для разносклоняемого имени существительного «время» это форма «времени».");
        list.add("В количественном числительном семьсот склоняются обе части (семь и сто). Соответственно, в форме предложного падежа будет семистах.");
        list.add("обеим");
        list.add("В соответствии с нормами русского литературного языка верными вариантами личных форм глагола испечь будут следующие: (я) испеку, (мы) испечём, (ты) испечёшь, (вы) испечёте, (он, она, оно) испечёт, (они) испекут. Употребление формы испекёт является ошибочным (это просторечная форма).");
        list.add("В соответствии с морфологическими нормами русского языка верная форма множественного числа слова сторож – сторожа.");
        list.add("Числительное «полтора» имеет только две формы: вин.п. = им.п., в остальных падежах «полутора».");
        list.add("Страдательные причастия, образованные от глаголов на -АТЬ/-ЯТЬ, заканчиваются на -АННЫЙ/-ЯННЫЙ, в остальных случаях на -ЕННЫЙ: обгрызть – обгрызенный. Применяя это правило, нельзя менять вид глагола. Обгрызенный – совершенный вид. Значит, это причастие образовано от глагола совершенного вида «обгрызть» (что сделать?), а не от глагола «обгрызать» (что делать?).");
        list.add("В соответствии с нормами русского языка верные варианты личных форм глагола зажечь следующие: (я) зажгу, (мы) зажжём, (ты) зажжёшь, (вы) зажжёте, (он, она, оно) зажжёт, (они) зажгут.");
        list.add("В количественном числительном восемьсот склоняются обе части (восемь и сто). Соответственно, в форме творительного падежа будет восемьюстами.");
        list.add("Форма «попробоваем» является просторечной, недопустимой в литературном языке.");

        list.add("У сложных числительных «пятьдесят – восемьдесят», «двести – девятьсот» склоняются обе части слова. Сравните: с шестью – с шестьюстами.");
        list.add("Формы родительного падежа множественного числа существительных, обозначающих фрукты и овощи, имеют, как правило, окончание -ОВ: апельсинов, абрикосов, гранатов, мандаринов, помидоров. Но: яблок (нулевое окончание).");
        list.add("Страдательные причастия, образованные от глаголов на -АТЬ/-ЯТЬ, заканчиваются на -АННЫЙ/-ЯННЫЙ, в остальных случаях на -ЕННЫЙ: обгрызть – обгрызенный.");
        list.add("Форма сравнительной степени наречия может состоять из одного слова (яснее) или двух (более ясно, менее ясно).");
        list.add("Формы родительного падежа множественного числа существительных, обозначающих фрукты и овощи, имеют, как правило, окончание -ОВ: апельсинов, абрикосов, гранатов, мандаринов, помидоров. Но: яблок (нулевое окончание).");
        list.add("У сложного числительного «триста» склоняются обе части слова. Правильно – «прибавить к ТРЁМСТАМ» (дательный падеж).");
        list.add("Правильно – «старше его». Местоимения -го лица после форм сравнительной степени употребляются без Н: выше её, лучше его, успешнее их. Буква Н добавляется после непроизводных предлогов: для неё, без него, у них.");
        list.add("Существительное ту́фля женского рода, а не мужского, поэтому в форме винительного падежа должно быть туфлю.");
        list.add("Существительное повидло среднего рода, поэтому в форме Творительного падежа оно имеет окончание -ом (повидлом).");
        list.add("Правильно – «в двести пятой аудитории». У порядковых числительных изменяется только последнее слово. В данном случае изменяется слово «пятая – пятой»; слово «двести» не меняет своей формы: двести пятая аудитория, в двести пятой аудитории, из двести пятой аудитории.");
        list.add("Глагол «съездить» – 2-го спряжения, поэтому правильно: он съездит, они съездят (в окончаниях буквы И, Я).");
        list.add("полутораста");
        list.add("озяб");
        list.add("Существительное рояль мужского рода, поэтому в форме Родительного падежа (около чего?) должно быть рояля.");
        list.add("При склонении количественных числительных изменяются все части: шестьсот – шестИстАХ.");

        list.add("точно");
        list.add("самыйкрепкий");
        list.add("шестьюстами");
        list.add("семисот");
        list.add("самыйвкусный");
        list.add("партизан");
        list.add("болеепростой");
        list.add("поезжайте");
        list.add("обеим");
        list.add("двеподруги");
        list.add("восьмистах");
        list.add("чулок");
        list.add("миру");
        list.add("двухсот");
        list.add("положи");

        list.add("вечерний");
        list.add("яркий");
        list.add("пятисот");
        list.add("двумстам");
        list.add("цыган");
        list.add("обеих");
        list.add("ездят");
        list.add("крендели");
        list.add("здоровее");
        list.add("двести");
        list.add("машет");
        list.add("обеим");
        list.add("полуторастами");
        list.add("озяб");
        list.add("тюлем");

        list.add("взмок");
        list.add("восьмьюдесятью");
        list.add("полутора");
        list.add("двухстах");
        list.add("полутораста");
        list.add("недра");
        list.add("блюдец");
        list.add("болеекрасиво");
        list.add("пятисотый");
        list.add("поезжай");
        list.add("ста");
        list.add("звучно");

        return list;
    }



}
