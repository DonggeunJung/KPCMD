package org.kpcmd.home;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

public class MainActivity extends AppCompatActivity {
    static String URL_HOME = "https://kpcmd.org/";
    // WebView 위젯의 핸들을 저장하는 멤버변수
    WebView mWebView1;
    View videoCustomView;
    FrameLayout customView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(android.os.Build.VERSION.SDK_INT >= 11) {
            getWindow().addFlags(16777216);
        }

        // WebView 위젯의 핸들을 구해서 멤버변수에 저장
        mWebView1 = (WebView)findViewById(R.id.webView1);

        customView = (FrameLayout)findViewById(R.id.customView);

        // 웹브라우저 실행 방지용 웹뷰 클라이언트 지정
        mWebView1.setWebViewClient(new WebClient());
        // 자바스크립트 활성화
        WebSettings webSet = mWebView1.getSettings();
        webSet.setJavaScriptEnabled(true);
        webSet.setPluginState(WebSettings.PluginState.ON);
        webSet.setSupportMultipleWindows(true);

        mWebView1.setWebChromeClient(new CustomWeb());

        // 웹페이지 로딩 타이머 시작
        mTimerLoadPage.sendEmptyMessageDelayed(0, 50);
    }

    class CustomWeb extends WebChromeClient {
        @Override
        public void onShowCustomView(View view, WebChromeClient.CustomViewCallback callback) {
            if (videoCustomView != null) {
                callback.onCustomViewHidden();
                return;
            }
            final FrameLayout frame = ((FrameLayout)view);
            final View v1 = frame.getChildAt(0);

            view.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.MATCH_PARENT, Gravity.CENTER));
            v1.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
                        onHideCustomView();
                        return true;
                    }
                    return false;
                }
            });

            videoCustomView = view;
            customView.setVisibility(View.VISIBLE);
            customView.setBackgroundColor(Color.BLACK);
            customView.bringToFront();
            mWebView1.setVisibility(View.GONE);

            customView.addView(videoCustomView);
        }

        @Override
        public void onHideCustomView() {
            super.onHideCustomView();

            customView.removeView(videoCustomView);
            videoCustomView = null;
            customView.setVisibility(View.INVISIBLE);
            mWebView1.setVisibility(View.VISIBLE);
        }
    }

    // 링크주소 선택했을 때 웹브라우저가 실행되는 것을 방지하기 위한 클래스
    class WebClient extends WebViewClient {
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }

    // 웹페이지 로딩 타이머 이벤트를 위한 핸들러 객체 생성 & 이벤트 함수 재정의
    Handler mTimerLoadPage = new Handler() {
        public void handleMessage(Message msg) {
            // WebView 에 URL 주소를 지정
            mWebView1.loadUrl( URL_HOME );
            mWebView1.setWebViewClient(new WebClient());
        }
    };

    // 하드웨어 키 Up 이벤트 함수
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        switch( keyCode ) {
            // Back 키 일때
            case KeyEvent.KEYCODE_BACK :
                // Home 페이지가 아닐때
                if( mWebView1.canGoBack() ) {
                    // 이전 페이지로 이동
                    mWebView1.goBack();
                }
                // Home 페이지 일때
                else {
                    return super.onKeyUp(keyCode, event);
                }
                break;
        }
        return true;
    }

}
