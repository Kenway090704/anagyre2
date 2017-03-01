package com.aofei.tch.anagyre.login.ui;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.aofei.tch.anagyre.R;
import com.aofei.tch.anagyre.main.ui.HomeActivity;
import com.aofei.tch.anagyre.other.ui.BaseFragment;
import com.aofei.tch.anagyre.other.utils.RegexValidateUtil;
import com.aofei.tch.anagyre.other.utils.log.LogActivity;
import com.aofei.tch.anagyre.other.utils.log.LogCatch;
import com.aofei.tch.anagyre.other.utils.log.LogUtils;

import java.io.File;

/**
 * Created by kenway on 16/11/18 10:51
 * Email : xiaokai090704@126.com
 */

public class LoginFragment extends BaseFragment {
    private  static  final  String TAG="LoginFragment";

    EditText mEditText;
    @Override
    protected int getLayoutId() {
        return R.layout.fragment_login;
    }

    @Override
    protected void initViews(final View root) {

        Button btn = (Button) root.findViewById(R.id.btn_fragment_login_emial);
                mEditText= (EditText) root.findViewById(R.id.mEditText);
           mEditText.setText("254903810@qq.com");
      //跳转到Log界面
        btn.setOnClickListener(new Button.OnClickListener() {//创建监听
            public void onClick(View v) {
//                Intent intent = new Intent(getActivity(), LogActivity.class);
//                startActivity(intent);
                if (mEditText.getText().toString().equals("")){
                    Toast.makeText(getActivity(),"请输入开发人员邮箱",Toast.LENGTH_SHORT).show();
                }else {
                    boolean isEmail= RegexValidateUtil.checkEmail(mEditText.getText().toString());

                    LogUtils.e(TAG,"isEmail="+isEmail);
                    if (isEmail){
                        sendEmail(mEditText.getText().toString());
                    }else {
                        Toast.makeText(getActivity(),"输入的邮箱不正确",Toast.LENGTH_SHORT).show();
                    }
                }

            }

        });
    }

    @Override
    protected void initEnvent() {

    }

    @Override
    protected void initData() {

    }

    /**
     * 将app在运行过程中出现的问题log日志发送到开发人员
     * @param adress  开发人员 邮箱地址
     */
    public void sendEmail(String adress) {
        Intent email = new Intent(Intent.ACTION_SEND);
        // 附件
        File file = new File(LogCatch.getInstance(getContext()).GetFileInfo());
        LogUtils.d(TAG, "附件文件为：" + LogCatch.getInstance(getContext()).GetFileInfo());
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
