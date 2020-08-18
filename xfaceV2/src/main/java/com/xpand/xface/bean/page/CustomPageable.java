package com.xpand.xface.bean.page;

import java.io.Serializable;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public class CustomPageable implements Pageable, Serializable{
	private static final long serialVersionUID = -1786698244591714484L;	
	private int offset = 0;
	private Sort sort = null;
	private int pageSize = 0;
	public CustomPageable(int offset, int pageSize, Sort sort){        
        this.offset = offset;
        this.sort = sort;
        this.pageSize = pageSize;         
    }
	@Override
	public int getPageNumber() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getPageSize() {
		return this.pageSize;
	}

	@Override
	public int getOffset() {
		return this.offset;
	}

	@Override
	public Sort getSort() {
		// TODO Auto-generated method stub
		return this.sort;
	}

	@Override
	public Pageable next() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Pageable previousOrFirst() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Pageable first() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean hasPrevious() {
		// TODO Auto-generated method stub
		return false;
	}
}
