# Project 지금여기 (HowPlace)

이 앱은 안드로이드 디바이스의 주변의 장소들의 정보를 각종 Api와 웹크롤링을 통해 정보를 제공해주는 앱 입니다.

# 사용법

이 앱은 각종 Api정보에 기반하여 정보를 찾기 때문에 각종 ApiKey가 필요하고

Api를 포함하는 파일을 넣어주어야 컴파일 에러를 피할 수 있습니다.

사용 Api는 {구글 PlaceAPI, GoogleMapsSDK, 네이버 OpenAPI 검색, 서울시 OpenAPI 실시간 지하철 정보} 입니다.

## keys.c
### (./app/src/main/jni/keys.c)
```
#include <jni.h>

jstring web_api_key = "{YourGoogle ApiKey(Permission: PlaceAPI)}";
jstring naver_client_id = "{네이버 OpenAPI ClientID (Permission: 검색)}";
jstring naver_client_secret = "{네이버 OpenAPI ClientSecret (Permission: 검색)}";
jstring subway_key = "{서울시 OpenApi APIKey(Permission: 실시간 지하철 정보)}"; 

JNIEXPORT jstring JNICALL
Java_com_alkemic_howplace_Define_getWebApiKey(JNIEnv *env, jclass type) {
    return (*env)->NewStringUTF(env, web_api_key);
}


JNIEXPORT jstring JNICALL
Java_com_alkemic_howplace_Define_getNaverClientID(JNIEnv *env, jclass type) {
    return (*env)->NewStringUTF(env, naver_client_id);
}

JNIEXPORT jstring JNICALL
Java_com_alkemic_howplace_Define_getNaverClientSecret(JNIEnv *env, jclass type) {

    return (*env)->NewStringUTF(env, naver_client_secret);
}

JNIEXPORT jstring JNICALL
Java_com_alkemic_howplace_Define_getSubwayKey(JNIEnv *env, jclass type) {

    return (*env)->NewStringUTF(env, subway_key);
}

```

## google_maps_api.xml
### (./app/src/main/res/values/google_maps_api.xml)
```
<resources>
    <string name="google_maps_key" templateMergeStrategy="preserve" translatable="false">
    {YourGoogleApiKey(Permission: MapsSDKforAndroid)}
    </string>
</resources>
```

# OpenSource Licence Announcement

이 프로그램은 오픈소스 라이브러리를 사용하였습니다.
좋은 라이브러리를 제공해준 분들께 감사합니다!

tbruyelle/RxPermissions (https://github.com/tbruyelle/RxPermissions)

JakeWharton/RxBinding (https://github.com/JakeWharton/RxBinding)

square/okhttp (https://github.com/square/okhttp)

jhy/jsoup (https://github.com/jhy/jsoup/)
