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

public class QueryCyActivity extends BaseActivity implements OnClickListener {

    Spinner cateSpinner, companySpinner, addressSpinner;
    EditText nameText;
    Button queryButton;
    CategoryAdapter cateAdapter, companyAdapter, addressAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.query_cy);

        cateSpinner = (Spinner) findViewById(R.id.category);
        companySpinner = (Spinner) findViewById(R.id.company);
        nameText = (EditText) findViewById(R.id.name);
        addressSpinner = (Spinner) findViewById(R.id.address);

        queryButton = (Button) findViewById(R.id.query_btn);
        queryButton.setOnClickListener(this);
        queryButton.setEnabled(false);

        cateAdapter = new CategoryAdapter(this, android.R.layout.simple_spinner_item);
        cateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        cateSpinner.setAdapter(cateAdapter);

        companyAdapter = new CategoryAdapter(this, android.R.layout.simple_spinner_item);
        companyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        companySpinner.setAdapter(companyAdapter);

        addressAdapter = new CategoryAdapter(this, android.R.layout.simple_spinner_item);
        addressAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        addressSpinner.setAdapter(addressAdapter);

        initQuery();
    }

    SHPostTaskM initQueryTask;

    void initQuery() {
        initQueryTask = getTask(DEFAULT_API_URL + "miQueryFriendInit.do", this);
        initQueryTask.start();
        showProgressDialog();
    }

    @Override
    public void onTaskFinished(SHTask task) throws Exception {
        dismissProgressDialog();
        if (task == initQueryTask) {
            DSObject dsobj = DSObjectFactory.create("miQueryFriendInit").fromJson(task.getResult());
            cateAdapter.append(dsobj.getList(CPModeName.CAIXIN_TYPE_LIST,
                CPModeName.CAIXIN_TYPE_ITEM));

            companyAdapter.append(dsobj.getList(CPModeName.CQ_COMPANY_LIST,
                CPModeName.CQ_COMPANY_ITEM));
            companySpinner.setAdapter(companyAdapter);

            addressAdapter.append(dsobj.getList("address", "address"));
            addressSpinner.setAdapter(addressAdapter);

            queryButton.setEnabled(true);
        }
    }

    @Override
    public void onClick(View v) {
        if (v == queryButton) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("cp://cylist"));
            intent.putExtra("oppotype", cateAdapter.getKey(cateSpinner.getSelectedItemPosition()));
            intent.putExtra("companyid",
                companyAdapter.getKey(cateSpinner.getSelectedItemPosition()));
            intent.putExtra("friendname", nameText.getText().toString());
            intent.putExtra("address",
                addressAdapter.getKey(addressSpinner.getSelectedItemPosition()));
            intent.putExtra("forresult", getBooleanParam("forresult"));
            startActivityForResult(intent, 1);
        }
    }

    @Override
    protected void onActivityResult(int arg0, int arg1, Intent arg2) {
        if (arg1 != RESULT_OK && getBooleanParam("forresult")) {
            return;
        }
        if (arg0 == 1) {
            setResult(RESULT_OK, arg2);
            finish();
        }
    }
}
