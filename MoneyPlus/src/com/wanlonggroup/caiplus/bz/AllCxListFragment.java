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
		cxlistReq.getTaskArgs().put("statusWithMe", 0);
		cxlistReq.getTaskArgs().put("oppoType", "");
		cxlistReq.start();
		return cxlistReq;
	}

}
