package com.lazan.acme.slots;

import java.util.Date;

public class Bag {
	private Date created;
	private BagState state;
	private String bagId;
	private DenominationType type;
	private int total;
	private String voltId;

	public Date getCreated() {
		return created;
	}
	public void setCreated(Date created) {
		this.created = created;
	}
	public BagState getState() {
		return state;
	}
	public void setState(BagState state) {
		this.state = state;
	}
	public String getBagId() {
		return bagId;
	}
	public void setBagId(String bagId) {
		this.bagId = bagId;
	}
	public DenominationType getType() {
		return type;
	}
	public void setType(DenominationType type) {
		this.type = type;
	}
	public int getTotal() {
		return total;
	}
	public void setTotal(int total) {
		this.total = total;
	}
	public String getVoltId() {
		return voltId;
	}
	public void setVoltId(String voltId) {
		this.voltId = voltId;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((bagId == null) ? 0 : bagId.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Bag other = (Bag) obj;
		if (bagId == null) {
			if (other.bagId != null)
				return false;
		} else if (!bagId.equals(other.bagId))
			return false;
		return true;
	}
}
