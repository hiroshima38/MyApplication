package com.example.hiroshima.myapplication;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.kii.cloud.storage.KiiUser;
import com.kii.cloud.storage.exception.app.AppException;

import java.io.IOException;


public class SettingActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        //usernameの取得
        //passwordの取得

        //新しいパスワードのEditText
//        EditText newpassword = (EditText) findViewById(R.id.password_new);
//
//        try {
//            KiiUser user = KiiUser.logIn(username, password);
//            user.changePassword(newpassword, password);
//        } catch (IOException e) {
//            // Password change failed for some reasons
//            // Please check IOExecption to see what went wrong...
//        } catch (AppException e) {
//            // Password change failed for some reasons
//            // Please check AppException to see what went wrong...
//        }
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_setting, menu);
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