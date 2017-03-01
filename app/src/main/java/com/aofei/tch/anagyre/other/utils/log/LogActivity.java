package com.aofei.tch.anagyre.other.utils.log;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.aofei.tch.anagyre.R;

import java.io.File;

public class LogActivity extends AppCompatActivity {
    private static final String TAG = "LogActivity";
    public boolean LogOn;//Log开关

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log);
        // 获取到控件
        ToggleButton mTogBtn = (ToggleButton) findViewById(R.id.mLogBtn);
        TextView mSendToEmail = (TextView) findViewById(R.id.mSendToEmail);
        final EditText mT = (EditText) findViewById(R.id.mEditText);
        Button mSendBtn = (Button) findViewById(R.id.mSendBtn);

        //检查权限
        LogUtils.d(TAG, "check right = " + ContextCompat.checkSelfPermission(LogActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE));
        mTogBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
                // TODO Auto-generated method stub
                if (isChecked) {
                    //选中
                    LogUtils.d(TAG, "Set Log On");
                    LogOn = true;
                    LogCatch.getInstance(LogActivity.this).stop();
                    LogCatch.getInstance(LogActivity.this).start();
                } else {
                    //未选中
                    LogUtils.d(TAG, "Set Log Off");
                    LogOn = false;
                    LogCatch.getInstance(LogActivity.this).stop();
                }
            }
        });
            mSendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (LogCatch.getInstance(LogActivity.this) != null) {
                    LogCatch.getInstance(LogActivity.this).stop();
                    LogOn = false;
                }
                LogUtils.d(TAG, "Send out");
                sendEmail(mT.getText().toString());
            }
        });
    }

    public void sendEmail(String adress) {
        Intent email = new Intent(Intent.ACTION_SEND);
        // 附件
        File file = new File(LogCatch.getInstance(LogActivity.this).GetFileInfo());
        LogUtils.d(TAG, "附件文件为：" + LogCatch.getInstance(LogActivity.this).GetFileInfo());
        //邮件发送类型：带附件的邮件
        email.setType("application/octet-stream");
        //邮件接收者（数组，可以是多位接收者）
        LogUtils.d(TAG, "adress = " + adress);
        String[] emailReciver = new String[]{adress};

        String emailTitle = "tuoluoGame LOG日志";
        //String emailContent = "内容";
        //设置邮件地址
        email.putExtra(Intent.EXTRA_EMAIL, emailReciver);
        //设置邮件标题
        email.putExtra(Intent.EXTRA_SUBJECT, emailTitle);
        //附件
        email.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
        //调用系统的邮件系统
        startActivity(Intent.createChooser(email, "请选择邮件发送软件"));
    }

}
