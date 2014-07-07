package com.wanlonggroup.caiplus.cx;

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
		cxlistReq.start();
		return cxlistReq;
	}

}
