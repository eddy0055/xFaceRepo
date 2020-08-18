package com.xpand.xface.entity;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;
@Entity
@Table(name="tbl_time_unit")
public class TimeUnit implements Serializable {
	private static final long serialVersionUID = 1L;
	public static final int TIME_MIDNIGHT = 1;
	
	
	@Id
	@Column(name="tuId", nullable=false)
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Integer tuId;
		
	@Override
	public String toString() {
		return "TimeUnit [timeUnit=" + timeUnit + ", dividedValue=" + dividedValue + "]";
	}

	@Column(name="timeUnit", nullable=false)
	private String timeUnit;	
	
	@Column(name="dividedValue", nullable=false)
	private int dividedValue;
	
	@JsonIgnore
	@OneToMany(mappedBy="ignoreSamePersonTimeUnit", fetch=FetchType.LAZY )
	private Set<HWIPCAnalyzeList> hwIPCAnalyzeLists;

	public int getDividedValue() {
		return dividedValue;
	}

	public void setDividedValue(int dividedValue) {
		this.dividedValue = dividedValue;
	}

	public Integer getTuId() {
		return tuId;
	}

	public void setTuId(Integer tuId) {
		this.tuId = tuId;
	}

	public Set<HWIPCAnalyzeList> getHwIPCAnalyzeLists() {
		return hwIPCAnalyzeLists;
	}

	public void setHwIPCAnalyzeLists(Set<HWIPCAnalyzeList> hwIPCAnalyzeLists) {
		this.hwIPCAnalyzeLists = hwIPCAnalyzeLists;
	}

	public String getTimeUnit() {
		return timeUnit;
	}

	public void setTimeUnit(String timeUnit) {
		this.timeUnit = timeUnit;
	}
	
}
