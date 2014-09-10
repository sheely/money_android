package com.wanlonggroup.caiplus.app;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.wanlonggroup.caiplus.R;
import com.wanlonggroup.caiplus.util.ConfigSwitch;
import com.wanlonggroup.caiplus.util.ConfigSwitch.DomainType;
import com.xdamon.util.CrashReportHelper;

public class DebugActivity extends com.xdamon.app.DebugActivity implements OnClickListener {

    private Button domainBtn;

    private Button crashBtn;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        domainBtn = (Button) findViewById(R.id.domain);
        domainBtn.setOnClickListener(this);

        crashBtn = (Button) findViewById(R.id.crash);
        crashBtn.setOnClickListener(this);

        findViewById(R.id.open).setOnClickListener(this);

        updateView();
    }

    @Override
    public void onSetContentView() {
        setContentView(R.layout.debug_panel);
    }

    private void updateView() {
        domainBtn.setText(ConfigSwitch.instance().getDomainType().toString());
    }

    @Override
    public void onClick(View v) {
        if (domainBtn == v) {
            final String[] items = new String[] { "Germmy", "WANDEJUN", "WANLONGTEST", "None" };
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                    android.R.layout.select_dialog_item, items);
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("选择服务器环境");
            builder.setAdapter(adapter, new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ConfigSwitch.instance().swithDomain(DomainType.toType(which));
                    updateView();
                }

            });
            builder.show();
        } else if (v.getId() == R.id.open) {
            String text = ((EditText) findViewById(R.id.open_url)).getText().toString();
            try {
                if (TextUtils.isEmpty(text)) {
                    return;
                }
                if (!text.startsWith("cp://")) {
                    text = "cp://" + text;
                }
                startActivity(text);
            } catch (Exception e) {
                showShortToast("can not open " + text);
            }
        } else if (v.getId() == R.id.crash) {
            String report = CrashReportHelper.getReportBak();
            if (TextUtils.isEmpty(report)) {
                Toast.makeText(this, "没有崩溃报告", Toast.LENGTH_SHORT).show();
                ;
            } else {
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("message/rfc822");
                i.putExtra(Intent.EXTRA_EMAIL, new String[] { "xxx@163.com" });
                i.putExtra(Intent.EXTRA_SUBJECT, "Crash Report");
                i.putExtra(Intent.EXTRA_TEXT, report);
                startActivity(Intent.createChooser(i, "Select email application"));
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return false;
    }

}
