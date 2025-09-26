package com.commandlinecommandos.campusmarketplace.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.commandlinecommandos.campusmarketplace.model.ListingImage;
import com.commandlinecommandos.campusmarketplace.model.Listing;
import java.util.List;
import java.util.Optional;

@Repository
public interface ListingImageRepository extends JpaRepository<ListingImage, Long> {

    Optional<List<ListingImage>> findByListing(Listing listing);

    @Query("SELECT li FROM ListingImage li WHERE li.listing = :listing ORDER BY li.displayOrder ASC")
    Optional<List<ListingImage>> findByListingOrderedByDisplayOrder(@Param("listing") Listing listing);

    @Query("SELECT li FROM ListingImage li WHERE li.listing = :listing AND li.displayOrder = 1")
    Optional<ListingImage> findPrimaryImageByListing(@Param("listing") Listing listing);

    void deleteByListing(Listing listing);

}