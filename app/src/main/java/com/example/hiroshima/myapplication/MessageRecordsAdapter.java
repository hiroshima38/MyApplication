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
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.kii.cloud.storage.KiiObject;
import com.kii.cloud.storage.callback.KiiObjectCallBack;

import java.util.List;

public class MessageRecordsAdapter extends ArrayAdapter<MessageRecord> {
    private ImageLoader mImageLoader;


    public MessageRecordsAdapter(Context context) {
        super(context, R.layout.message_item);
        mImageLoader = new ImageLoader(VolleyApplication.getInstance().getRequestQueue(), new BitmapLruCache());
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if(convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.message_item, parent, false);
        }

        NetworkImageView imageView = (NetworkImageView) convertView.findViewById(R.id.image1);
        TextView textView = (TextView) convertView.findViewById(R.id.text2);
        TextView textView2 = (TextView) convertView.findViewById(R.id.text1);

        final MessageRecord messageRecord = getItem(position);

        //詳細画面への遷移
        LinearLayout cell = (LinearLayout)convertView.findViewById(R.id.cell);
        cell.setOnClickListener(new ViewGroup.OnClickListener(){
            @Override
            public void onClick(View view) {
                LinearLayout cell = (LinearLayout) view;
                Intent intent = new Intent(view.getContext(), DetailActivity.class);
                intent.putExtra("comment", messageRecord.getComment());
                intent.putExtra("image_url", messageRecord.getImageUrl());
                intent.putExtra("mainText", messageRecord.getMainText());
                intent.putExtra("id", messageRecord.getId());
                view.getContext().startActivity(intent);
            }
        });





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

        Button buttonView1 = (Button) convertView.findViewById(R.id.button1);
        buttonView1.setText(getContext().getString(R.string.good)+":"+imageRecord.getGoodCount());
        Button buttonView2 = (Button) convertView.findViewById(R.id.button2);
        buttonView2.setText(getContext().getString(R.string.veryGood)+":"+imageRecord.getVeryGoodCount());

        //いいねボタン機能
        buttonView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Button buttonView1 = (Button) view;
                MessageRecord messageRecord = getItem(position);
                Uri objUri = Uri.parse("kiicloud://buckets/" + "messages" + "/objects/" + messageRecord.getId());
                KiiObject object = KiiObject.createByUri(objUri);
                object.set("goodCount", messageRecord.getGoodCount() + 1);
                object.save(new KiiObjectCallBack() {
                    @Override
                    public void onSaveCompleted(int token, KiiObject object, Exception exception) {
                        if (exception != null) {
                            return;
                        }
                        MessageRecord messageRecord = getItem(position);
                        messageRecord.setGoodCount(messageRecord.getGoodCount() + 1);
                        notifyDataSetChanged();
                        Toast.makeText(getContext(), getContext().getString(R.string.good_done), Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });

        //超いいねボタン機能
        buttonView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Button buttonView2 = (Button) view;
                MessageRecord messageRecord = getItem(position);
                Uri objUri = Uri.parse("kiicloud://buckets/" + "messages" + "/objects/" + messageRecord.getId());
                KiiObject object = KiiObject.createByUri(objUri);
                object.set("veryGoodCount", messageRecord.getVeryGoodCount() + 1);
                object.save(new KiiObjectCallBack() {
                    @Override
                    public void onSaveCompleted(int token, KiiObject object, Exception exception) {
                        if (exception != null) {
                            return;
                        }
                        MessageRecord messageRecord = getItem(position);
                        messageRecord.setVeryGoodCount(messageRecord.getVeryGoodCount() + 1);
                        notifyDataSetChanged();
                        Toast.makeText(getContext(), getContext().getString(R.string.veryGood_done), Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });


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
