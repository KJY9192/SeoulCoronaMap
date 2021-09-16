package com.kjy2021.seoulcoronamap;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

public class MenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        //제목줄에 닉네임
        getSupportActionBar().setTitle(G.nickname);
        //회원번호는 서브제목으로
        getSupportActionBar().setSubtitle("회원번호:" + G.id);

        //프로필 이미지 보여주기
        ImageView iv= findViewById(R.id.iv);
        Glide.with(this).load(G.profileUrl).into(iv);
    }
}