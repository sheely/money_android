package com.wanlonggroup.caiplus.bz;

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
		cxlistReq.getTaskArgs().put("statusWithMe",99);
		cxlistReq.getTaskArgs().put("oppoType", "");
		cxlistReq.getTaskArgs().put("bossName", "");
		cxlistReq.getTaskArgs().put("oppoTitle", "");
		cxlistReq.start();
		return cxlistReq;
	}

	@Override
	CXListAdapter createAdapter() {
		return new CXListAdapter();
	}

}
