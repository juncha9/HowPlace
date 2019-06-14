# Project 지금여기 (HowPlace)

이 앱은 안드로이드 디바이스의 주변의 장소들의 정보를 각종 Api와 웹크롤링을 통해 정보를 제공해주는 앱 입니다.

# 사용법

이 앱은 각종 Api정보에 기반하여 정보를 찾기 때문에 각종 ApiKey가 필요하고

Api를 포함하는 파일을 넣어주어야 컴파일 에러를 피할 수 있습니다.

# keys.c
### (./app/src/main/jni/keys.c)
```
#include <jni.h>

jstring web_api_key = "{YourGoogleApiKey(Include: PlaceAPI)}";
jstring app_api_key = "{YourGoogleApiKey(Include: MapsSDKforAndroid, PlaceAPI)}";
jstring naver_client_id = "{네이버 OpenAPI ClientID (사용API: 검색)}";
jstring naver_client_secret = "{네이버 OpenAPI ClientSecret (사용API: 검색)}";
jstring subway_key = "{서울시 OpenApi APIKey}"; 

JNIEXPORT jstring JNICALL
Java_com_alkemic_howplace_Define_getWebApiKey(JNIEnv *env, jclass type) {
    return (*env)->NewStringUTF(env, web_api_key);
}

JNIEXPORT jstring JNICALL
Java_com_alkemic_howplace_Define_getAppApiKey(JNIEnv *env, jclass type) {
    return (*env)->NewStringUTF(env, app_api_key);
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

# res
### (./app/src/main/res/values/google_maps_api.xml)
```
<resources>
    <string name="google_maps_key" templateMergeStrategy="preserve" translatable="false">
    {YourGoogleApiKey(Include: MapsSDKforAndroid)}
    </string>
</resources>
```

# google-services.json
### (./app/google-services.json)
```
파이어베이스 json
(파이어베이스 홈페이지에서 발급)
```


