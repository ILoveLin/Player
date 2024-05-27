package com.company.shenzhou.mineui.activity;

import com.company.shenzhou.R;
import com.company.shenzhou.app.AppActivity;
import com.joanzapata.pdfview.PDFView;

/**
 * company: 江西神州医疗设备有限公司
 * author : LoveLin
 * time   : 2024/5/27 11:50
 * desc   : 操作手册
 */
public final class How2UseActivity extends AppActivity {

    private PDFView mPDFView;

    @Override
    protected int getLayoutId() {
        return R.layout.howuse_activity;
    }

    @Override
    protected void initView() {
        mPDFView = findViewById(R.id.pdfview);
        mPDFView.fromAsset("how2use.pdf")
                .defaultPage(1)
                .showMinimap(false)
                .enableSwipe(true)
                .load();
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != mPDFView) {
            mPDFView = null;
        }
    }
}