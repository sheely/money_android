package com.wanlonggroup.caiplus.bz;

import android.text.TextUtils;

import com.next.net.SHPostTaskM;

/**
 * 全部财信列表
 * 
 * @author damon
 * 
 */
public class AllCxListFragment extends CxListFragment {

	SHPostTaskM createQueryTask() {
		SHPostTaskM cxlistReq = getTask(DEFAULT_API_URL + "miQueryOppoList.do", this);
		cxlistReq.getTaskArgs().put("statusWithMe", 99);
		cxlistReq.getTaskArgs().put("oppoType", TextUtils.isEmpty(oppoType) ? "" : oppoType);
		cxlistReq.getTaskArgs().put("bossName", TextUtils.isEmpty(bossName) ? "" : bossName);
		cxlistReq.getTaskArgs().put("oppoTitle", TextUtils.isEmpty(oppoTitle) ? "" : oppoTitle);
		cxlistReq.start();
		return cxlistReq;
	}

	@Override
	CXListAdapter createAdapter() {
		return new CXListAdapter();
	}

}
