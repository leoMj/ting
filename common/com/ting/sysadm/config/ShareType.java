package com.ting.sysadm.config;

public enum ShareType {
	Dynamic(0, "发布了动态"), Blog(1, "发布了文章"), BLOG(2, "反馈意见"), LEAVEMESSAGE(3, "留言");
	/** 状态值 */
	private long value;
	/** 状态名 */
	private String name;

	private ShareType(long value, String name) {
		this.value = value;
		this.name = name;
	}

	public long getValue() {
		return value;
	}

	public String getName() {
		return name;
	}

	public static void main(String[] args) {
	}
}
