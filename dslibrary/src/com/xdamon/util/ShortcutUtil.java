package com.xdamon.util;

import java.util.List;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.Parcelable;
import android.text.TextUtils;

import com.xdamon.library.R;

/**
 * 创建桌面快捷键工具类
 * 
 * @author wenpei.wei
 * 
 */
public class ShortcutUtil {

    /**
     * 创建桌面快捷键
     * 目前是在第一次登陆选择好城市后创建
     * 
     * @param context
     * @param iconResId
     *            快捷键名称
     * @param appnameResId
     *            图标
     */
    public static void createShortcut(Context context, int iconResId, int appnameResId) {

        Intent shortcutIntent = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");
        shortcutIntent.putExtra("duplicate", false);
        shortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, context.getString(appnameResId));
        Parcelable icon = Intent.ShortcutIconResource.fromContext(context.getApplicationContext(),
            iconResId);
        shortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, icon);
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.setPackage(context.getPackageName());
        shortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, intent);
        context.sendBroadcast(shortcutIntent);
    }

    private static String getAuthorityFromPermission(Context context, String permission) {
        if (TextUtils.isEmpty(permission)) {
            return null;
        }
        List<PackageInfo> packs = context.getPackageManager().getInstalledPackages(
            PackageManager.GET_PROVIDERS);
        if (packs == null) {
            return null;
        }
        for (PackageInfo pack : packs) {
            ProviderInfo[] providers = pack.providers;
            if (providers != null) {
                for (ProviderInfo provider : providers) {
                    if (permission.equals(provider.readPermission)
                            || permission.equals(provider.writePermission)) {
                        return provider.authority;
                    }
                }
            }
        }
        return null;
    }

    /**
     * 判断是否已经创建了快捷键
     * 快捷键信息存储在data/data/ 下的相应数据库中
     * 由于不同系统数据库存储位置不同（MIUI在/data/data/com.miui.home/databases/launcher），
     * 所以需要用getAuthorityFromPermission()方法来查找具体AUTHORITY
     * 然后查看是否有相应快捷键
     * 
     * @param context
     * @return
     */
    public static boolean hasShortcut(Context context) {
        final String AUTHORITY = getAuthorityFromPermission(context.getApplicationContext(),
            "com.android.launcher.permission.READ_SETTINGS");
        Cursor c = null;
        if (TextUtils.isEmpty(AUTHORITY)) {
            return true;
        }
        final ContentResolver cr = context.getContentResolver();
        Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/favorites?notify=true");
        try {
            c = cr.query(CONTENT_URI, new String[] { "title" }, "title=?",
                new String[] { context.getString(R.string.app_name) }, null);
            if (c != null && c.getCount() > 0) {
                return true;
            }
        } catch (Exception e) {
            return true;
        } finally {
            if (c != null) {
                c.close();
            }
        }
        return false;
    }
}
