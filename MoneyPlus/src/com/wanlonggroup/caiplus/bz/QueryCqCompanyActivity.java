package com.wanlonggroup.caiplus.bz;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.next.net.SHPostTaskM;
import com.next.net.SHTask;
import com.wanlonggroup.caiplus.R;
import com.wanlonggroup.caiplus.app.BaseActivity;
import com.wanlonggroup.caiplus.model.CPModeName;
import com.xdamon.app.DSObject;
import com.xdamon.util.DSObjectFactory;

public class QueryCqCompanyActivity extends BaseActivity implements OnClickListener {
    Spinner cateSpinner;
    EditText companyNameTextView;
    Button queryButton;
    CategoryAdapter cateAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.query_cq);

        cateSpinner = (Spinner) findViewById(R.id.category);
        companyNameTextView = (EditText) findViewById(R.id.company_name);
        queryButton = (Button) findViewById(R.id.query_btn);
        queryButton.setOnClickListener(this);

        cateAdapter = new CategoryAdapter(this, android.R.layout.simple_spinner_item);
        cateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        cateSpinner.setAdapter(cateAdapter);

        queryCate();
    }

    SHPostTaskM queryCateTask;

    void queryCate() {
        queryCateTask = getTask(DEFAULT_API_URL + "queryCompanyInit.do", this);
        queryCateTask.start();
        showProgressDialog();
    }

    @Override
    public void onTaskFinished(SHTask task) throws Exception {
        dismissProgressDialog();
        if (task == queryCateTask) {
            DSObject dsobj = DSObjectFactory.create(CPModeName.COMPANY_CATEGORY_LIST).fromJson(
                task.getResult());
            cateAdapter.append(dsobj.getList(CPModeName.COMPANY_CATEGORY_LIST,
                CPModeName.COMPANY_CATEGORY_ITEM));
        }
    }

    @Override
    public void onClick(View v) {
        if (v == queryButton) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("cp://cqcompanylist"));
            intent.putExtra("companycategorykey",
                cateAdapter.getKey(cateSpinner.getSelectedItemPosition()));
            intent.putExtra("companyname", companyNameTextView.getText().toString());
            startActivity(intent);
        }
    }
}
