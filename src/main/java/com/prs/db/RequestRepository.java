package com.prs.db;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.prs.business.Request;

public interface RequestRepository extends CrudRepository<Request, Integer> {
	
	List<Request> findByStatusAndUserIdNot(String status, Integer UserId);

}
