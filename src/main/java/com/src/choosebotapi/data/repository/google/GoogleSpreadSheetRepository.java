package com.src.choosebotapi.data.repository.google;

import com.src.choosebotapi.data.model.google.GoogleSpreadSheet;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.webmvc.RepositoryRestController;

import java.util.Optional;

@RepositoryRestController
public interface GoogleSpreadSheetRepository extends CrudRepository<GoogleSpreadSheet, Long> {
}
