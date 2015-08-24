package com.ab.health.utility;

import com.ab.health.model.User;

public class HealthUtility {

	public static float calHeightMi(float paramFloat) {
		return paramFloat / 100.0F;
	}

	public static float calBMI(User paramUser) {
		return paramUser.getWeight()/ (calHeightMi(paramUser.getHeight()) * calHeightMi(paramUser.getHeight()));
	}

	public static float calStanderWeight(User user) {
		float d;
		if (user.getSex().booleanValue()) {
			d = (float) (( user.getHeight() - 100 ) * 0.9 -2);
		} else {
			d = (float) (( user.getHeight() - 100 ) * 0.9 - 4.5);
		}
		return d;
	}

	public static double highFat(User paramUser) {
		return 0.8D * (200 - paramUser.getAge());
	}

	public static double highHealthWeight(User paramUser) {
		return 23.899999999999999D * calHeightMi(paramUser.getHeight())
				* calHeightMi(paramUser.getHeight());
	}

	public static double lowFat(User paramUser) {
		return 0.6D * (200 - paramUser.getAge());
	}

	public static double lowHealthWeight(User paramUser) {
		return 18.0F * calHeightMi(paramUser.getHeight())
				* calHeightMi(paramUser.getHeight());
	}

	public static double tunwei(User paramUser) {
		double d;
		if (paramUser.getSex().booleanValue()) {
			d = 0.51D * paramUser.getHeight();
		} else {
			d = 0.542D * paramUser.getHeight();
		}
		return d;
	}
	
	public static float calBMR(User paramUser){
		double d;
		if (paramUser.getSex().booleanValue()) {
			d = 67 + 13.73 * paramUser.getWeight() + 5 * paramUser.getHeight() - 6.9 * paramUser.getAge();
			
			//d = 10 * paramUser.getWeight() + 6.25 * paramUser.getHeight() - 5 * paramUser.getAge() + 5 ;
		} else {
			d = 661 + 9.6 * paramUser.getWeight() + 1.72 * paramUser.getHeight() - 4.7 * paramUser.getAge();
			
			//d = 10 * paramUser.getWeight() + 6.25 * paramUser.getHeight() - 5 * paramUser.getAge() - 161 ;
		}
		return (float)d;
	}
	
	public static double xiongwei(User paramUser) {
		double d;
		if (paramUser.getSex().booleanValue()) {
			d = 0.48D * paramUser.getHeight();
		} else {
			d = 0.51D * paramUser.getHeight();
		}
		return d;
	}

	public static String boolHealth(User paramUser) {
		String str;
		Double localDouble = Double.valueOf(calBMI(paramUser));
		if (localDouble.doubleValue() < 18.5D) {
			str = "偏轻";
		} else if ((localDouble.doubleValue() >= 18.5D)
				&& (localDouble.doubleValue() < 24.0D)) {
			str = "正常";
		} else if ((localDouble.doubleValue() >= 24.0D)
				&& (localDouble.doubleValue() < 28.0D)) {
			str = "超重";
		} else {
			str = "肥胖";
		}
		return str;
	}

	public static double yaowei(User paramUser) {
		double d;
		if (paramUser.getSex().booleanValue()) {
			d = 0.47D * paramUser.getHeight();
		} else {
			d = 0.34D * paramUser.getHeight();
		}
		return d;
	}

	public static float standardHealthWeight(User paramUser,boolean low) {
		if(low){
			return 18.5f * calHeightMi(paramUser.getHeight()) * calHeightMi(paramUser.getHeight());
		}else {
			return 24f * calHeightMi(paramUser.getHeight()) * calHeightMi(paramUser.getHeight());
		}
	}

	public static double xishu(User paramUser) {
		return 0.00659D * paramUser.getHeight() + 0.0126D
				* paramUser.getWeight() - 0.1603D;
	}

}
