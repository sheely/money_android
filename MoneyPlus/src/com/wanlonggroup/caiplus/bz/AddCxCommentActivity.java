package com.wanlonggroup.caiplus.bz;

import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;

import com.next.net.SHPostTaskM;
import com.next.net.SHTask;
import com.wanlonggroup.caiplus.R;
import com.wanlonggroup.caiplus.app.BaseActivity;
import com.xdamon.app.DSActionBar;
import com.xdamon.app.DSObject;

public class AddCxCommentActivity extends BaseActivity {

	DSObject dsCaixin;
	int commentType;
	String leaveComment;
	EditText contentEditText;

	protected void onCreate(android.os.Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_cx_comment);
		contentEditText = (EditText) findViewById(R.id.content);

		dsCaixin = getIntent().getParcelableExtra("caixin");
		if (dsCaixin == null) {
			finish();
			return;
		}
		leaveComment = getStringParam("leavecomment");
		contentEditText.setText(leaveComment);
		
		commentType = getIntParam("commentertype", -1);
		if (commentType == 2) {
			setTitle("执行人评论");
		} else if (commentType == 1) {
			setTitle("承接人评论");
		}
	};

	SHPostTaskM addCommentTask;

	void addComment() {
		leaveComment = contentEditText.getText().toString();
		if(TextUtils.isEmpty(leaveComment)){
			showAlert("请输入评论");
			return;
		}
		addCommentTask = getTask(DEFAULT_API_URL + "commentAdd.do", this);
		addCommentTask.getTaskArgs().put("oppoId", dsCaixin.getString("oppoId"));
		addCommentTask.getTaskArgs().put("commenterType", commentType);
		addCommentTask.getTaskArgs().put("leaveComment", leaveComment);
		addCommentTask.start();
		showProgressDialog();
	}
	
	@Override
	public void onTaskFinished(SHTask task) throws Exception {
		dismissProgressDialog();
		addCommentTask = null;
		showShortToast("评论成功");
		setResult(RESULT_OK);
		finish();
	}
	
	@Override
	public void onCreateActionBar(DSActionBar actionBar) {
		actionBar.addAction("提交", "submit_comment", new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				addComment();
			}
		});
	}

}
