package com.ls.uploadpic;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.lidroid.xutils.util.LogUtils;
import com.ls.utils.Constants;
import com.ls.utils.HttpHandlerUtils;
import com.ls.utils.JSONUtils;
import com.ls.utils.Message;
import com.ls.utils.Utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements OnClickListener {

    public static final boolean DEBUG = true;
    public static final String TAG = "MainActivity";

    private ImageView[] pics = new ImageView[5];
    private int[] picId = new int[]{R.id.pic1, R.id.pic2, R.id.pic3, R.id.pic4, R.id.pic5};
    private boolean[] isUse = new boolean[5];

    private Button mHandlePic;
    private Button mGetResult;
    private ImageView mCamera;
    private ImageView mAddPic;
    private ImageView mSendPic;
    private ImageView mClearPic;
    private ListView mListView;

    private Uri mBitmapUri;
    private List<File> mPicsFileList = new ArrayList<>();
    private List<Message> mMessageList = new ArrayList<>();


    private ProgressDialog xh_pDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();

    }

    private void initView() {
        for (int i = 0; i < pics.length; i++) {
            pics[i] = (ImageView) findViewById(picId[i]);
            isUse[i] = false;
        }

        mHandlePic = (Button) findViewById(R.id.handle_pic);
        mGetResult = (Button) findViewById(R.id.get_result);
        mCamera = (ImageView) findViewById(R.id.camera);
        mAddPic = (ImageView) findViewById(R.id.add_pic);
        mSendPic = (ImageView) findViewById(R.id.send_pic);
        mClearPic = (ImageView) findViewById(R.id.clear_pic);
        mListView = (ListView) findViewById(R.id.listview);

        mHandlePic.setOnClickListener(this);
        mGetResult.setOnClickListener(this);
        mCamera.setOnClickListener(this);
        mAddPic.setOnClickListener(this);
        mSendPic.setOnClickListener(this);
        mClearPic.setOnClickListener(this);

        xh_pDialog = new ProgressDialog(this);
        xh_pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        xh_pDialog.setCancelable(false);
        xh_pDialog.setTitle("注意");
        xh_pDialog.setMessage("正在上传图片,请稍候...");
        xh_pDialog.setIcon(R.mipmap.ic_send);
        xh_pDialog.setIndeterminate(false);
        xh_pDialog.setCancelable(true);
        xh_pDialog.setButton("取消上传", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                xh_pDialog.dismiss();
            }
        });
    }

    @Override
    public void onClick(View v) {

        if (v == mHandlePic) {//处理图片
            handlePic();
        } else if (v == mGetResult) {//获取处理结果
            getResult();

        } else if (v == mCamera) {//拍照
            startCamera();
        } else if (v == mAddPic) {//添加照片
            startFileManager();
        } else if (v == mSendPic) {//发送
            uploadPic();
        } else if (v == mClearPic) {//清除
            clearPic();
        }
    }

    private void getResult() {
        HttpHandlerUtils utils = new HttpHandlerUtils();
        utils.setHttpStateListener(new HttpHandlerUtils.HttpStateListener() {
            @Override
            public void fail(String loginState) {

            }

            @Override
            public void success(String refreshState) {
                if (DEBUG) {
                    Log.e(TAG, " 成功:" + refreshState);
                }
                mMessageList = JSONUtils.parseList(refreshState, Message.class);
                mListView.setAdapter(new ArrayAdapter<>(MainActivity.this,android.R.layout.simple_list_item_1,mMessageList));
            }
        });
        utils.httpGet(Constants.GET_RESULT + Utils.getIMIE(MainActivity.this.getApplication()));
    }

    private void handlePic() {
        HttpHandlerUtils utils = new HttpHandlerUtils();
        utils.httpGet(Constants.HANDLE);
    }

    private void clearPic() {
        for (int i = 0; i < pics.length; i++) {
            pics[i] = (ImageView) findViewById(picId[i]);
            pics[i].setImageResource(R.mipmap.ic_plus);
            isUse[i] = false;
        }
        mPicsFileList.clear();
    }

    private void uploadPic() {
        xh_pDialog.show();
        HttpHandlerUtils utils = new HttpHandlerUtils();
        utils.setHttpStateListener(new HttpHandlerUtils.HttpStateListener() {
            @Override
            public void fail(String loginState) {
                xh_pDialog.dismiss();
                Toast.makeText(MainActivity.this.getApplicationContext(), "上传失败!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void success(String refreshState) {
                if (DEBUG) {
                    Log.e(TAG, " 上传成功!");
                }
                xh_pDialog.dismiss();
                Toast.makeText(MainActivity.this.getApplicationContext(), "上传成功!", Toast.LENGTH_SHORT).show();
                clearPic();
            }
        });
        utils.upLoad(Constants.UPLOAD, mPicsFileList);
    }

    public void startFileManager() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        try {
            startActivityForResult(Intent.createChooser(intent, "请选择一个要上传的图片"),
                    Constants.GET_ATTACH_PHOTO_REQUEST_CODE);
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(this, "请安装文件管理器", Toast.LENGTH_SHORT)
                    .show();
        }
    }

    public void startCamera() {
        File file = Utils.getAlbumStorageDir("test " + System.currentTimeMillis() + ".jpg");
        mBitmapUri = Uri.fromFile(file);
        Intent localIntent1 = new Intent();
        localIntent1.setAction("android.media.action.IMAGE_CAPTURE");
        localIntent1.putExtra(MediaStore.EXTRA_OUTPUT, mBitmapUri); //指定图片输出地址
        startActivityForResult(localIntent1, Constants.GET_CAMERA_PHOTO_REQUEST_CODE);
    }


    /**
     * 根据返回选择的文件，来进行上传操作 *
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case Constants.GET_ATTACH_PHOTO_REQUEST_CODE:
                    getAttachment(data.getData());
                    break;
                case Constants.GET_CAMERA_PHOTO_REQUEST_CODE:
                    getPhoto();
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void getAttachment(Uri mUri) {
        if (DEBUG) {
            Log.e(TAG, "---  getAttachment --- ");
        }
        if (!Utils.isPic(getApplicationContext(), mUri))
            return;
        Bitmap image;
        try {
            image = Utils.getBitmap(mUri, this, 500);
            if (image != null) {
                refershPicsView(image);
                File mPhotoAttachment = Utils.saveBitmapToFile(Utils.getIMIE(getApplicationContext()) + "+" + System.currentTimeMillis() + "." + Utils.getPicType(getApplicationContext(), mUri), image);
                mPicsFileList.add(mPhotoAttachment);
                //HttpHandlerUtils.uploadFile(file.getAbsolutePath(),"http://113.251.216.145:8080/Lianluoquan/Login");
                //HttpHandlerUtils.upLoad("http://113.250.156.102:8080/Lianluoquan/Login", file);
                if (DEBUG) {
                    Log.e(TAG, "pic name is : " + mPhotoAttachment.getPath());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    private void getPhoto() {
        String imageName = Utils.getIMIE(getApplicationContext()) + "+" + System.currentTimeMillis() + ".jpg";//图片命名
        if (mBitmapUri != null) {
            Bitmap image;
            try {
                image = Utils.getBitmap(mBitmapUri, this, 500);
                if (image != null) {
                    refershPicsView(image);
                    File mPhotoAttachment = Utils.saveBitmapToFile(imageName, image);
                    mPicsFileList.add(mPhotoAttachment);
                    //HttpHandlerUtils.uploadFile(file.getAbsolutePath(),"http://113.251.216.145:8080/Lianluoquan/Login");
                    //HttpHandlerUtils.upLoad("http://113.250.156.102:8080/Lianluoquan/Login", file);
                    if (DEBUG) {
                        Log.e(TAG, "pic name is : " + mPhotoAttachment.getName());
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void refershPicsView(Bitmap image) {
        for (int i = 0; i < pics.length; i++) {
            if (!isUse[i]) {
                pics[i].setImageBitmap(image);
                isUse[i] = true;
                break;
            }
        }
    }


    class ListAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return 0;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            return null;
        }
    }

}
