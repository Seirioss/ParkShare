package com.ych.dao;

import java.util.Date;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * 
 * @author kelly
 * @deprecated 使用开源框架ORMLite操作sqlite数据库，建立与数据库表对应的project;
 * 数据库对应的pojo类，注意一下三点 
 * 1、填写表的名称 "@DatabaseTable"
 * 2、填写表中持久化项的 "@DatabaseField" 还可使顺便设置其属性
 * 3、保留一个无参的构造函数
 */

@DatabaseTable(tableName = "park")
public class Park {
	// 主键 id 自增长
	@DatabaseField(generatedId = true)
	private int id;
	@DatabaseField(index=true)
	private int pk;
	@DatabaseField
	private String MAC;
	@DatabaseField
	private String key_lock;
	@DatabaseField
	private String key_open;
	@DatabaseField
	private String address;
	@DatabaseField
	private String comment;
	@DatabaseField
	private String describe;
	@DatabaseField
	private String name_own;
	@DatabaseField
	private double longitude;
	@DatabaseField
	private double latitude;
	@DatabaseField
	private String lock_name;
	@DatabaseField
	private boolean is_shared;
	@DatabaseField
	private Date time_share_begig;
	@DatabaseField
	private Date time_share_end;

	@DatabaseField
	private boolean is_borrowed;
	@DatabaseField
	private String name_borrowed;
	@DatabaseField
	private Date time_borrowed_begin;
	@DatabaseField
	private Date time_borrowed_end;
	@DatabaseField
	private float price;

	public Park() {
		//ORMLite 需要一个无参构造
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getPk() {
		return pk;
	}

	public void setPk(int pk) {
		this.pk = pk;
	}

	public String getMAC() {
		return MAC;
	}

	public void setMAC(String mAC) {
		MAC = mAC;
	}

	public String getKey_lock() {
		return key_lock;
	}

	public void setKey_lock(String key_lock) {
		this.key_lock = key_lock;
	}

	public String getKey_open() {
		return key_open;
	}

	public void setKey_open(String key_open) {
		this.key_open = key_open;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getDescribe() {
		return describe;
	}

	public void setDescribe(String describe) {
		this.describe = describe;
	}

	public String getName_own() {
		return name_own;
	}

	public void setName_own(String name_own) {
		this.name_own = name_own;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public boolean isIs_shared() {
		return is_shared;
	}

	public void setIs_shared(boolean is_shared) {
		this.is_shared = is_shared;
	}

	public Date getTime_share_begig() {
		return time_share_begig;
	}

	public void setTime_share_begig(Date time_share_begig) {
		this.time_share_begig = time_share_begig;
	}

	public Date getTime_share_end() {
		return time_share_end;
	}

	public void setTime_share_end(Date time_share_end) {
		this.time_share_end = time_share_end;
	}

	public boolean isIs_borrowed() {
		return is_borrowed;
	}

	public void setIs_borrowed(boolean is_borrowed) {
		this.is_borrowed = is_borrowed;
	}

	public String getName_borrowed() {
		return name_borrowed;
	}

	public void setName_borrowed(String name_borrowed) {
		this.name_borrowed = name_borrowed;
	}

	public Date getTime_borrowed_begin() {
		return time_borrowed_begin;
	}

	public void setTime_borrowed_begin(Date time_borrowed_begin) {
		this.time_borrowed_begin = time_borrowed_begin;
	}

	public Date getTime_borrowed_end() {
		return time_borrowed_end;
	}

	public void setTime_borrowed_end(Date time_borrowed_end) {
		this.time_borrowed_end = time_borrowed_end;
	}

	public float getPrice() {
		return price;
	}

	public void setPrice(float price) {
		this.price = price;
	}

	public String getLock_name() {
		return lock_name;
	}

	public void setLock_name(String lock_name) {
		this.lock_name = lock_name;
	}

	@Override
	public String toString() {
		return "Park [id=" + id + ", pk=" + pk + ", MAC=" + MAC + ", key_lock=" + key_lock + ", key_open=" + key_open + ", address=" + address + ", comment=" + comment + ", describe=" + describe + ", name_own=" + name_own + ", longitude=" + longitude + ", latitude=" + latitude + ", lock_name="
				+ lock_name + ", is_shared=" + is_shared + ", time_share_begig=" + time_share_begig + ", time_share_end=" + time_share_end + ", is_borrowed=" + is_borrowed + ", name_borrowed=" + name_borrowed + ", time_borrowed_begin=" + time_borrowed_begin + ", time_borrowed_end="
				+ time_borrowed_end + ", price=" + price + "]";
	}

}
