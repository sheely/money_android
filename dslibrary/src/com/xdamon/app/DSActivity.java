package com.xdamon.app;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager.OnBackStackChangedListener;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.view.Window;
import android.widget.Toast;

import com.xdamon.ext.DSHandler;
import com.xdamon.library.R;
import com.xdamon.util.DialogUtils;
import com.xdamon.util.URLBase64;
import com.xdamon.widget.BeautifulProgressDialog;

public class DSActivity extends FragmentActivity {

    private final Handler mHander = new DSHandler(this) {

        public void handleRealMessage(android.os.Message msg) {
            if (msg.what == 1) {
                Fragment fragment = getSupportFragmentManager().findFragmentById(
                    android.R.id.content);
                if (fragment instanceof DSFragment) {
                    ((DSFragment) fragment).invalidateActionBar();
                } else {
                    invalidateActionBar();
                }
            }
        };

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DSApplication._inner_instance().activityOnCreate(this);

        if (actionBarType() == ActionBarType.NONE) {
            getWindow().requestFeature(Window.FEATURE_NO_TITLE);
            setContentView(new ViewStub(this));
            actionBar = (DSActionBar) LayoutInflater.from(this).inflate(R.layout.ds_action_bar,
                null, false);
        } else if (actionBarType() == ActionBarType.DSACTIONBAR) {
            getWindow().requestFeature(Window.FEATURE_CUSTOM_TITLE);
            setContentView(new ViewStub(this));
            getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.ds_action_bar);

            initActionBar();
        }

        getSupportFragmentManager().addOnBackStackChangedListener(new OnBackStackChangedListener() {

            @Override
            public void onBackStackChanged() {
                mHander.sendEmptyMessageDelayed(1, 300);
            }
        });

