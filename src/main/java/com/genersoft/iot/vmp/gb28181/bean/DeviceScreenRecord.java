package com.genersoft.iot.vmp.gb28181.bean;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Date;

@Schema(description = "录屏文件列表")
public class DeviceScreenRecord {


	/**
	 * 数据库自增ID
	 */
	@Schema(description = "数据库自增ID")
	private int id;

	/**
	 * 文件名称
	 */
	@Schema(description = "文件名称")
	private String fileName;

	/**
	 * 文件类型
	 */
	@Schema(description = "文件类型")
	private String fileType;
	
	/**
	 * 用户姓名
	 */
	@Schema(description = "用户姓名")
	private String userName;
	
	/**
	 * 用户ID
	 */
	@Schema(description = "用户ID")
	private Integer userId;
	@Schema(description = "操作时间")
	private Date createTime;
	
	/**
	 * 文件路径
	 */
	@Schema(description = "文件路径")
	private String fileUrl;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getFileType() {
		return fileType;
	}

	public void setFileType(String fileType) {
		this.fileType = fileType;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public String getFileUrl() {
		return fileUrl;
	}

	public void setFileUrl(String fileUrl) {
		this.fileUrl = fileUrl;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
}
