package com.prs.web;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.annotation.*;

import com.prs.business.LineItem;
import com.prs.business.Request;
import com.prs.db.LineItemRepository;
import com.prs.db.RequestRepository;

@CrossOrigin
@RestController

@RequestMapping("/line-items")

public class LineItemController {

	@Autowired
	private LineItemRepository lineItemRepo;
	
	@Autowired
	private RequestRepository requestRepo;
	
	// list - return all lineItems
	@GetMapping("/")
	public JsonResponse listLineItems() {
		JsonResponse jr = null;
		try {
			jr = JsonResponse.getInstance(lineItemRepo.findAll());
		} catch (Exception e) {
			jr = JsonResponse.getInstance(e);
			e.printStackTrace();
		}
		return jr;
	}

	// return one lineItem for a given ID
	@GetMapping("/{id}")
	public JsonResponse getLineItem(@PathVariable int id) {
		JsonResponse jr = null;
		try {
			jr = JsonResponse.getInstance(lineItemRepo.findById(id));
		} catch (Exception e) {
			jr = JsonResponse.getInstance(e);
			e.printStackTrace();
		}
		return jr;
	}

	// add - adds a new lineItem
	@PostMapping("/")
	public JsonResponse addLineItem(@RequestBody LineItem li) {
		JsonResponse jr = null;
		try {
			jr = JsonResponse.getInstance(lineItemRepo.save(li));
			recalcTotal(li.getRequest().getId());
		} catch (DataIntegrityViolationException dive) {
			jr = JsonResponse.getInstance(dive.getRootCause().getMessage());
			dive.printStackTrace();
		} catch (Exception e) {
			jr = JsonResponse.getInstance(e);
			e.printStackTrace();
		}
		return jr;
	}

	// update - update a lineItem
	@PutMapping("/")
	public JsonResponse updateLineItem(@RequestBody LineItem li) {
		JsonResponse jr = null;
		try {
			if (lineItemRepo.existsById(li.getId())) {
				jr = JsonResponse.getInstance(lineItemRepo.save(li));
				recalcTotal(li.getRequest().getId());
			} else {
				// record doesn't exist
				jr = JsonResponse.getInstance("Error updating lineItem. " + "id: " + li.getId() + " doesn't exist");
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

	// delete - delete a lineItem
	@DeleteMapping("/{id}")
	public JsonResponse deleteLineItem(@PathVariable int id) {
		JsonResponse jr = null;
		try {
			if (lineItemRepo.existsById(id)) {
				LineItem li = lineItemRepo.findById(id).get();
				lineItemRepo.deleteById(id);
				recalcTotal(li.getRequest().getId());
			} else {
				jr = JsonResponse.getInstance("Error deleting lineItem. " + "id: " + id + " doesn't exist");
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
	
	@GetMapping("/lines-for-pr/{requestId}")
	public JsonResponse listLineItemsForRequestId(@PathVariable Integer requestId) {
		JsonResponse jr = null;
		try {
			jr = JsonResponse.getInstance(lineItemRepo.findByRequestId(requestId)); 
		} catch (Exception e) {
			jr = JsonResponse.getInstance(e);
			e.printStackTrace();
		}
		return jr;
	}

	void recalcTotal(int requestId) {
		List<LineItem> li_list = lineItemRepo.findByRequestId(requestId);
		double total = 0;
		for (LineItem li: li_list) {
			total += li.getLineTotal();
		}
		
		Request request = requestRepo.findById(requestId).get();
		request.setTotal(total);
		try {
			requestRepo.save(request);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
