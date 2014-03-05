package com.fimtrus.securitycard.helper;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import com.fimtrus.securitycard.model.CardInfoModel;
import com.fimtrus.securitycard.util.AesCrypto;
import com.google.gson.Gson;

public class CardInfoHelper {
	/**
	 * Preference에 저장된 내용들 얻어온다. Key가 필요하다.
	 * 
	 * @param name
	 *            : Key
	 * @return
	 */
	public static CardInfoModel getCardInfo ( Context context, String name ) {
		
		Gson gson = new Gson();
		SharedPreferences pref = context.getSharedPreferences("card_information", Activity.MODE_PRIVATE);
		
		String value = pref.getString(name, null);
		String decryptValue = null;
		
		if ( value != null ) {
			try {
				decryptValue = AesCrypto.decrypt( context, value);
				return gson.fromJson(decryptValue, CardInfoModel.class);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return null;
	}
	
	
	/**
	 * Preference에 데이터를 저장한다.
	 */
	public static boolean setCardInfo ( Context context, CardInfoModel cardInfo ) {
		
		Gson gson = new Gson();
		SharedPreferences pref = context.getSharedPreferences("card_information", Activity.MODE_PRIVATE);
		SharedPreferences.Editor editor = pref.edit();
		
		String jsonString = gson.toJson(cardInfo);
		
		if (jsonString instanceof String) {
			
			String cryptValue = null;
			try {
				cryptValue = AesCrypto.encrypt( context, jsonString);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return false;
			}
			
			if ( cryptValue != null ) {
				editor.putString(cardInfo.getCardCertNumber(), cryptValue );
				editor.commit();
			}
		} 
		return true;
	}
	
	/**
	 * Preference에 저장된 내용들 얻어온다. Key가 필요하다.
	 * 
	 * @param name
	 *            : Key
	 * @return
	 */
	public static ArrayList<CardInfoModel> getAll ( Context context ) {
		
		ArrayList<CardInfoModel> cardInfoList = new ArrayList<CardInfoModel>();
		Gson gson = new Gson();
		SharedPreferences pref = context.getSharedPreferences("card_information", Activity.MODE_PRIVATE);
		
		Map map = pref.getAll();
		Set keySet = map.keySet();
		Iterator keyIterator = keySet.iterator();
		
		while ( keyIterator.hasNext() ) {
			String value = (String) map.get(keyIterator.next());
			
			String decryptValue = null;
			
			if ( value != null ) {
				try {
					decryptValue = AesCrypto.decrypt( context, value);
					cardInfoList.add( gson.fromJson(decryptValue, CardInfoModel.class) );
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
		}
		
		
		return cardInfoList;
	}
	
	/**
	 * Preference에 데이터를 저장한다.
	 */
	public static boolean setDefault ( Context context, String cardCertNumber ) {
		
		SharedPreferences pref = context.getSharedPreferences("card_default", Activity.MODE_PRIVATE);
		SharedPreferences.Editor editor = pref.edit();
		
			
		String cryptValue = null;
		try {
			cryptValue = AesCrypto.encrypt( context, cardCertNumber);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		
		if ( cryptValue != null ) {
			editor.putString("card_default", cryptValue );
			editor.commit();
		}
		return true;
	}
	/**
	 * Preference에 데이터를 저장한다.
	 */
	public static String getDefault ( Context context ) {
		
		SharedPreferences pref = context.getSharedPreferences("card_default", Activity.MODE_PRIVATE);
		
		String value = pref.getString("card_default", null);
		String decryptValue = null;
		
		if ( value != null ) {
			try {
				decryptValue = AesCrypto.decrypt( context, value);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return decryptValue;
	}
	
	/**
	 * 카드정보를 삭제한다.
	 */
	public static boolean removeCard ( Context context, CardInfoModel cardInfo ) {
		
		Gson gson = new Gson();
		SharedPreferences pref = context.getSharedPreferences("card_information", Activity.MODE_PRIVATE);
		SharedPreferences.Editor editor = pref.edit();
		
		editor.remove(cardInfo.getCardCertNumber());
		editor.commit();
		return true;
	}
	/**
	 * 패스워드를 저장한다.
	 */
	public static boolean setPassword ( Context context, String password ) {
		
		SharedPreferences pref = context.getSharedPreferences("login_password", Activity.MODE_PRIVATE);
		SharedPreferences.Editor editor = pref.edit();
		
			
		String cryptValue = null;
		try {
			cryptValue = AesCrypto.encrypt( context,  password );
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		
		if ( cryptValue != null ) {
			editor.putString("card_default", cryptValue );
			editor.commit();
		}
		return true;
	}
	/**
	 * 패스워드를 불러온다.
	 */
	public static String getPassword ( Context context ) {
		
		SharedPreferences pref = context.getSharedPreferences("login_password", Activity.MODE_PRIVATE);
		
		String value = pref.getString("card_default", null);
		String decryptValue = null;
		
		if ( value != null ) {
			try {
				decryptValue = AesCrypto.decrypt( context, value);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return decryptValue;
	}
	
}
