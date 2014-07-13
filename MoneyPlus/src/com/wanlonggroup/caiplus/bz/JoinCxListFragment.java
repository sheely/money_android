package com.wanlonggroup.caiplus.bz;

import com.next.net.SHPostTaskM;

/**
 * 参与的财信
 * @author damon
 *
 */
public class JoinCxListFragment extends CxListFragment {
	
	SHPostTaskM createQueryTask() {
		SHPostTaskM cxlistReq = getTask(DEFAULT_API_URL + "miQueryOppoList.do", this);
		cxlistReq.getTaskArgs().put("statusWithMe", 3);
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
