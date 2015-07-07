package com.jtool.docbuilderplugin.model;

import java.util.Comparator;
import java.util.List;

public class ApiModel implements Comparator<ApiModel> {

	private String methodName;
	
	private Double chapter;
	private String apiName;
	private String info;
	private String host;
	private String url;
	private String method;
	private List<Class> requestType;
	private List<Class> errorType;
	private Class successType;
	private Class successReturn;
	private String otherInfo;
	private boolean isDeprecated;

	@Override
	public int compare(ApiModel o1, ApiModel o2) {
		return o1.getChapter().compareTo(o2.getChapter());
	}

	public String getMethodName() {
		return methodName;
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	public Double getChapter() {
		return chapter;
	}

	public void setChapter(Double chapter) {
		this.chapter = chapter;
	}

	public String getApiName() {
		return apiName;
	}

	public void setApiName(String apiName) {
		this.apiName = apiName;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public Class getSuccessReturn() {
		return successReturn;
	}

	public void setSuccessReturn(Class successReturn) {
		this.successReturn = successReturn;
	}

	public String getOtherInfo() {
		return otherInfo;
	}

	public void setOtherInfo(String otherInfo) {
		this.otherInfo = otherInfo;
	}

	public boolean isDeprecated() {
		return isDeprecated;
	}

	public void setIsDeprecated(boolean isDeprecated) {
		this.isDeprecated = isDeprecated;
	}

	public List<Class> getRequestType() {
		return requestType;
	}

	public void setRequestType(List<Class> requestType) {
		this.requestType = requestType;
	}

	public List<Class> getErrorType() {
		return errorType;
	}

	public void setErrorType(List<Class> errorType) {
		this.errorType = errorType;
	}

	public Class getSuccessType() {
		return successType;
	}

	public void setSuccessType(Class successType) {
		this.successType = successType;
	}

	@Override
	public String toString() {
		return "ApiModel{" +
				"methodName='" + methodName + '\'' +
				", chapter=" + chapter +
				", apiName='" + apiName + '\'' +
				", info='" + info + '\'' +
				", host='" + host + '\'' +
				", url='" + url + '\'' +
				", method='" + method + '\'' +
				", requestType=" + requestType +
				", errorType=" + errorType +
				", successType=" + successType +
				", successReturn=" + successReturn +
				", otherInfo='" + otherInfo + '\'' +
				", isDeprecated=" + isDeprecated +
				'}';
	}
}