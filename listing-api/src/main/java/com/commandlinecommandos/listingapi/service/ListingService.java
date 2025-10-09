package com.commandlinecommandos.listingapi.service;

import java.math.BigDecimal;
import java.util.List;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.commandlinecommandos.listingapi.model.Listing;
import com.commandlinecommandos.listingapi.model.ListingImage;
import com.commandlinecommandos.listingapi.model.Category;
import com.commandlinecommandos.listingapi.model.ItemCondition;
import com.commandlinecommandos.listingapi.model.ListingStatus;
import com.commandlinecommandos.listingapi.repository.ListingRepository;
import com.commandlinecommandos.listingapi.exception.ListingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class ListingService {

    private static final Logger logger = LoggerFactory.getLogger(ListingService.class);

    @Autowired
    private ListingRepository listingRepository;

    public Listing createListing(String title, String description, BigDecimal price, Category category,
            ItemCondition condition, String location, Long sellerId) {
        logger.debug("Creating listing - title: '{}', sellerId: {}, price: {}, category: {}", 
                   title, sellerId, price, category);
        
        try {
            Listing listing = new Listing(title, description, price, category, condition, location, sellerId);
            Listing savedListing = listingRepository.save(listing);
            
            logger.info("Successfully created listing ID: {} with title: '{}' for seller ID: {}", 
                       savedListing.getListingId(), savedListing.getTitle(), sellerId);
            
            return savedListing;
        } catch (Exception e) {
            logger.error("Error creating listing - title: '{}', sellerId: {}, error: {}", 
                        title, sellerId, e.getMessage(), e);
            throw new ListingException("Error creating listing - title: '" + title + "', sellerId: " + sellerId + ", error: " + e.getMessage(), e);
        }
    }

    public Listing createListing(String title, String description, BigDecimal price, Category category,
            ItemCondition condition, String location, Long sellerId, List<ListingImage> images) {
        logger.debug("Creating listing with images - title: '{}', sellerId: {}, price: {}, imageCount: {}", 
                   title, sellerId, price, images != null ? images.size() : 0);
        
        try {
            Listing listing = new Listing(title, description, price, category, condition, location, sellerId, images);
            Listing savedListing = listingRepository.save(listing);
            
            logger.info("Successfully created listing ID: {} with title: '{}' and {} images for seller ID: {}", 
                       savedListing.getListingId(), savedListing.getTitle(), 
                       images != null ? images.size() : 0, sellerId);
            
            return savedListing;
        } catch (Exception e) {
            logger.error("Error creating listing with images - title: '{}', sellerId: {}, error: {}", 
                        title, sellerId, e.getMessage(), e);
            throw new ListingException("Error creating listing with images - title: '" + title + "', sellerId: " + sellerId + ", error: " + e.getMessage(), e);
        }
    }

    public Listing getListingById(Long listingId) {
        logger.debug("Retrieving listing by ID: {}", listingId);
        
        try {
            Listing listing = listingRepository.findById(listingId)
                .orElseThrow(() -> new ListingException("Listing not found with id: " + listingId));
            
            logger.debug("Successfully retrieved listing ID: {} - title: '{}', status: {}, seller: {}", 
                        listingId, listing.getTitle(), listing.getStatus(), listing.getSellerId());
            
            return listing;
        } catch (ListingException e) {
            logger.warn("Listing not found with ID: {}", listingId);
            throw e;
        } catch (Exception e) {
            logger.error("Error retrieving listing ID: {} - error: {}", 
                        listingId, e.getMessage(), e);
            throw new ListingException("Error retrieving listing ID: " + listingId + " - error: " + e.getMessage(), e);
        }
    }

    public Page<Listing> getAllListings(Pageable pageable) {
        logger.debug("Retrieving all active listings - page: {}, size: {}", 
                   pageable.getPageNumber(), pageable.getPageSize());
        
        try {
            Page<Listing> listings = listingRepository.findByStatusOrderByCreatedAtDesc(ListingStatus.ACTIVE, pageable)
                .orElseThrow(() -> new ListingException("No listings found"));
            
            logger.info("Successfully retrieved {} active listings (page {}/{} with {} total elements)", 
                       listings.getNumberOfElements(), pageable.getPageNumber() + 1, 
                       listings.getTotalPages(), listings.getTotalElements());
            
            return listings;
        } catch (ListingException e) {
            logger.warn("No active listings found");
            throw e;
        } catch (Exception e) {
            logger.error("Error retrieving all listings - error: {}", 
                        e.getMessage(), e);
            throw new ListingException("Error retrieving all listings - error: " + e.getMessage(), e);
        }
    }

    public Page<Listing> getListingsBySellerId(Long sellerId, Pageable pageable) {
        logger.debug("Retrieving listings for seller ID: {} - page: {}, size: {}", 
                   sellerId, pageable.getPageNumber(), pageable.getPageSize());
        
        try {
            Page<Listing> listings = listingRepository.findBySellerId(sellerId, pageable)
                .orElseThrow(() -> new ListingException("No listings found for seller with id: " + sellerId));
            
            logger.info("Successfully retrieved {} listings for seller ID: {} (page {}/{} with {} total elements)", 
                       listings.getNumberOfElements(), sellerId, pageable.getPageNumber() + 1, 
                       listings.getTotalPages(), listings.getTotalElements());
            
            return listings;
        } catch (ListingException e) {
            logger.warn("No listings found for seller ID: {}", sellerId);
            throw new ListingException("No listings found for seller with id: " + sellerId);
        } catch (Exception e) {
            logger.error("Error retrieving listings for seller ID: {} - error: {}", 
                        sellerId, e.getMessage(), e);
            throw new ListingException("Error retrieving listings for seller ID: " + sellerId + " - error: " + e.getMessage(), e);
        }
    }

    public Page<Listing> getListingsBySellerIdAndStatus(Long sellerId, ListingStatus status, Pageable pageable) {
        return listingRepository.findBySellerIdAndStatus(sellerId, status, pageable)
            .orElseThrow(() -> new ListingException("No listings found for seller with id: " + sellerId + " and status: " + status));
    }

    public Page<Listing> getListingsByCategory(Category category, Pageable pageable) {
        return listingRepository.findByCategory(category, pageable)
            .orElseThrow(() -> new ListingException("No listings found for category: " + category));
    }

    public Page<Listing> getListingsByStatus(ListingStatus status, Pageable pageable) {
        return listingRepository.findByStatus(status, pageable)
            .orElseThrow(() -> new ListingException("No listings found for status: " + status));
    }

    public Page<Listing> searchListings(String keyword, Pageable pageable) {
        return listingRepository.findByTitleOrDescriptionContaining(keyword, pageable)
            .orElseThrow(() -> new ListingException("No listings found with keyword: " + keyword));
    }

    public Page<Listing> getListingsWithFilters(ListingStatus status, String keyword, Category category, ItemCondition condition,
            BigDecimal minPrice, BigDecimal maxPrice, String location, Pageable pageable) {
        logger.debug("Searching listings with filters - keyword: '{}', status: {}, category: {}, condition: {}, " +
                   "priceRange: {} - {}, location: '{}', page: {}, size: {}", 
                   keyword, status, category, condition, minPrice, maxPrice, location, 
                   pageable.getPageNumber(), pageable.getPageSize());
        
        try {
            Page<Listing> listings = listingRepository.findWithFilters(status, keyword, category, condition, minPrice, maxPrice, location, pageable)
                .orElseThrow(() -> new ListingException("No listings found with filters"));
            
            logger.info("Search completed - found {} listings matching filters (page {}/{} with {} total elements)", 
                       listings.getNumberOfElements(), pageable.getPageNumber() + 1, 
                       listings.getTotalPages(), listings.getTotalElements());
            
            return listings;
        } catch (ListingException e) {
            logger.info("No listings found matching the specified filters");
            throw new ListingException("No listings found matching the specified filters");
        } catch (Exception e) {
            logger.error("Error searching listings with filters - error: {}", 
                        e.getMessage(), e);
            throw new ListingException("Error searching listings with filters - error: " + e.getMessage(), e);
        }
    }
    
    public Listing updateListing(Long listingId, String title, String description, BigDecimal price, Category category,
            ItemCondition condition, String location, List<ListingImage> images) {
        logger.debug("Updating listing ID: {} - title: '{}', price: {}, imageCount: {}", 
                   listingId, title, price, images != null ? images.size() : 0);
        
        try {
            Listing listing = getListingById(listingId);
            if (listing == null) {
                logger.warn("Listing not found for update - ID: {}", listingId);
                throw new ListingException("Listing not found with id: " + listingId);
            }

            String oldTitle = listing.getTitle();
            BigDecimal oldPrice = listing.getPrice();
            
            listing.setTitle(title);
            listing.setDescription(description);
            listing.setPrice(price);
            listing.setCategory(category);
            listing.setCondition(condition);
            listing.setLocation(location);
            listing.setUpdatedAt(LocalDateTime.now());
            listing.setImages(images);
            
            Listing updatedListing = listingRepository.save(listing);
            
            logger.info("Successfully updated listing ID: {} - title changed from '{}' to '{}', " +
                       "price changed from {} to {}", 
                       listingId, oldTitle, title, oldPrice, price);
            
            return updatedListing;
        } catch (Exception e) {
            logger.error("Error updating listing ID: {} - error: {}", 
                        listingId, e.getMessage(), e);
            throw new ListingException("Error updating listing ID: " + listingId + " - error: " + e.getMessage(), e);
        }
    }

    public Listing addImagesToListing(Long listingId, List<ListingImage> images) {
        logger.debug("Adding images to listing ID: {} - image count: {}", listingId, images.size());

        try {
            Listing listing = getListingById(listingId);
            if (listing == null) {
                logger.warn("Listing not found for image addition - ID: {}", listingId);
                throw new ListingException("Listing not found with id: " + listingId);
            }

            listing.addImages(images);
            Listing updatedListing = listingRepository.save(listing);

            logger.info("Successfully added {} images to listing ID: {} - total images now: {}", 
                       images.size(), listingId, updatedListing.getImages().size());
            
            return updatedListing;
        } catch (Exception e) {
            logger.error("Error adding images to listing ID: {} - error: {}", 
                        listingId, e.getMessage(), e);
            throw new ListingException("Error adding images to listing ID: " + listingId + " - error: " + e.getMessage(), e);
        }
    }

    public Listing markAsSold(Long listingId) {
        logger.debug("Marking listing ID: {} as sold", listingId);
        
        try {
            Listing listing = getListingById(listingId);
            if (listing == null) {
                logger.warn("Listing not found for mark as sold - ID: {}", listingId);
                throw new ListingException("Listing not found with id: " + listingId);
            }

            ListingStatus oldStatus = listing.getStatus();
            listing.markAsSold();
            Listing updatedListing = listingRepository.save(listing);
            
            logger.info("Successfully marked listing ID: {} as sold - status changed from {} to {}", 
                       listingId, oldStatus, updatedListing.getStatus());
            
            return updatedListing;
        } catch (Exception e) {
            logger.error("Error marking listing ID: {} as sold - error: {}", 
                        listingId, e.getMessage(), e);
            throw new ListingException("Error marking listing ID: " + listingId + " as sold - error: " + e.getMessage(), e);
        }
    }

    public Listing cancelListing(Long listingId) {
        logger.debug("Cancelling listing ID: {}", listingId);
        
        try {
            Listing listing = getListingById(listingId);
            if (listing == null) {
                logger.warn("Listing not found for cancellation - ID: {}", listingId);
                throw new ListingException("Listing not found with id: " + listingId);
            }

            ListingStatus oldStatus = listing.getStatus();
            listing.setStatus(ListingStatus.CANCELLED);
            Listing updatedListing = listingRepository.save(listing);
            
            logger.info("Successfully cancelled listing ID: {} - status changed from {} to {}", 
                       listingId, oldStatus, updatedListing.getStatus());
            
            return updatedListing;
        } catch (Exception e) {
            logger.error("Error cancelling listing ID: {} - error: {}", 
                        listingId, e.getMessage(), e);
            throw new ListingException("Error cancelling listing ID: " + listingId + " - error: " + e.getMessage(), e);
        }
    }

    public void deleteListing(Long listingId) {
        logger.debug("Deleting listing ID: {}", listingId);
        
        try {
            Listing listing = getListingById(listingId);
            if (listing == null) {
                logger.warn("Listing not found for deletion - ID: {}", listingId);
                throw new ListingException("Listing not found with id: " + listingId);
            }
            
            String title = listing.getTitle();
            Long sellerId = listing.getSellerId();
            listingRepository.delete(listing);
            
            logger.info("Successfully deleted listing ID: {} with title: '{}' for seller ID: {}", 
                       listingId, title, sellerId);
        } catch (Exception e) {
            logger.error("Error deleting listing ID: {} - error: {}", 
                        listingId, e.getMessage(), e);
            throw new ListingException("Error deleting listing ID: " + listingId + " - error: " + e.getMessage(), e);
        }
    }

    public Long countListingsBySellerIdAndStatus(Long sellerId, ListingStatus status) {
        return listingRepository.countBySellerIdAndStatus(sellerId, status);
    }

    public boolean isListingOwner(Long listingId, Long sellerId) {
        logger.debug("Checking listing ownership - listing ID: {}, seller ID: {}", listingId, sellerId);
        
        try {
            Listing listing = getListingById(listingId);
            if (listing == null) {
                logger.warn("Listing not found for ownership check - ID: {}", listingId);
                throw new ListingException("Listing not found with id: " + listingId);
            }

            boolean isOwner = listing.getSellerId().equals(sellerId);
            logger.debug("Ownership check result - listing ID: {}, seller ID: {}, isOwner: {}", 
                        listingId, sellerId, isOwner);
            
            return isOwner;
        } catch (Exception e) {
            logger.error("Error checking listing ownership - listing ID: {}, seller ID: {}, error: {}", 
                        listingId, sellerId, e.getMessage(), e);
            throw new ListingException("Error checking listing ownership - listing ID: " + listingId + ", seller ID: " + sellerId + " - error: " + e.getMessage(), e);
        }
    }

    public int incrementViewCount(Long listingId) {
        logger.debug("Incrementing view count for listing ID: {}", listingId);
        
        try {
            Listing listing = getListingById(listingId);
            if (listing == null) {
                logger.warn("Listing not found for view count increment - ID: {}", listingId);
                throw new ListingException("Listing not found with id: " + listingId);
            }

            int oldViewCount = listing.getViewCount();
            listing.incrementViewCount();
            Listing updatedListing = listingRepository.save(listing);
            int newViewCount = updatedListing.getViewCount();
            
            logger.debug("Successfully incremented view count for listing ID: {} from {} to {}", 
                        listingId, oldViewCount, newViewCount);
            
            return newViewCount;
        } catch (Exception e) {
            logger.error("Error incrementing view count for listing ID: {} - error: {}", 
                        listingId, e.getMessage(), e);
            throw new ListingException("Error incrementing view count for listing ID: " + listingId + " - error: " + e.getMessage(), e);
        }
    }
}