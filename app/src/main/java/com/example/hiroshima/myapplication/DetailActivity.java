package com.example.hiroshima.myapplication;

import android.app.DialogFragment;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.kii.cloud.storage.Kii;
import com.kii.cloud.storage.KiiBucket;
import com.kii.cloud.storage.KiiObject;
import com.kii.cloud.storage.callback.KiiObjectCallBack;
import com.kii.cloud.storage.exception.CloudExecutionException;


public class DetailActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        ImageLoader mImageLoader = new ImageLoader(VolleyApplication.getInstance().getRequestQueue(), new BitmapLruCache());
        Intent intent = getIntent();

        final String id = intent.getStringExtra("id");
        final String comment = intent.getStringExtra("comment");
        final String imageUrl  = intent.getStringExtra("image_url");
        final String mainText = intent.getStringExtra("mainText");

        TextView textViewComment1 = (TextView) findViewById(R.id.comment1);
        TextView textViewMainText1 = (TextView) findViewById(R.id.mainText1);
        TextView textViewUrl1 = (TextView) findViewById(R.id.url1);
        NetworkImageView imageView = (NetworkImageView) findViewById(R.id.image_view1);

        textViewComment1.setText(comment);
        textViewMainText1.setText(mainText);
        textViewUrl1.setText(imageUrl);
        final Uri uri = Uri.parse(imageUrl);

        if(imageUrl.length() != 0){
            imageView.setImageUrl(imageUrl, mImageLoader);

            SpannableString spanSiteUrl = new SpannableString(imageUrl);
            spanSiteUrl.setSpan(new ClickableSpan() {
                @Override
                public void onClick(View textView) {
                    Intent intent = new Intent(textView.getContext(), WebActivity.class);
                    intent.putExtra("url", uri.toString());
                    textView.getContext().startActivity(intent);
                }
            }, 0, imageUrl.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
            textViewUrl1.setText(spanSiteUrl);
            textViewUrl1.setMovementMethod(LinkMovementMethod.getInstance());
        }

        Button btn = (Button)findViewById(R.id.button1);
        btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        //編集ボタン
        Button editBtn = (Button) findViewById(R.id.button2);
        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), EditActivity.class);
                intent.putExtra("comment", comment);
                intent.putExtra("image_url", imageUrl);
                intent.putExtra("mainText", mainText);
                intent.putExtra("id", id);
                startActivity(intent);
                finish();
            }
        });

        //削除ボタン
        Button deleteBtn = (Button) findViewById(R.id.button3);
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                KiiBucket bucket = Kii.bucket("messages");
                KiiObject object = bucket.object(id);//new
                object.delete(new KiiObjectCallBack() {
                    @Override
                    public void onDeleteCompleted(int token, Exception exception) {
                        if (exception == null) {
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            Toast.makeText(getApplicationContext(), "削除しました", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            if (exception instanceof CloudExecutionException)
                                showAlert(Util.generateAlertMessage((CloudExecutionException) exception));
                            else
                                showAlert(exception.getLocalizedMessage());
                        }
                    }
                });
            }
        });
    }
    void showAlert(String message) {
        DialogFragment newFragment = AlertDialogFragment.newInstance(R.string.operation_failed, message, null);
        newFragment.show(getFragmentManager(), "dialog");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_detail, menu);
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
