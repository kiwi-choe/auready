package com.kiwi.auready.taskheads;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.kiwi.auready.R;

/**
 * Created by kiwi on 6/26/17.
 */

public class CustomDialog extends Dialog {

    private TextView mContentView;
    private Button mOkButton;

    private View.OnClickListener mOkClickListener;

    private String mContent;

    public CustomDialog(@NonNull Context context) {
        // Set Dialog background to Transparent
        super(context, android.R.style.Theme_Translucent_NoTitleBar);
    }

    public CustomDialog(Context context, String content, View.OnClickListener singleListener) {
        super(context, android.R.style.Theme_Translucent_NoTitleBar);
        mContent = content;
        mOkClickListener = singleListener;
    }

    @Override
    public void onBackPressed() {
        if(getOwnerActivity() != null) {
            getOwnerActivity().finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        WindowManager.LayoutParams lpWindow = new WindowManager.LayoutParams();
        lpWindow.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        lpWindow.dimAmount = 0.8f;
        getWindow().setAttributes(lpWindow);

        setContentView(R.layout.custom_dialog);

        setLayout();
        setContent(mContent);
        setClickListener(mOkClickListener);
    }

    private void setClickListener(View.OnClickListener okClickListener) {
        if(okClickListener != null) {
            mOkButton.setOnClickListener(okClickListener);
        }
    }

    private void setLayout() {
        mContentView = (TextView) findViewById(R.id.tv_content);
        mOkButton = (Button) findViewById(R.id.bt_ok_dialog);
    }

    public void setContent(String content) {
        mContentView.setText(content);
    }
}
