package com.example.dragonquest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;

import com.example.dragonquest.databinding.ActivityBattleBinding;

public class BattleActivity extends AppCompatActivity {
    private ActivityBattleBinding binding;

    private Handler handler = new Handler();//スレッド内でテキストをいじる
    private int myhp = 100; //キャラのHPを保存する変数

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBattleBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        //初期非表示
        binding.battleMessage.setVisibility(View.INVISIBLE);    //バトルメッセージ非表示
        binding.battleEndButton.setVisibility(View.INVISIBLE);  //バトル終了ボタン非表示

        //ライフゲージ周りの設定
        binding.myHpBar.setMax(myhp);           //キャラの最大値の設定
        binding.myHpBar.setProgress(myhp);      //キャラの現在地の設定
        binding.ememyHpBar.setMax(myhp);        //エネミーの最大値の設定
        binding.ememyHpBar.setProgress(myhp);   //エネミーの現在地の設定
        binding.myHpText.setText(myhp + "/" + myhp);    //キャラのHPテキスト


        //左上ボタンクリック時の処理
        binding.skill1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //メッセージの表示
                binding.battleMessage.setVisibility(View.VISIBLE);
            }
        });

        //メッセージのクリック処理有効化
        binding.battleMessage.setClickable(true);
        //メッセージクリック時の処理
        binding.battleMessage.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                //メッセージの非表示
                binding.battleMessage.setVisibility(View.INVISIBLE);
            }
        });

        //HPが減る処理
        int hpPoint = 1;
        int maxHp = binding.myHpBar.getMax();   //hp最大値
        //スレッド
        new Thread(new Runnable() {
            @Override
            public void run() {
                //HPが0になるまで
                while (binding.myHpBar.getProgress() >= 0){
                    //HPを１ずつ減らす
                    myhp -= hpPoint;
                    binding.myHpBar.setProgress(myhp);  //バーの表示更新
                    handler.post(()->{
                       binding.myHpText.setText(myhp + "/" + maxHp);    //テキストの更新
                    });

                    //HPがゼロになったらボタン表示
                    if (myhp == 0){
                        handler.post(()->{
                            binding.battleEndButton.setText("リザルト画面へ");
                            binding.battleEndButton.setVisibility(View.VISIBLE);
                        });
                        //ループ終了
                        break;
                    }

                    try {
                        Thread.sleep(50);   //50ミリ秒待つ
                    }catch (InterruptedException e){

                    }
                }
            }

        }).start();
        //スレッドここまで

    }

}