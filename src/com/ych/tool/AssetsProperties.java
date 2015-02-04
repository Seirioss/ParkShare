package com.ych.tool;

import android.content.Context;
import android.content.Loader;
import android.content.res.Resources;
import android.test.RenamingDelegatingContext;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.Properties;

/**
 * Created by fernando on 9/10/14.
 */
public class AssetsProperties {

	private static final String TAG = "AssetsProperties";
	private static final String EXTENSION = ".properties";
	private static final String DEFAUT_ANNOTATION_VALUE = "";
	private Properties properties;

	public static AssetsProperties load(Context context, String assetsfile) {
		return new AssetsProperties(context, assetsfile);
	}

	public static AssetsProperties load(Context context) {
		return new AssetsProperties(context, "propertie");
	}

	private AssetsProperties(Context context, String assetsfile) {
		super();
		properties = new Properties();
		try {
			properties.load(context.getAssets().open(assetsfile + EXTENSION));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public int getInt(String key, int defaultValue) {
		try {
			return Integer.parseInt(properties.getProperty(key));
		} catch (Exception e) {
			return defaultValue;
		}
	}

	public float getFloat(String key, float defaultValue) {
		try {
			return Float.parseFloat(properties.getProperty(key));
		} catch (Exception e) {
			return defaultValue;
		}
	}

	public double getDouble(String key, double defaultValue) {
		try {
			return Double.parseDouble(properties.getProperty(key));
		} catch (Exception e) {
			return defaultValue;
		}
	}

	public boolean getBoolean(String key, boolean defaultValue) {
		try {
			return Boolean.parseBoolean(properties.getProperty(key));
		} catch (Exception e) {
			return defaultValue;
		}
	}

	public String getString(String key, String defaultValue) {
		try {
			return properties.getProperty(key, defaultValue);
		} catch (Exception e) {
			return defaultValue;
		}
	}
}
