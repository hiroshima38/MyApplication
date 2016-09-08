//起動時に実行されるアクティビティーです。１つの画面に１つのアクティビティーが必要です。
//どのアクティビティーが起動時に実行されるのかはAndroidManifestに記述されています。
package com.example.hiroshima.myapplication;


import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends Activity {

    //アダプタークラスです。
    private MessageRecordsAdapter mAdapter;

    //起動時にOSから実行される関数です。
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //メイン画面のレイアウトをセットしています。ListView
        setContentView(R.layout.activity_main);



        //アダプターを作成します。newでクラスをインスタンス化しています。
        mAdapter = new MessageRecordsAdapter(this);
        //ListViewのViewを取得
        ListView listView = (ListView) findViewById(R.id.mylist);
        //ListViewにアダプターをセット。
        listView.setAdapter(mAdapter);
        //一覧のデータを作成して表示します。



        final Button button = (Button) findViewById(R.id.button1);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "リストを表示します", Toast.LENGTH_SHORT).show();
                button.setVisibility(View.GONE);
                fetch();
            }
        });
     }




    //自分で作った関数です。一覧のデータを作成して表示します。
    private void fetch() {
        //jsonデータをサーバーから取得する通信機能です。Volleyの機能です。通信クラスのインスタンスを作成しているだけです。通信はまだしていません。
        JsonObjectRequest request = new JsonObjectRequest(
                "https://googledrive.com/host/0B3o5-E3LmlWkT2VhYlBZWGQ1b2s" ,//jsonデータが有るサーバーのURLを指定します。
                null,
                //サーバー通信した結果、成功した時の処理をするクラスを作成しています。
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        //try catchでエラーを処理します。tryが必要かどうかはtryに記述している関数次第です。
                        try {
                            //jsonデータを下記で定義したparse関数を使いデータクラスにセットしています。
                            List<MessageRecord> messageRecords = parse(jsonObject);
                            //データをアダプターにセットしています。
                            mAdapter.setMessageRecords(messageRecords);
                        }
                        catch(JSONException e) {
                            //トーストを表示
                            Toast.makeText(getApplicationContext(), "Unable to parse data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                //通信結果、エラーの時の処理クラスを作成。
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        //トーストを表示
                        Toast.makeText(getApplicationContext(), "Unable to fetch data: " + volleyError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
        //作成した通信クラスをキュー、待ち行列にいれて適当なタイミングで通信します。
        //VolleyApplicationはnewしていません。これはAndroidManifestで記載しているので起動時に自動的にnewされています。
        VolleyApplication.getInstance().getRequestQueue().add(request);
    }
    //サーバにあるjsonデータをMessageRecordに変換します。
    private List<MessageRecord> parse(JSONObject json) throws JSONException {
        //空のMessageRecordデータの配列を作成
        ArrayList<MessageRecord> records = new ArrayList<MessageRecord>();
        //jsonデータのmessagesにあるJson配列を取得します。
        JSONArray jsonMessages = json.getJSONArray("messages");
        //配列の数だけ繰り返します。
        for(int i =0; i < jsonMessages.length(); i++) {
            //１つだけ取り出します。
            JSONObject jsonMessage = jsonMessages.getJSONObject(i);
            //jsonの値を取得します。
            String title = jsonMessage.getString("comment");
            String url = jsonMessage.getString("imageUrl");
            //jsonMessageを新しく作ります。
            MessageRecord record = new MessageRecord(url, title);
            //MessageRecordの配列に追加します。
            records.add(record);
        }

        return records;
    }
}
