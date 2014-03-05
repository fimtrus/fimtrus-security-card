package com.fimtrus.securitycard.model;

import java.util.ArrayList;

import com.fimtrus.securitycard.CommonApplication;
import com.fimtrus.securitycard.helper.CardInfoHelper;

public class CardInfoModel {
	String index;
	String bankName;
	String cardCertNumber;
//	int[] certNumbers;
	
	ArrayList<String> certNumbers;
	
//	public static final CardInfoModel newInstance( String bankName, String cardCertNumber, int[] certNumbers ) {
//		
//		CardInfoModel cardInfo = new CardInfoModel();
//		
//		cardInfo.index = String.valueOf( CardInfoHelper.getAll(CommonApplication.getApplication().getApplicationContext()).size() );
//		cardInfo.bankName = bankName;
//		cardInfo.cardCertNumber = cardCertNumber;
//		cardInfo.certNumbers = certNumbers;
//		
//		return cardInfo;
//	}
	
	public static final CardInfoModel newInstance( String bankName, String cardCertNumber, ArrayList<String> certNumbers ) {
		
		CardInfoModel cardInfo = new CardInfoModel();
		
		cardInfo.index = String.valueOf( CardInfoHelper.getAll(CommonApplication.getApplication().getApplicationContext()).size() );
		cardInfo.bankName = bankName;
		cardInfo.cardCertNumber = cardCertNumber;
		cardInfo.certNumbers = certNumbers;
		
		return cardInfo;
	}

	public String getIndex() {
		return index;
	}

	public void setIndex(String index) {
		this.index = index;
	}

	public String getBankName() {
		return bankName;
	}

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}

	public String getCardCertNumber() {
		return cardCertNumber;
	}

	public void setCardCertNumber(String cardCertNumber) {
		this.cardCertNumber = cardCertNumber;
	}

	public ArrayList<String> getCertNumbers() {
		return certNumbers;
	}

	public void setCertNumbers(ArrayList<String> certNumbers) {
		this.certNumbers = certNumbers;
	}
}
