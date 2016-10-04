//ブラウザを表示するアクティビティーです
package com.example.hiroshima.myapplication;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;


public class WebActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);

        Intent intent = getIntent();
        String url  = intent.getStringExtra("url");

        WebView webView = (WebView) findViewById(R.id.webView1);
        Log.d("get myurl", url);
        webView.setWebViewClient(new WebViewClient());
        webView.loadUrl(url);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        WebView webView = (WebView) findViewById(R.id.webView1);
        if(event.getAction() == KeyEvent.ACTION_DOWN
                && keyCode == KeyEvent.KEYCODE_BACK
                && webView.canGoBack() == true) {
            System.out.println("event.getAction():" + event.getAction());
            System.out.println("keyCode:" + keyCode);
            ReturnPage();
            return true;
        }else if (event.getAction() == KeyEvent.ACTION_DOWN
                && keyCode == KeyEvent.KEYCODE_DEL) {
            System.out.println("event.getAction():" + event.getAction());
            System.out.println("keyCode:" + keyCode);
            return true;
        }else {
            Finish();
            return true;
        }
    }
    private void ReturnPage() {
        WebView webView = (WebView) findViewById(R.id.webView1);
        webView.goBack();
    }
    private void Finish() {
        finish();
    }






    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_web, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.web_action_settings) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
