package com.wanlonggroup.caiplus.util;

import java.util.HashMap;

import android.text.TextUtils;

import com.wanlonggroup.caiplus.app.BaseActivity;
import com.wanlonggroup.caiplus.app.CaiPlusApplication;
import com.xdamon.util.PreferencesUtils;

public class ConfigSwitch {

    private static ConfigSwitch _instance;
    private DomainType domainType = DomainType.NONE;
    private HashMap<String, String> domainMap = new HashMap<String, String>();

    private ConfigSwitch() {
        domainType = DomainType.toType(PreferencesUtils.getInt(CaiPlusApplication.instance(),
            "config_domain"));
        // if(Environment.isDebug()){
        // domainType = DomainType.WANDEJUN;
        // }
        swithDomain(domainType);
    }

    public static ConfigSwitch instance() {
        if (_instance == null) {
            _instance = new ConfigSwitch();
        }
        return _instance;
    }

    public void reset() {
        domainMap.clear();
    }

    public DomainType getDomainType() {
        return domainType;
    }

    public String wrapDomain(final String url) {
        if (TextUtils.isEmpty(url)) {
            return url;
        }
        String tmp = url.trim();
        for (String key : domainMap.keySet()) {
            if (url.startsWith(key.trim())) {
                tmp = url.replace(key, domainMap.get(key));
                break;
            }
        }
        return tmp;
    }

    public void swithDomain(DomainType type) {
        reset();
        domainType = type;
        switch (type) {
            case GERMMY:
                domainMap.put(BaseActivity.DEFAULT_API_URL,
                    "http://germmyapp.nat123.net/myStruts1/");
            case WANDEJUN:
                domainMap.put(BaseActivity.DEFAULT_API_URL,
                    "http://112.124.22.156:18080/myStruts1/");
                break;
            case WANLONGTEST:
                domainMap.put(BaseActivity.DEFAULT_API_URL, "http://210.13.70.38:17070/myStruts1/");
                break;
            default:
                break;
        }
        PreferencesUtils.putInt(CaiPlusApplication.instance(), "config_domain", type.getType());
    }

    public enum DomainType {
        NONE(-1), GERMMY(0), WANDEJUN(1), WANLONGTEST(2);

        private int type;

        private DomainType(int type) {
            this.type = type;
        }

        public static DomainType toType(int type) {
            switch (type) {
                case 0:
                    return GERMMY;
                case 1:
                    return WANDEJUN;
                case 2:
                    return WANLONGTEST;
                default:
                    return NONE;
            }
        }

        public int getType() {
            return type;
        }
    }
}
