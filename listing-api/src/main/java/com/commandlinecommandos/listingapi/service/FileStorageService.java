package com.commandlinecommandos.listingapi.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.commandlinecommandos.listingapi.repository.ListingImageRepository;

@Service
public class FileStorageService {

    @Autowired
    private ListingImageRepository listingImageRepository;
}