package com.commandlinecommandos.listingapi.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.commandlinecommandos.listingapi.repository.ListingRepository;

@Service
public class ListingService {

    @Autowired
    private ListingRepository listingRepository;
}