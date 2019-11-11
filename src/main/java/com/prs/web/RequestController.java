package com.prs.web;

import java.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.annotation.*;
import com.prs.business.Request;
import com.prs.db.RequestRepository;

@CrossOrigin
@RestController

@RequestMapping("/requests")

public class RequestController {

	@Autowired
	private RequestRepository requestRepo;

	// list - return all requests
	@GetMapping("/")
	public JsonResponse listRequests() {
		JsonResponse jr = null;
		try {
			jr = JsonResponse.getInstance(requestRepo.findAll());
		} catch (Exception e) {
			jr = JsonResponse.getInstance(e);
			e.printStackTrace();
		}
		return jr;
	}

	// return one request for a given ID
	@GetMapping("/{id}")
	public JsonResponse getRequest(@PathVariable int id) {
		JsonResponse jr = null;
		try {
			jr = JsonResponse.getInstance(requestRepo.findById(id));
		} catch (Exception e) {
			jr = JsonResponse.getInstance(e);
			e.printStackTrace();
		}
		return jr;
	}


	// add - adds a new request
	@PostMapping("/")
	public JsonResponse addRequest(@RequestBody Request r) {
		JsonResponse jr = null;
		r.setSubmittedDate(LocalDateTime.now());
		r.setStatus("New");
		r.setTotal(0);
		try {
			jr = JsonResponse.getInstance(requestRepo.save(r));
		} catch (DataIntegrityViolationException dive) {
			jr = JsonResponse.getInstance(dive.getRootCause().getMessage());
			dive.printStackTrace();
		} catch (Exception e) {
			jr = JsonResponse.getInstance(e);
			e.printStackTrace();
		}
		return jr;
	}

	// update - update a request
	@PutMapping("/")
	public JsonResponse updateRequest(@RequestBody Request r) {
		JsonResponse jr = null;
		try {
			if (requestRepo.existsById(r.getId())) {
				jr = JsonResponse.getInstance(requestRepo.save(r));
			} else {
				// record doesn't exist
				jr = JsonResponse.getInstance("Error updating request. " + "id: " + r.getId() + " doesn't exist");
			}
		} catch (DataIntegrityViolationException dive) {
			jr = JsonResponse.getInstance(dive.getRootCause().getMessage());
			dive.printStackTrace();
		} catch (Exception e) {
			jr = JsonResponse.getInstance(e);
			e.printStackTrace();
		}
		return jr;
	}

	// delete - delete a request
	@DeleteMapping("/{id}")
	public JsonResponse deleteRequest(@PathVariable int id) {
		JsonResponse jr = null;
		try {
			if (requestRepo.existsById(id)) {
				requestRepo.deleteById(id);
				jr = JsonResponse.getInstance("Delete successful!");
			} else {
				jr = JsonResponse.getInstance("Error deleting request. " + "id: " + id + " doesn't exist");
			}
		} catch (DataIntegrityViolationException dive) {
			jr = JsonResponse.getInstance(dive.getRootCause().getMessage());
			dive.printStackTrace();
		} catch (Exception e) {
			jr = JsonResponse.getInstance(e);
			e.printStackTrace();
		}

		return jr;
	}
	
	// submit a request for review
	@PutMapping("/submit-review")
	public JsonResponse submitRequest(@RequestBody Request r) {
		JsonResponse jr = null;
		if (r.getTotal() <= 50 ) 
		{
			r.setStatus("Approved");
		} else {
			r.setStatus("Review");
		}
		r.setSubmittedDate(LocalDateTime.now());
		try {
			jr = updateRequest(r);
		} catch (Exception e) {
			jr = JsonResponse.getInstance(e);
			e.printStackTrace();
		}
		return jr;
	}
	
	// submit a request for review
	@PutMapping("/approve")
	public JsonResponse approveRequest(@RequestBody Request r) {
		JsonResponse jr = null;
		r.setStatus("Approved");
		try {
			jr = updateRequest(r);
		} catch (Exception e) {
			jr = JsonResponse.getInstance(e);
			e.printStackTrace();
		}
		return jr;
	}
	
	// submit a request for review
	@PutMapping("/reject")
	public JsonResponse rejectRequest(@RequestBody Request r) {
		JsonResponse jr = null;
		r.setStatus("Rejected");
		try {
			jr = updateRequest(r);
		} catch (Exception e) {
			jr = JsonResponse.getInstance(e);
			e.printStackTrace();
		}
		return jr;
	}
	
	@GetMapping("/list-review/{userId}")
	public JsonResponse listRequestsForReview(@PathVariable Integer userId) {
		JsonResponse jr = null;
		try {
			jr = JsonResponse.getInstance(requestRepo.findByStatusAndUserIdNot("Review", userId));

		} catch (Exception e) {
			jr = JsonResponse.getInstance(e);
			e.printStackTrace();
		}
		return jr;
	}

}
