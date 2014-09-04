package com.wanlonggroup.caiplus.bz;

import android.os.Bundle;
import android.text.TextUtils;
import android.webkit.WebView;

import com.next.net.SHPostTaskM;
import com.next.net.SHTask;
import com.wanlonggroup.caiplus.R;
import com.wanlonggroup.caiplus.app.BaseActivity;
import com.xdamon.app.DSObject;
import com.xdamon.util.DSObjectFactory;

public class SkillDetailActivity extends BaseActivity {

    WebView webView;
    int itemType;
    String itemId;
    String itemTitle;
    DSObject dsSkillDetail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.skill_detail);

        webView = (WebView) findViewById(R.id.webview);

        itemType = getIntParam("itemtype");
        itemId = getStringParam("itemid");
        itemTitle = getStringParam("itemtitle");
        if (!TextUtils.isEmpty(itemTitle)) {
            setTitle(itemTitle);
        }

        updateView();
        queryDetail();
    }

    void updateView() {
        if (dsSkillDetail == null) {
            return;
        }
        webView.loadDataWithBaseURL(null, dsSkillDetail.getString("itemContent"), "text/html",
            "utf-8", null);
    }

    SHPostTaskM detailTask;

    void queryDetail() {
        detailTask = getTask(DEFAULT_API_URL + "miQuerySkillCaseDetail.do", this);
        detailTask.getTaskArgs().put("itemType", itemType);
        detailTask.getTaskArgs().put("itemId", itemId);
        detailTask.start();
        showProgressDialog();
    }

    @Override
    public void onTaskFinished(SHTask task) throws Exception {
        dismissProgressDialog();
        dsSkillDetail = DSObjectFactory.create("SkillDetail").fromJson(task.getResult());
        updateView();
    }

}
