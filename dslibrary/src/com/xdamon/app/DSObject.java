package com.xdamon.app;

import java.io.IOException;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.xdamon.io.DSStreamReader;
import com.xdamon.io.DSStreamWriter;

public class DSObject implements Parcelable {

    public static final String TAG = "DSObject";

    protected JSONObject jsonObj;
    protected String objName;

    public DSObject(String objName) {
        this.objName = objName;
        jsonObj = new JSONObject();
    }

    public DSObject(byte[] bytes) {
        this(bytes, 0, bytes.length);
    }

    public DSObject(byte[] bytes, int offset, int lenght) {
        DSStreamReader dr = new DSStreamReader(bytes, offset, lenght);
        try {
            objName = dr.readString();
            jsonObj = new JSONObject(dr.readString());
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        } finally {
            try {
                dr.close();
            } catch (Exception ex) {

            }
        }
    }

    public static final Parcelable.Creator<DSObject> CREATOR = new Parcelable.Creator<DSObject>() {
        public DSObject createFromParcel(Parcel in) {
            return new DSObject(in);
        }

        public DSObject[] newArray(int size) {
            return new DSObject[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    public boolean isObject(String objName) {
        if (TextUtils.isEmpty(this.objName) || TextUtils.isEmpty(objName)) {
            return false;
        }
        return this.objName.equals(objName);
    }

    public boolean hasKey(String name) {
        return jsonObj.has(name);
    }

    public JSONObject getRootJsonObject() {
        return jsonObj;
    }

    public String getString(String name) {
        return getString(name, null);
    }

    public String getString(String name, String defaultValue) {
        return jsonObj.optString(name, defaultValue);
    }

    public DSObject put(String name, String value) {
        try {
            this.jsonObj.put(name, value);
        } catch (JSONException e) {
            throw new IllegalArgumentException(e);
        }
        return this;
    }

    public int getInt(String name) {
        return getInt(name, 0);
    }

    public int getInt(String name, int defaultValue) {
        return jsonObj.optInt(name, defaultValue);
    }

    public DSObject put(String name, int value) {
        try {
            this.jsonObj.put(name, value);
        } catch (JSONException e) {
            throw new IllegalArgumentException(e);
        }
        return this;
    }

    public long getLong(String name) {
        return getLong(name, 0L);
    }

    public long getLong(String name, long defaultValue) {
        return jsonObj.optLong(name, defaultValue);
    }

    public DSObject put(String name, long value) {
        try {
            this.jsonObj.put(name, value);
        } catch (JSONException e) {
            throw new IllegalArgumentException(e);
        }
        return this;
    }

    public Object getObject(String name) {
        return jsonObj.opt(name);
    }

    public DSObject put(String name, Object value) {
        try {
            this.jsonObj.put(name, value);
        } catch (JSONException e) {
            throw new IllegalArgumentException(e);
        }
        return this;
    }

    public double getDouble(String name) {
        return getDouble(name, Double.NaN);
    }

    public double getDouble(String name, double defaultValue) {
        return jsonObj.optDouble(name, defaultValue);
    }

    public DSObject put(String name, double value) {
        try {
            this.jsonObj.put(name, value);
        } catch (JSONException e) {
            throw new IllegalArgumentException(e);
        }
        return this;
    }

    public boolean getBoolean(String name) {
        return jsonObj.optBoolean(name);
    }

    public DSObject put(String name, boolean value) {
        try {
            this.jsonObj.put(name, value);
        } catch (JSONException e) {
            throw new IllegalArgumentException(e);
        }
        return this;
    }

    public JSONObject getJSONObject(String name) {
        return jsonObj.optJSONObject(name);
    }

    public DSObject put(String name, JSONObject json) {
        try {
            jsonObj.put(name, json);
        } catch (JSONException e) {
            throw new IllegalArgumentException(e);
        }
        return this;
    }

    public JSONArray getJSONArray(String name) {
        return jsonObj.optJSONArray(name);
    }

    public DSObject put(String name, JSONArray arr) {
        try {
            jsonObj.put(name, arr);
        } catch (JSONException e) {
            throw new IllegalArgumentException(e);
        }
        return this;
    }

    public DSObject put(String name, DSObject[] arr) {
        try {
            JSONArray jonsArr = new JSONArray();
            for (DSObject obj : arr) {
                jonsArr.put(obj.jsonObj);
            }
            put(name, jonsArr);
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
        return this;
    }

    public DSObject getDSObject(String name) {
        return getDSObject(name, name);
    }

    public DSObject getDSObject(String name, String objName) {
        if (jsonObj.has(name)) {
            if (TextUtils.isEmpty(objName)) {
                objName = name;
            }
            DSObject cpObject = new DSObject(name);
            return cpObject.fromJson(jsonObj.optJSONObject(name));
        }
        return null;
    }

    public DSObject put(DSObject obj) {
        try {
            if (TextUtils.isEmpty(obj.objName)) {
                throw new IllegalArgumentException("the obj must has ojbname");
            }
            jsonObj.put(obj.objName, obj.jsonObj);
        } catch (JSONException e) {
            throw new IllegalArgumentException(e);
        }
        return this;
    }

    public DSObject[] getArray(String name) {
        return getArray(name, name);
    }

    public DSObject[] getArray(String name, String objName) {
        ArrayList<DSObject> list = getList(name, objName);
        if (list != null) {
            return list.toArray(new DSObject[list.size()]);
        }
        return null;
    }

    public DSObject put(DSObject[] arr) {
        for (DSObject obj : arr) {
            put(obj);
        }
        return this;
    }

    public ArrayList<DSObject> getList(String name) {
        return getList(name, name);
    }

    public ArrayList<DSObject> getList(String name, String objName) {
        while (jsonObj.has(name)) {
            JSONArray array = jsonObj.optJSONArray(name);
            if (array == null) {
                break;
            }
            if (TextUtils.isEmpty(objName)) {
                objName = name;
            }
            int len = array.length();
            ArrayList<DSObject> list = new ArrayList<DSObject>(len);
            for (int i = 0; i < len; i++) {
                try {
                    Object tmp = array.opt(i);
                    if (tmp instanceof JSONObject) {
                        list.add(new DSObject(objName).fromJson((JSONObject) tmp));
                    } else {
                        JSONObject obj = new JSONObject();
                        obj.put(objName, tmp);
                        list.add(new DSObject(objName).fromJson(obj));
                    }
                } catch (Exception e) {
                    list.add(new DSObject(objName).fromJson(array.optJSONObject(i)));

                }
            }
            return list;
        }
        return null;
    }

    public DSObject put(ArrayList<DSObject> list) {
        for (DSObject obj : list) {
            put(obj);
        }
        return this;
    }

    public boolean remove(String name) {
        return this.jsonObj.remove(name) != null;
    }

    public byte[] toByteArray() {
        DSStreamWriter dw = new DSStreamWriter();
        try {
            dw.writeString(objName);
            dw.writeString(jsonObj.toString());
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        } finally {
            try {
                dw.close();
            } catch (Exception ex) {

            }
        }
        return dw.toByteArray();
    }

    private DSObject(Parcel in) {
        this.objName = in.readString();
        String jsonStr = in.readString();
        try {
            jsonObj = new JSONObject(jsonStr);
        } catch (JSONException e) {
            throw new IllegalArgumentException(e);
        }
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        String jsonStr = new JSONObject().toString();
        if (jsonObj != null) {
            jsonStr = jsonObj.toString();
        }
        dest.writeString(objName);
        dest.writeString(jsonStr);
    }

    public DSObject fromJson(JSONObject json) {
        if (json == null) {
            json = new JSONObject();
        }
        this.jsonObj = json;
        return this;
    }

    public DSObject fromJson(Object obj) {
        try {
            if (obj instanceof String) {
                return fromJson((String) obj);
            }
            if (obj instanceof JSONObject) {
                return fromJson((JSONObject) obj);
            }

            JSONObject json = new JSONObject();
            json.put("value", obj);
            this.jsonObj = json;
        } catch (Exception e) {
            throw new IllegalArgumentException("format obj to JSONObject failed");
        }
        return this;
    }

    public DSObject fromJson(String json) {
        try {
            if (!TextUtils.isEmpty(json)) {
                if (!json.startsWith("{")) {
                    if (json.indexOf(":") < 0) {
                        json = "value:" + json;
                    }
                    json = "{" + json + "}";
                }
                jsonObj = new JSONObject(json);
            }
        } catch (JSONException e) {
            throw new IllegalArgumentException(e);
        }
        return this;
    }

    public <T extends DSObject> T convertToSubInstance(Class<T> classOfT) {
        try {
            T t = classOfT.getDeclaredConstructor(String.class).newInstance(objName);
            t.objName = objName;
            t.jsonObj = jsonObj;
            return t;
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    @Override
    public String toString() {
        return objName + ":" + jsonObj.toString();
    }

    @Override
    public int hashCode() {
        return objName.hashCode() + jsonObj.toString().hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (!(o instanceof DSObject)) {
            return false;
        }
        DSObject an = (DSObject) o;
        if (!objName.equals(an.objName)) {
            return false;
        }
        if (!jsonObj.toString().equals(an.jsonObj.toString())) {
            return false;
        }
        return true;
    }

}
