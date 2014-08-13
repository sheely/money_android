package com.xdamon.util;

/*
 * Copyright (C) 2010 Michael Pardo
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.text.TextUtils;

public class IntentUtils {
	public static void startWebActivity(Context context, String url) {
		final Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setData(Uri.parse(url));
		context.startActivity(intent);
	}

	public static void startEmailActivity(Context context, int toResId, int subjectResId, int bodyResId) {
		startEmailActivity(context, context.getString(toResId), context.getString(subjectResId),
				context.getString(bodyResId));
	}

	public static void startEmailActivity(Context context, String to, String subject, String body) {
		final Intent intent = new Intent(Intent.ACTION_SEND);
		intent.setType("message/rfc822");

		if (!TextUtils.isEmpty(to)) {
			intent.putExtra(Intent.EXTRA_EMAIL, new String[] { to });
		}
		if (!TextUtils.isEmpty(subject)) {
			intent.putExtra(Intent.EXTRA_SUBJECT, subject);
		}
		if (!TextUtils.isEmpty(body)) {
			intent.putExtra(Intent.EXTRA_TEXT, body);
		}

		final PackageManager pm = (PackageManager) context.getPackageManager();
		try {
			if (pm.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY).size() == 0) {
				intent.setType("text/plain");
			}
		}
		catch (Exception e) {
			DSLog.w("Exception encountered while looking for email intent receiver.", e.getLocalizedMessage());
		}

		context.startActivity(intent);
	}
}
