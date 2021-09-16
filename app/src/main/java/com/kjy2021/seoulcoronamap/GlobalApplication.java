package com.kjy2021.seoulcoronamap;

import android.app.Application;

import com.kakao.sdk.common.KakaoSdk;

public class GlobalApplication extends Application { @Override
public void onCreate() {
    super.onCreate();

    //카카오 SDK 초기화
    KakaoSdk.init(this, "cb007eb158199e2cf27899360b9419bc");//네이티브앱키
}
}


