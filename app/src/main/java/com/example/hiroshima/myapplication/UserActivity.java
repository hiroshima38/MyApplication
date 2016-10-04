package com.example.hiroshima.myapplication;

import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.kii.cloud.storage.KiiUser;
import com.kii.cloud.storage.callback.KiiUserCallBack;
import com.kii.cloud.storage.exception.CloudExecutionException;


public class UserActivity extends ActionBarActivity {
    private EditText mUsernameField;
    private EditText mPasswordField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences pref = getSharedPreferences(getString(R.string.save_data_name), Context.MODE_PRIVATE);
        String token = pref.getString(getString(R.string.save_token), "");
        if(token == "") {
            CreateMyView(savedInstanceState);
        }else {
            try {
                KiiUser.loginWithToken(callback, token);
            } catch (Exception e) {
                showAlert(R.string.operation_failed, e.getLocalizedMessage(), null);
                CreateMyView(savedInstanceState);
            }
        }

    }
    //Viewを作る。
    protected void CreateMyView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_user);
        mUsernameField = (EditText) findViewById(R.id.username_field);
        mPasswordField = (EditText) findViewById(R.id.password_field);
        mPasswordField.setTransformationMethod(new PasswordTransformationMethod());
        mPasswordField.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        //登録ボタン
        Button signupBtn = (Button) findViewById(R.id.signup_button);
        //ログインボタン
        Button loginBtn = (Button) findViewById(R.id.login_button);
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onLoginButtonClicked(v);
            }
        });
        //登録ボタンをクリックした時の処理を設定（NewUserActivityに飛ぶ）
        signupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), NewUserActivity.class);
                startActivity(intent);
            }
        });
    }
    //ログイン処理
    public void onLoginButtonClicked(View v) {
        InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        String username = mUsernameField.getText().toString();
        String password = mPasswordField.getText().toString();
        try {
            //KiiCloudのログイン処理。完了すると結果がcallback関数として実行される。
            KiiUser.logIn(callback, username, password);
        } catch (Exception e) {
            showAlert(R.string.operation_failed, e.getLocalizedMessage(), null);
        }
    }
    //ダイアログを表示する
    void showAlert(int titleId, String message, AlertDialogFragment.AlertDialogListener listener ) {
        DialogFragment newFragment = AlertDialogFragment.newInstance(titleId, message, listener);
        newFragment.show(getFragmentManager(), "dialog");
    }

    //新規登録、ログインの時に呼び出されるコールバック関数
    KiiUserCallBack callback = new KiiUserCallBack() {
        @Override
        public void onLoginCompleted(int token, KiiUser user, Exception e) {
            if (e == null) {
                SharedPreferences pref = getSharedPreferences(getString(R.string.save_data_name), Context.MODE_PRIVATE);
                pref.edit().putString(getString(R.string.save_token), user.getAccessToken()).apply();

                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            } else {
                if (e instanceof CloudExecutionException)
                    showAlert(R.string.operation_failed, Util.generateAlertMessage((CloudExecutionException) e), null);
                else
                    showAlert(R.string.operation_failed, e.getLocalizedMessage(), null);
            }
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_user, menu);
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
