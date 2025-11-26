package com.commandlinecommandos.campusmarketplace.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * REST Controller for Student-specific dashboard and operations
 * Note: Listing management has been moved to ListingController at /api/listings
 */
@RestController
@RequestMapping("/student")
@CrossOrigin(origins = "*", maxAge = 3600)
public class StudentController {

    /**
     * Get student dashboard with stats and quick info
     */
    @GetMapping("/dashboard")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<?> getStudentDashboard(Authentication authentication) {
        Map<String, Object> dashboard = new HashMap<>();
        dashboard.put("message", "Welcome to Student Dashboard");
        dashboard.put("username", authentication.getName());

        // Get actual stats
        // Note: These can be expanded with real data from services
        dashboard.put("myListings", 0); // Can query listingsService
        dashboard.put("watchlist", 0);
        dashboard.put("messages", 0);

        return ResponseEntity.ok(dashboard);
    }

    /**
     * Get student profile information
     */
    @GetMapping("/profile")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<?> getStudentProfile(Authentication authentication) {
        Map<String, Object> profile = new HashMap<>();
        profile.put("userId", authentication.getName());
        profile.put("message", "Student profile endpoint");
        return ResponseEntity.ok(profile);
    }

    /**
     * Get student listings
     * Note: This is a test endpoint. Real listing management is at /api/listings
     */
    @GetMapping("/listings")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<?> getStudentListings(Authentication authentication) {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Student listings endpoint");
        response.put("username", authentication.getName());
        return ResponseEntity.ok(response);
    }

    /**
     * Create student listing
     * Note: This is a test endpoint. Real listing creation is at /api/listings
     */
    @PostMapping("/listings")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<?> createStudentListing(Authentication authentication, @RequestBody Map<String, Object> listing) {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Listing created successfully");
        response.put("username", authentication.getName());
        response.put("listing", listing);
        return ResponseEntity.ok(response);
    }
}
