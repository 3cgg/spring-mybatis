package me.libme.module.spring.mybatis;

import java.util.Date;


/**
 *
 * @author J
 *
 */
@SuppressWarnings("serial")
public abstract class BaseModel implements IEntityModel {

	/**
	 * the primary key , uuid 
	 */
	private String id;
	
	/**
	 * create user id
	 */
	private String createId;
	
	/**
	 * update user id 
	 */
	private String updateId;
	
	/**
	 * create time
	 */
	private Date createTime;
	
	/**
	 * update time
	 */
	private Date updateTime;
	
	/**
	 * marks whether the record is deleted. Value is Y|N
	 */
	private String deleted;
	
	/**
	 * the property can limit the async operation effectively 
	 */
	private int version;


	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getCreateId() {
		return createId;
	}

	public void setCreateId(String createId) {
		this.createId = createId;
	}

	public String getUpdateId() {
		return updateId;
	}

	public void setUpdateId(String updateId) {
		this.updateId = updateId;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public String getDeleted() {
		return deleted;
	}

	public void setDeleted(String deleted) {
		this.deleted = deleted;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	@Override
	public String get_id() {
		return getId();
	}

	@Override
	public void set_id(String id) {
		setId(id);
	}

	@Override
	public String get_create_id() {
		return getCreateId();
	}

	@Override
	public void set_create_id(String createId) {
		setCreateId(createId);
	}

	@Override
	public String get_update_id() {
		return getUpdateId();
	}

	@Override
	public void set_update_id(String updateId) {
		setUpdateId(updateId);
	}

	@Override
	public Date get_create_time() {
		return getCreateTime();
	}

	@Override
	public void set_create_time(Date createTime) {
		setCreateTime(createTime);
	}

	@Override
	public Date get_update_time() {
		return getUpdateTime();
	}

	@Override
	public void set_update_time(Date updateTime) {
		setUpdateTime(updateTime);
	}

	@Override
	public String get_deleted() {
		return getDeleted();
	}

	@Override
	public void set_deleted(String deleted) {
		setDeleted(deleted);
	}

	@Override
	public int get_version() {
		return getVersion();
	}

	@Override
	public void set_version(Integer version) {
		setVersion(version);
	}
}