        onSetContentView();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mHander.sendEmptyMessageDelayed(1, 300);
    }

    public final void invalidateActionBar() {
        if (actionBar != null) {
            actionBar.removeAllAction();
            onCreateActionBar(actionBar);
        }
    }

    public void onCreateActionBar(DSActionBar actionBar) {

    }

    protected void onSetContentView() {

    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            popFragment(android.R.id.content);
        } else if (canBack()) {
            super.onBackPressed();
        }
    }

    public void addFramgent(Class<?> clss, Bundle args) {
        addFramgent(android.R.id.content, null, clss, args);
    }

    public void addFramgent(String tag, Class<?> clss, Bundle args) {
        addFramgent(android.R.id.content, tag, clss, args);
    }

    public void addFramgent(int containerViewId, String tag, Class<?> clss, Bundle args) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        Fragment fragment = Fragment.instantiate(this, clss.getName(), args);
        transaction.add(containerViewId, fragment, tag);
        transaction.addToBackStack(null);
        transaction.commitAllowingStateLoss();
        getSupportFragmentManager().executePendingTransactions();
    }

    public boolean popFragment() {
        return popFragment(android.R.id.content);
    }

    public boolean popFragment(int containerViewId) {
        Fragment fragment = getSupportFragmentManager().findFragmentById(containerViewId);
        return popFragment(fragment);
    }

    public boolean popFragment(String tag) {
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(tag);
        return popFragment(fragment);
    }

    public boolean popFragment(Fragment fragment) {
        if (fragment instanceof DSFragment) {
            if (!((DSFragment) fragment).canBack()) {
                return false;
            }
        }
        return getSupportFragmentManager().popBackStackImmediate();
    }

    public boolean canBack() {
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        DSApplication._inner_instance().activityOnResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        DSApplication._inner_instance().activityOnPause(this);
    }

    @Override
    protected void onDestroy() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        DSApplication._inner_instance().activityOnDestory(this);
        super.onDestroy();
    }

    public void startActivity(String urlSchema) {
        startActivityForResult(urlSchema, -1);
    }

    public void startActivityForResult(String urlSchema, int requestCode) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(urlSchema));
        startActivityForResult(intent, requestCode);
    }

    // ----actionbar-----

    protected enum ActionBarType {
        DSACTIONBAR, NONE
    }

    private DSActionBar actionBar;

    private boolean isActionBarShowing = actionBarType() != ActionBarType.NONE;

    protected boolean isActionBarShowing() {
        return isActionBarShowing;
    }

    protected ActionBarType actionBarType() {
        return ActionBarType.DSACTIONBAR;
    }

    protected DSActionBar actionBar() {
        return actionBar;
    }

    @Override
    public void setTitle(CharSequence title) {
        super.setTitle(title);
        if (actionBar != null) {
            actionBar.setTitle(title);
        }
    }

    private void initActionBar() {
        actionBar = (DSActionBar) findViewById(R.id.ds_action_bar);
        actionBar.setBackgroundColor(getResources().getColor(R.color.actionbarBackground));
        actionBar.setHomeAsUpResource(R.drawable.ic_navi_back);
        actionBar.setHomeAsUpListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        setTitle(getTitle());
    }

    public void setCustomTitleFeatureInt(int customTitleLayoutId) {
        try {
            // retrieve value for
            // com.android.internal.R.id.title_container(=0x1020149)
            int titleContainerId = (Integer) Class.forName("com.android.internal.R$id").getField(
                "title_container").get(null);

            // remove all views from titleContainer
            ((ViewGroup) getWindow().findViewById(titleContainerId)).removeAllViews();

            // add new custom title view
            getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, customTitleLayoutId);

        } catch (Exception ex) {
            // whatever you want to do here..
        }
    }

    public void hideActionBar() {
        if (!isActionBarShowing) {
            return;
        }
        isActionBarShowing = false;
        if (actionBarType() == ActionBarType.DSACTIONBAR) {
            try {
                // retrieve value for
                // com.android.internal.R.id.title_container(=0x1020149)
                int titleContainerId = (Integer) Class.forName("com.android.internal.R$id").getField(
                    "title_container").get(null);

                // remove all views from titleContainer
                ((ViewGroup) getWindow().findViewById(titleContainerId)).setVisibility(View.GONE);
                ;

            } catch (Exception ex) {
                // whatever you want to do here..
            }
        } else {
            actionBar.hide();
        }
    }

    public void showActionBar() {
        if (isActionBarShowing) {
            return;
        }
        isActionBarShowing = true;
        if (actionBarType() == ActionBarType.DSACTIONBAR) {
            try {
                // retrieve value for
                // com.android.internal.R.id.title_container(=0x1020149)
                int titleContainerId = (Integer) Class.forName("com.android.internal.R$id").getField(
                    "title_container").get(null);

                // remove all views from titleContainer
                ((ViewGroup) getWindow().findViewById(titleContainerId)).setVisibility(View.VISIBLE);
                ;

            } catch (Exception ex) {
                // whatever you want to do here..
            }
        } else {
            actionBar.show();
        }
    }

    // -----toast and dialog----

    public void showShortToast(String message) {
        if (isFinishing()) {
            return;
        }
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private BeautifulProgressDialog progressDialog;
    int progressDialogCount;

    public void showProgressDialog() {
        showProgressDialog("请稍候...");
    }

    public void showProgressDialog(String message) {
        if (isFinishing()) {
            return;
        }
        if (progressDialog == null || !progressDialog.isShowing()) {
            progressDialog = DialogUtils.showProgressDialog(this, message,
                new DialogInterface.OnCancelListener() {

                    @Override
                    public void onCancel(DialogInterface dialog) {
                        _onProgressDialogCancel();

                    }
                }, true);
        } else {
            progressDialog.setMessage(message);
        }
        progressDialogCount++;
    }

    public void dismissProgressDialog() {
        progressDialogCount--;
        if (progressDialogCount < 0) {
            progressDialogCount = 0;
        }
        if (progressDialogCount == 0 && progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    private void _onProgressDialogCancel() {
        progressDialogCount--;
        onProgressDialogCancel();
    }

    public void onProgressDialogCancel() {
    }

    public void showAlert(String message) {
        showAlert("提示", message, false, null, null);
    }

    public void showAlert(String title, String message, boolean hasCancelBtn,
            DialogInterface.OnClickListener lOk, DialogInterface.OnClickListener lCancel) {

        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        };
        if (lOk == null) {
            lOk = listener;
        }
        if (lCancel == null) {
            lCancel = listener;
        }
        if (!hasCancelBtn) {
            DialogUtils.showAlert(this, message, title, "确定", false, lOk);
        } else {
            DialogUtils.showAlert(this, message, title, "确定", "取消", false, lOk, lCancel);
        }
    }

    // ====get parameters

    public int getIntParam(String name, int defaultValue) {
        Intent i = getIntent();
        try {
            Uri uri = i.getData();
            if (uri != null) {
                String val = uri.getQueryParameter(name);
                return Integer.parseInt(val);
            }
        } catch (Exception e) {
        }

        return i.getIntExtra(name, defaultValue);
    }

    public int getIntParam(String name) {
        return getIntParam(name, 0);
    }

    public String getStringParam(String name) {
        return getStringParam(name, null);
    }

    public String getStringParam(String name, String defaultValue) {
        Intent i = getIntent();
        try {
            Uri uri = i.getData();
            if (uri != null) {
                String val = uri.getQueryParameter(name);
                if (val != null)
                    return val;
            }
        } catch (Exception e) {
        }
        if (i.getExtras() != null && i.getExtras().containsKey(name)) {
            return i.getStringExtra(name);
        }
        return defaultValue;
    }

    public boolean getBooleanParam(String name, boolean defaultValue) {
        Intent i = getIntent();
        try {
            Uri uri = i.getData();
            if (uri != null) {
                String val = uri.getQueryParameter(name);
                if (val != null)
                    return "true".equalsIgnoreCase(val) || "1".equals(val);
            }
        } catch (Exception e) {
        }
        return getIntent().getBooleanExtra(name, defaultValue);
    }

    public boolean getBooleanParam(String name) {
        return getBooleanParam(name, false);
    }

    public double getDoubleParam(String name, double defaultValue) {
        Intent i = getIntent();
        try {
            Uri uri = i.getData();
            if (uri != null) {
                String val = uri.getQueryParameter(name);
                return Double.parseDouble(val);
            }
        } catch (Exception e) {
        }

        return i.getDoubleExtra(name, defaultValue);
    }

    public double getDoubleParam(String name) {
        return getDoubleParam(name, 0);
    }

    public DSObject getDSObjectParam(String name) {
        Intent i = getIntent();
        try {
            Uri uri = i.getData();
            if (uri != null) {
                String val = uri.getQueryParameter(name);
                if (val != null) {
                    byte[] bytes = URLBase64.decode(val);
                    return new DSObject(bytes);
                }
            }
        } catch (Exception e) {
        }

        return i.getParcelableExtra(name);
    }

}
