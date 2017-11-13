package com.sc.plugin;


/**
 * Copyright (c) 2017-2018 LINKFACE Corporation. All rights reserved.
 */
public class RecognizerInitFailException extends Exception{

	/**
	 * BankCardRecognizer 无法正常初始化，抛出此异常
	 */
	private static final long serialVersionUID = -5328131538799809770L;
	private static final String msg = "Recognizer Cannot Create Instanse";
	
	public RecognizerInitFailException() {
		super(msg);
	}

}
