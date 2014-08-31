package com.wanlonggroup.caiplus.bz.im;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.xdamon.app.DSObject;
import com.xdamon.executor.ThreadExecutorsHelper;
import com.xdamon.io.DSStreamReader;
import com.xdamon.io.DSStreamWriter;
import com.xdamon.util.ACache;
import com.xdamon.util.Collection2Utils;
import com.xdamon.util.DSLog;

public class ChatHelper {

    public final String FILE_NAME = "chat_list";

    public final String KEY_NAME = "abFUJ970er78dfds";

    public final Context mContext;

    private static ChatHelper _instance;

    private ACache aCache;

    private ChatHelper(Context context) {
        mContext = context;
        aCache = ACache.get(context, FILE_NAME);
    }

    public static ChatHelper instance(Context context) {
        if (_instance == null) {
            _instance = new ChatHelper(context);
        }
        return _instance;
    }

    public void chat2Who(final Context context, DSObject dscaiyou) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("cp://chat"));
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("caiyou", dscaiyou);
        context.startActivity(intent);
    }

    public void saveChatingList(final ArrayList<DSObject> dsMsgList) {

        Runnable runnable = new Runnable() {

            @Override
            public void run() {
                synchronized (aCache) {

                    if (Collection2Utils.isEmpty(dsMsgList)) {
                        return;
                    }
                    DSStreamWriter writer = new DSStreamWriter();
                    byte[] buffer;
                    try {
                        writer.writeInt(dsMsgList.size());
                        for (DSObject msg : dsMsgList) {
                            buffer = msg.toByteArray();
                            writer.writeByte(buffer);
                        }
                        aCache.put(KEY_NAME, writer.toByteArray());
                    } catch (Exception e) {
                        DSLog.e("chathelp", e.getLocalizedMessage());
                    } finally {
                        try {
                            writer.close();
                        } catch (Exception ex) {
                            DSLog.e("chathelp", ex.getLocalizedMessage());
                        }
                    }
                }
            }
        };

        ThreadExecutorsHelper.execute(runnable);
    }

    public ArrayList<DSObject> getChatingList() {
        byte[] buffer = aCache.getAsBinary(KEY_NAME);
        if (buffer == null || buffer.length == 0) {
            return null;
        }

        ArrayList<DSObject> dsMsgList = new ArrayList<DSObject>();
        DSStreamReader reader = new DSStreamReader(buffer);
        try {
            int len = reader.readInt();
            for (int i = 0; i < len; i++) {
                byte[] bytes = reader.readBytes(reader.readInt());
                dsMsgList.add(new DSObject(bytes));
            }
            return dsMsgList;
        } catch (Exception e) {
            DSLog.e("chathelp", e.getLocalizedMessage());
        } finally {
            try {
                reader.close();
            } catch (Exception ex) {
                DSLog.e("chathelp", ex.getLocalizedMessage());
            }
        }
        return null;
    }

}
