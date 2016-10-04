//ListViewに１つのセルの情報(message_item.xmlとMessageRecord)を結びつけるためのクラス
package com.example.hiroshima.myapplication;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.Spannable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import java.util.List;

public class MessageRecordsAdapter extends ArrayAdapter<MessageRecord> {
    private ImageLoader mImageLoader;


    public MessageRecordsAdapter(Context context) {
        super(context, R.layout.message_item);
        mImageLoader = new ImageLoader(VolleyApplication.getInstance().getRequestQueue(), new BitmapLruCache());
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.message_item, parent, false);
        }

        NetworkImageView imageView = (NetworkImageView) convertView.findViewById(R.id.image1);
        TextView textView = (TextView) convertView.findViewById(R.id.text2);
        TextView textView2 = (TextView) convertView.findViewById(R.id.text1);

        //詳細画面への遷移






        //webリンクを制御するプログラム
        textView.setOnTouchListener(new ViewGroup.OnTouchListener() {
            @Override
            public boolean onTouch(final View view, MotionEvent event) {
                TextView textView = (TextView) view;
                MutableLinkMovementMethod m = new MutableLinkMovementMethod();
                m.setOnUrlClickListener(new MutableLinkMovementMethod.OnUrlClickListener() {
                    public void onUrlClick(TextView v, Uri uri) {
                        Log.d("myurl", uri.toString());
                        Intent intent = new Intent(view.getContext(), WebActivity.class);

                        intent.putExtra("url", uri.toString());
                        view.getContext().startActivity(intent);

                    }
                });
                textView.setMovementMethod(m);
                boolean mt = m.onTouchEvent(textView, (Spannable) textView.getText(), event);
                textView.setMovementMethod(null);
                textView.setFocusable(false);
                return mt;
            }
        });





        MessageRecord imageRecord = getItem(position);
        imageView.setImageUrl(imageRecord.getImageUrl(), mImageLoader);
        textView.setText(imageRecord.getMainText());
        textView2.setText(imageRecord.getComment());
        return convertView;
    }
    public void setMessageRecords(List<MessageRecord> objects) {
        clear();
        for(MessageRecord object : objects) {
            add(object);
        }
        notifyDataSetChanged();
    }
}
