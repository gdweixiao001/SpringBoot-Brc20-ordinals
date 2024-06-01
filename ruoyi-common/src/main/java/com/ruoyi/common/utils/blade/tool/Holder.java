package com.ruoyi.common.utils.blade.tool;

import java.security.SecureRandom;
import java.util.Random;

/**
 * 一些常用的单利对象
 *
 */
public class Holder {

	/**
	 * RANDOM
	 */
	public final static Random RANDOM = new Random();

	/**
	 * SECURE_RANDOM
	 */
	public final static SecureRandom SECURE_RANDOM = new SecureRandom();
}
