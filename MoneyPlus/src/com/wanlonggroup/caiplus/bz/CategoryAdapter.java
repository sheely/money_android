package com.wanlonggroup.caiplus.bz;

import java.util.ArrayList;

import android.content.Context;
import android.widget.ArrayAdapter;

import com.xdamon.app.DSObject;
import com.xdamon.util.Collection2Utils;

public class CategoryAdapter extends ArrayAdapter<String> {
    ArrayList<DSObject> dsCates;

    public CategoryAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
        dsCates = new ArrayList<DSObject>();
        dsCates.add(new DSObject("Category").put("key", "").put("value", "全部"));
    }

    public void append(ArrayList<DSObject> data) {
        if (!Collection2Utils.isEmpty(data)) {
            dsCates.clear();
            dsCates.add(new DSObject("Category").put("key", "").put("value", "全部"));
            dsCates.addAll(data);
            notifyDataSetChanged();
        }
    }

    @Override
    public int getCount() {
        if (dsCates != null) {
            return dsCates.size();
        }
        return 0;
    }

    @Override
    public String getItem(int position) {
        return dsCates.get(position).getString("value");
    }

    public String getKey(int position) {
        return dsCates.get(position).getString("key");
    }
}
