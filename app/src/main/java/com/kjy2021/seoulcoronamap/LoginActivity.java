package com.kjy2021.seoulcoronamap;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.kakao.sdk.auth.model.OAuthToken;
import com.kakao.sdk.common.util.Utility;
import com.kakao.sdk.user.UserApiClient;
import com.kakao.sdk.user.model.User;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;

public class LoginActivity extends AppCompatActivity {
    CircleImageView civ;
    TextView tvNickname;
    TextView tvEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //캐해시 값 얻어와서 Logcat 창에 출력
        String keyHash= Utility.INSTANCE.getKeyHash(this);
        Log.i("KeyHash", keyHash);

        civ= findViewById(R.id.civ);
        tvNickname= findViewById(R.id.tv_nickname);
        tvEmail= findViewById(R.id.tv_email);

    }

    public void clickLogin(View view) {
        //카카오 계정으로 로그인
        UserApiClient.getInstance().loginWithKakaoAccount(LoginActivity.this, new Function2<OAuthToken, Throwable, Unit>() {
            @Override
            public Unit invoke(OAuthToken oAuthToken, Throwable throwable) {

                if(oAuthToken !=null ) { //로그인 정보객체가 있다면
                    Toast.makeText(LoginActivity.this, "로그인 성공", Toast.LENGTH_SHORT).show();

                    //로그인 한 계정 정보 얻어오기
                    UserApiClient.getInstance().me(new Function2<User, Throwable, Unit>() {
                        @Override
                        public Unit invoke(User user, Throwable throwable) {
                            if(user!=null){
                                //동의와 상관없이 받을 수 있는 값
                                G.id= user.getId(); //카카오 회원번호

                                //로그인정보를 여러 액티비티에서 사용하는 경우가 많음
                                //모든 액티비티에서 자유롭게 로그인정보를 사용하고 싶다면. G클래스의 static이용 [일종의 전역변수]

                                //필수동의 항목의 회원프로필 정보 [닉네임/프로필이미지 url]
                                G.nickname= user.getKakaoAccount().getProfile().getNickname();
                                G.profileUrl= user.getKakaoAccount().getProfile().getProfileImageUrl();

                                //이용 중 동의 항목으로 지정한 email - 동의를 별도로 하지 않으면 빈 문자열 리턴
                                G.email= user.getKakaoAccount().getEmail();

                                //이용 중 동의에 대한 추가 동의 요구작업
                                List<String> scope= new ArrayList<>();
                                if (user.getKakaoAccount().getEmailNeedsAgreement()) scope.add("account_email");

                                if (scope.size()>0){
                                    //새로운 동의 요청 화면 띄우도록
                                    UserApiClient.getInstance().loginWithNewScopes(LoginActivity.this, scope, new Function2<OAuthToken, Throwable, Unit>() {
                                        @Override
                                        public Unit invoke(OAuthToken oAuthToken, Throwable throwable) {

                                            G.email= user.getKakaoAccount().getEmail();
                                            tvEmail.setText(G.email);

                                            return null;
                                        }
                                    });
                                }

                                //화면에 보이기
                                tvNickname.setText(G.nickname);
                                tvEmail.setText(G.email);
                                Glide.with(LoginActivity.this).load(G.profileUrl).into(civ);

                                //원래는 이 정보들을 다음에 앱을 사용할때 또 입력하게 하고 싶지 않다면.
                                //디바이스에 저장하기 위해 SharedPreferences 로 저장함.

                            }

                            return null;
                        }
                    });

                }else{
                    Toast.makeText(LoginActivity.this, "사용자 정보 요청 실패", Toast.LENGTH_SHORT).show();
                }

                return null;
            }
        });
    }

    public void clickNext(View view) {
        startActivity(new Intent(this, MenuActivity.class));
    }

    public void clickLogOut(View view) {
        UserApiClient.getInstance().logout(new Function1<Throwable, Unit>() {
            @Override
            public Unit invoke(Throwable throwable) {
                Toast.makeText(LoginActivity.this, "로그아웃되셨습니다", Toast.LENGTH_SHORT).show();

                //기존정보들 모두 제거
                G.id=-1;
                G.nickname="";
                G.email= null;
                G.profileUrl= "";

                //화면도 모두 제거
                tvNickname.setText("닉네임");
                tvEmail.setText("이메일");
                Glide.with(LoginActivity.this).load(R.mipmap.ic_launcher).into(civ);

                return null;
            }
        });
    }
}