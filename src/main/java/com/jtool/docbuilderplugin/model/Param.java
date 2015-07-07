package com.jtool.docbuilderplugin.model;

public class Param {
	private String key;
	private String option;
	private String comment;
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public String getOption() {
		return option;
	}
	public void setOption(String option) {
		this.option = option;
	}
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	@Override
	public String toString() {
		return "Param [key=" + key + ", option=" + option + ", comment=" + comment + "]";
	}
}
