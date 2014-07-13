package com.wanlonggroup.caiplus.bz;

import java.io.File;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.damon.ds.app.DSObject;
import com.damon.ds.download.impl.DefaultDownloadTask;
import com.damon.ds.download.intf.DownloadListener;
import com.damon.ds.download.intf.DownloadTask;
import com.damon.ds.util.AndroidUtils;
import com.damon.ds.util.DSObjectFactory;
import com.damon.ds.widget.BasicSingleItem;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.next.net.SHPostTaskM;
import com.next.net.SHTask;
import com.wanlonggroup.caiplus.R;
import com.wanlonggroup.caiplus.adapter.BasicDSAdapter;
import com.wanlonggroup.caiplus.app.BasePtrListActivity;
import com.wanlonggroup.caiplus.model.CPModeName;
import com.wanlonggroup.caiplus.util.Utils;

public class AttachmentListActivity extends BasePtrListActivity {

	String oppoId;
	String attachType;
	DSObject dsAttachments;
	Adapter adapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		oppoId = getStringParam("oppoid");
		attachType = getStringParam("attachtype");

		listView.setMode(Mode.DISABLED);
		adapter = new Adapter();
		listView.setAdapter(adapter);
	}

	SHPostTaskM queryTask;

	void queryAttachments() {
		queryTask = getTask(DEFAULT_API_URL + "queryAttachment.do", this);
		queryTask.getTaskArgs().put("oppoId", oppoId);
		queryTask.getTaskArgs().put("attachmentType", attachType);
		queryTask.start();
	}

	@Override
	public void onTaskFinished(SHTask task) throws Exception {
		dismissProgressDialog();
		dsAttachments = DSObjectFactory.create(CPModeName.CAIXIN_LIST).fromJson(task.getResult());
		adapter.appendList(dsAttachments.getArray(CPModeName.CAIXIN_LIST, CPModeName.CAIXIN_ATTACH_ITEM));
	}

	@Override
	public void onTaskFailed(SHTask task) {
		dismissProgressDialog();
		adapter.appendList(null, task.getRespInfo().getMessage());
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Object obj = (DSObject) parent.getItemAtPosition(position);
		if (Utils.isDSObject(obj, CPModeName.CAIXIN_ATTACH_ITEM)) {
			DSObject dsObject = (DSObject) obj;
			downloadPdf(dsObject);
		}
	}

	DefaultDownloadTask task;

	void downloadPdf(DSObject attach) {
		File file = getDownloadFile(attach);
		if(file != null){
			openPdf(file);
		}else{
			if (task != null) {
				task.cancel(true);
				dismissProgressDialog();
			}
			task = new DefaultDownloadTask(this, AndroidUtils.getCacheDirectory(this), PdfDownloadListener);
			task.start(attach.getString("attachmentUrl"));
			showProgressDialog();
		}
	}

	void openPdf(File pdfFile) {
		AndroidUtils.checkmod("777", pdfFile.getAbsolutePath());
		
		Intent intent = new Intent();
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setAction(android.content.Intent.ACTION_VIEW);
		String type = "application/pdf";
		intent.setDataAndType(Uri.fromFile(pdfFile), type);
		startActivity(Intent.createChooser(intent, "选择程序"));
	}

	File getDownloadFile(DSObject attach) {
		String url = attach.getString("attachmentUrl");
		if (!TextUtils.isEmpty(url)) {
			int index = url.lastIndexOf("/");
			if (index > 0) {
				url = url.substring(index + 1);
			}
		}
		File file = new File(AndroidUtils.getCacheDirectory(this), url);
		if (file.exists() && !file.isDirectory()) {
			return file;
		}
		return null;
	}

	private final DownloadListener PdfDownloadListener = new DownloadListener() {

		@Override
		public void onStart(DownloadTask task) {

		}

		@Override
		public void onDoing(DownloadTask task, int progress) {

		}

		@Override
		public void onPause(DownloadTask task, String message) {

		}

		@Override
		public void onResume(DownloadTask task) {

		}

		@Override
		public void onSuccess(DownloadTask task) {
			dismissProgressDialog();
			

			Intent intent = new Intent();
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.setAction(android.content.Intent.ACTION_VIEW);
			String type = "application/pdf";
			intent.setDataAndType(Uri.fromFile(task.downloadFile()), type);
			startActivity(Intent.createChooser(intent, "选择程序"));
		}

		@Override
		public void onFailed(DownloadTask task, String message) {

		}

		@Override
		public void onCancel(DownloadTask task) {

		}

	};

	class Adapter extends BasicDSAdapter {

		public String getAttachName(int position) {
			DSObject attach = (DSObject) getItem(position);
			String url = attach.getString("attachmentUrl");
			if (!TextUtils.isEmpty(url)) {
				int index = url.lastIndexOf("/");
				if (index > 0) {
					return url.substring(index + 1);
				}
			}
			return url;
		}

		@Override
		public View getCPItemView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.common_basic_single_item,
					parent, false);
			}
			BasicSingleItem item = (BasicSingleItem) convertView;
			item.setTitle(getAttachName(position));
			return convertView;
		}

		@Override
		public void loadNextData(int startIndex) {
			queryAttachments();
		}

	}

}
