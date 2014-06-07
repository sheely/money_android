package com.next.util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SHFuck {

	public static String getString(String key, JSONObject dic, JSONObject ext) {
		try {
			JSONObject meta = ext.getJSONObject(key);
			Object value;

			value = dic.get(key);

			if (meta.getString("type").equalsIgnoreCase("selection")) {
				JSONArray array = meta.getJSONArray("selection");
				for (int i = 0; i < array.length(); i++) {
					JSONArray ary = (JSONArray) array.get(i);
					if (((String)ary.get(0)).equalsIgnoreCase((String) value)) {
						return (String)ary.get(1);
					}
				}

			}
			// else if([[meta valueForKey:@"type"]isEqualToString:@"many2one"]){
			// if([[value class]isSubclassOfClass:[NSArray class]]){
			// return [((NSArray *)value) objectAtIndex:1 ];
			// }
			// }
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}
}
