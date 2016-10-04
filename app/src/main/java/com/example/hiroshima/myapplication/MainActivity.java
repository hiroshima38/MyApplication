package com.example.hiroshima.myapplication;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.kii.cloud.storage.Kii;
import com.kii.cloud.storage.KiiObject;
import com.kii.cloud.storage.KiiUser;
import com.kii.cloud.storage.callback.KiiQueryCallBack;
import com.kii.cloud.storage.query.KiiQuery;
import com.kii.cloud.storage.query.KiiQueryResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends ActionBarActivity {

    private MessageRecordsAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //KiiCloudでのログイン状態を取得。
        KiiUser user = KiiUser.getCurrentUser();
        //自動ログインのため保存されているaccess tokenを読み出す。
        SharedPreferences pref = getSharedPreferences(getString(R.string.save_data_name), Context.MODE_PRIVATE);
        String token = pref.getString(getString(R.string.save_token), "");
        //ログインしていない時はログインのactivityに遷移
        if(user == null || token == "") {
            Intent intent = new Intent(getApplicationContext(), UserActivity.class);
            startActivity(intent);
            finish();
        }

        //メイン画面のレイアウトをセット。ListView
        setContentView(R.layout.activity_main);



        mAdapter = new MessageRecordsAdapter(this);
        ListView listView = (ListView) findViewById(R.id.mylist);
        listView.setAdapter(mAdapter);
     }
    //KiiCLoud対応のfetch。
    //一覧のデータを作成して表示。
    private void fetch() {
        //KiiCloudの検索条件を作成。全件。
        KiiQuery query = new KiiQuery();
        //ソート条件を設定。日付の降順
        query.sortByDesc("_created");
        //バケットmessagesを検索する。最大200件
        Kii.bucket("messages")
                .query(new KiiQueryCallBack<KiiObject>() {
                    @Override
                    public void onQueryCompleted(int token, KiiQueryResult<KiiObject> result, Exception exception) {
                        if (exception != null) {
                            return;
                        }
                        ArrayList<MessageRecord> records = new ArrayList<MessageRecord>();
                        List<KiiObject> objLists = result.getResult();
                        for (KiiObject obj : objLists) {
                            String id = obj.getString("_id", "");
                            String title = obj.getString("comment", "");
                            String url = obj.getString("imageUrl", "");
                            String mainText = obj.getString("mainText", "");

                            MessageRecord record = new MessageRecord(id, url, title, mainText);
                            records.add(record);
                        }
                        mAdapter.setMessageRecords(records);
                    }
                }, query);

    }
    @Override
    protected void onStart() {
        super.onStart();
        fetch();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(getApplicationContext(), SettingActivity.class);
            startActivity(intent);
            return true;
        }

        //ログアウト処理.AccesTokenを削除して対応。
        if (id == R.id.log_out) {
            //自動ログインのため保存されているaccess tokenを消す。
            SharedPreferences pref = getSharedPreferences(getString(R.string.save_data_name), Context.MODE_PRIVATE);
            pref.edit().clear().apply();
            Intent intent = new Intent(getApplicationContext(), UserActivity.class);
            startActivity(intent);
            finish();
            return true;
        }
        //投稿処理
        if (id == R.id.post) {
            Intent intent = new Intent(getApplicationContext(), PostActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
