package com.My_Car_Rental_Application.entity;

import jakarta.persistence.Embeddable;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Embeddable
public class Features {

	private String feature1;
	private String feature2;
	private String feature3;
	private String feature4;
	private String feature5;
	
	public Features() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Features(String feature1, String feature2, String feature3, String feature4, String feature5) {
		super();
		this.feature1 = feature1;
		this.feature2 = feature2;
		this.feature3 = feature3;
		this.feature4 = feature4;
		this.feature5 = feature5;
	}

	public String getFeature1() {
		return feature1;
	}

	public void setFeature1(String feature1) {
		this.feature1 = feature1;
	}

	public String getFeature2() {
		return feature2;
	}

	public void setFeature2(String feature2) {
		this.feature2 = feature2;
	}

	public String getFeature3() {
		return feature3;
	}

	public void setFeature3(String feature3) {
		this.feature3 = feature3;
	}

	public String getFeature4() {
		return feature4;
	}

	public void setFeature4(String feature4) {
		this.feature4 = feature4;
	}

	public String getFeature5() {
		return feature5;
	}

	public void setFeature5(String feature5) {
		this.feature5 = feature5;
	}

		

}
