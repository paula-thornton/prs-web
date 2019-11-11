package com.prs.web;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.annotation.*;

import com.prs.business.User;
import com.prs.db.UserRepository;

@CrossOrigin
@RestController

@RequestMapping("/users")

public class UserController {

	@Autowired
	private UserRepository userRepo;

	// list - return all users
	@GetMapping("/")
	public JsonResponse listUsers() {
		JsonResponse jr = null;
		try {
			jr = JsonResponse.getInstance(userRepo.findAll());
		} catch (Exception e) {
			jr = JsonResponse.getInstance(e);
			e.printStackTrace();
		}
		return jr;
	}

	// return one user for a given ID
	@GetMapping("/{id}")
	public JsonResponse getUser(@PathVariable int id) {
		JsonResponse jr = null;
		try {
			jr = JsonResponse.getInstance(userRepo.findById(id));
		} catch (Exception e) {
			jr = JsonResponse.getInstance(e);
			e.printStackTrace();
		}
		return jr;
	}

	// add - adds a new user
	@PostMapping("/")
	public JsonResponse addUser(@RequestBody User u) {
		JsonResponse jr = null;
		try {
			jr = JsonResponse.getInstance(userRepo.save(u));
		} catch (DataIntegrityViolationException dive) {
			jr = JsonResponse.getInstance(dive.getRootCause().getMessage());
			dive.printStackTrace();
		} catch (Exception e) {
			jr = JsonResponse.getInstance(e);
			e.printStackTrace();
		}
		return jr;
	}

	// update - update a user
	@PutMapping("/")
	public JsonResponse updateUser(@RequestBody User u) {
		JsonResponse jr = null;
		try {
			if (userRepo.existsById(u.getId())) {
				jr = JsonResponse.getInstance(userRepo.save(u));
			} else {
				// record doesn't exist
				jr = JsonResponse.getInstance("Error updating user. " + "id: " + u.getId() + " doesn't exist");
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

	// delete - delete a user
	@DeleteMapping("/{id}")
	public JsonResponse deleteUser(@PathVariable int id) {
		JsonResponse jr = null;
		try {
			if (userRepo.existsById(id)) {
				userRepo.deleteById(id);
				jr = JsonResponse.getInstance("Delete successful!");
			} else {
				jr = JsonResponse.getInstance("Error deleting user. " + "id: " + id + " doesn't exist");
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
	
	@PostMapping("/login")
	public JsonResponse login(@RequestBody User user) {
		JsonResponse jr = null;
		try {
			jr = JsonResponse.getInstance(userRepo.findByUserNameAndPassword(user.getUserName(), user.getPassword())); 
		} catch (Exception e) {
			jr = JsonResponse.getInstance(e);
			e.printStackTrace();
		}
		return jr;
	}

	
}
