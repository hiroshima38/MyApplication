//投稿するActivity
package com.example.hiroshima.myapplication;

import android.Manifest;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.kii.cloud.storage.Kii;
import com.kii.cloud.storage.KiiBucket;
import com.kii.cloud.storage.KiiObject;
import com.kii.cloud.storage.callback.KiiObjectCallBack;
import com.kii.cloud.storage.callback.KiiObjectPublishCallback;
import com.kii.cloud.storage.exception.CloudExecutionException;
import com.kii.cloud.storage.resumabletransfer.KiiRTransfer;
import com.kii.cloud.storage.resumabletransfer.KiiRTransferCallback;
import com.kii.cloud.storage.resumabletransfer.KiiUploader;

import java.io.File;
import java.io.FileOutputStream;


public class PostActivity extends ActionBarActivity {
    private static final int IMAGE_CHOOSER_RESULTCODE = 1;
    private String mImagePath = null;
    private KiiObject mKiiImageObject = null;
    private String comment;
    private Uri mImageUri;
    private String mainText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        Button attachBtn = (Button) findViewById(R.id.attach_button);
        attachBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onAttachFileButtonClicked(v);
            }
        });
        Button attachCameraBtn = (Button) findViewById(R.id.attach_camera_button);
        attachCameraBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onAttachCameraFileButtonClicked(v);
            }
        });
        Button postBtn = (Button) findViewById(R.id.post_button);
        postBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPostButtonClicked(v);
            }
        });

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED) {
            String[] permissions = new String[] {
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            };
            ActivityCompat.requestPermissions(this, permissions, 1);
        } else {
            //処理
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) !=
                PackageManager.PERMISSION_GRANTED) {
            String[] permissions = new String[] {
                    Manifest.permission.CAMERA
            };
            ActivityCompat.requestPermissions(this, permissions, 1);
        } else {
            //処理
        }
    }
    //画像の添付ボタンをおした時の処理
    public void onAttachFileButtonClicked(View v) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), IMAGE_CHOOSER_RESULTCODE);
    }
    //カメラの添付ボタンをおした時の処理
    public void onAttachCameraFileButtonClicked(View v) {
        String filename = System.currentTimeMillis() + ".jpg";

        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, filename);
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");

        mImageUri = getContentResolver().insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        Intent intent = new Intent();
        intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageUri);
        startActivityForResult(intent, IMAGE_CHOOSER_RESULTCODE);
    }

    //画像を選択した後に実行されるコールバック関数。
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == IMAGE_CHOOSER_RESULTCODE) {
            //失敗の時
            if (resultCode != RESULT_OK ) {
                return;
            }

            //画像を取得する。Xperia = data.それ以外 = mImageUri
            Uri result;
            if(data != null) {
                result = data.getData();
            }else {
                result = mImageUri;
                Log.d("log:mImageUri:",result.toString());
            }
            ImageView iv = (ImageView) findViewById(R.id.image_view1);
            iv.setImageURI(result);
            mImagePath = getFilePathByUri(result);

        }
    }
    //uriからファイルのパスを取得。KiiCloudのチュートリアル。
    private String getFilePathByUri(Uri selectedFileUri) {
        //4.2以降
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            // Workaround of retrieving file image through ContentResolver
            // for Android4.2 or later
            String filePath = null;
            FileOutputStream fos = null;
            try {
                //ビットマップを取得
                Bitmap bmp = MediaStore.Images.Media.getBitmap(
                        this.getContentResolver(), selectedFileUri);
                String cacheDir = Environment.getExternalStorageDirectory()
                        .getAbsolutePath() + File.separator + "gsapp";
                //ディレクトリ作成
                File createDir = new File(cacheDir);
                if (!createDir.exists()) {
                    createDir.mkdir();
                }
                //一時ファイル名を作成。毎回上書き
                filePath = cacheDir + File.separator + "upload.jpg";
                File file = new File(filePath);
                //
                Matrix varMat = new Matrix();
                varMat.postScale(0.5F, 0.5F);
                Bitmap bmp2 = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), varMat, true);
                //ビットマップをjpgに変換して一時的に保存する。
                fos = new FileOutputStream(file);
                bmp2.compress(Bitmap.CompressFormat.JPEG, 95, fos);
                fos.flush();
                fos.getFD().sync();
            } catch (Exception e) {
                filePath = null;
            } finally {
                if (fos != null) {
                    try {
                        fos.close();
                    } catch (Exception e) {
                        // Nothing to do
                    }
                }
            }
            return filePath;
        } else {
            //データから探す
            String[] filePathColumn = { MediaStore.MediaColumns.DATA };
            Cursor cursor = this.getContentResolver().query(
                    selectedFileUri, filePathColumn, null, null, null);

            if (cursor == null)
                return null;
            try {
                if (!cursor.moveToFirst())
                    return null;
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                if (columnIndex < 0) {
                    return null;
                }
                String picturePath = cursor.getString(columnIndex);
                return picturePath;
            } finally {
                cursor.close();
            }
        }
    }


    //投稿ボタンを御した時の処理
    public void onPostButtonClicked(View v) {
        EditText mMainTextField = (EditText) (findViewById(R.id.mainText_field));
        mainText = mMainTextField.getText().toString();
        EditText mCommentField = (EditText) (findViewById(R.id.comment_field));
        comment = mCommentField.getText().toString();
        if (comment.equals("")) {
            showAlert(getString(R.string.no_data_message));
            return;
        }
        //画像をUPしてからmessagesに投稿。
        if (mImagePath != null) {
            showDialog();
            uploadFile(mImagePath);
        }else {
            postMessages(null);
        }
    }
    //投稿処理。
    public void postMessages(String url) {
        KiiBucket bucket = Kii.bucket("messages");
        KiiObject object = bucket.object();
        object.set("comment", comment);
        object.set("mainText", mainText);
        //画像があるときだけセット
        if(url != null) {
            object.set("imageUrl", url);
        }
        //データをKiiCloudに保存
        object.save(new KiiObjectCallBack() {
            @Override
            public void onSaveCompleted(int token, KiiObject object, Exception exception) {
                if (exception == null) {
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
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
    //画像をKiiCloudのimagesにUP。
    private void uploadFile(String path) {
        KiiBucket bucket = Kii.bucket("images");
        KiiObject object = bucket.object();
        mKiiImageObject = object;
        File f = new File(path);
        KiiUploader uploader = object.uploader(this, f);
        uploader.transferAsync(new KiiRTransferCallback() {
            @Override
            public void onTransferCompleted(KiiRTransfer operator, Exception e) {
                if (e == null) {
                    mKiiImageObject.refresh(new KiiObjectCallBack() {
                        public void onRefreshCompleted(int token, KiiObject object, Exception e) {
                            if (e == null) {
                                object.publishBody(new KiiObjectPublishCallback() {
                                    @Override
                                    public void onPublishCompleted(String url, KiiObject kiiObject, Exception e) {
                                        Log.d("mogiurl", url);
                                        postMessages(url);
                                    }
                                });
                            }
                        }
                    });


                } else {
                    Throwable cause = e.getCause();
                    if (cause instanceof CloudExecutionException)
                        showAlert(Util
                                .generateAlertMessage((CloudExecutionException) cause));
                    else
                        showAlert(e.getLocalizedMessage());
                }
            }
        });
    }
    //エラーダイアログを表示する
    void showAlert(String message) {
        DialogFragment newFragment = AlertDialogFragment.newInstance(R.string.operation_failed, message, null);
        newFragment.show(getFragmentManager(), "dialog");
    }

    //アップロード中のプログレス
    private static ProgressDialog mProgressDialog;
    private void showDialog() {
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("アップロード中...");
        mProgressDialog.setIndeterminate(false);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_post, menu);
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
