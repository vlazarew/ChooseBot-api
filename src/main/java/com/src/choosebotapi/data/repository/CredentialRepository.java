package com.src.choosebotapi.data.repository;

import com.src.choosebotapi.data.model.Credential;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.webmvc.RepositoryRestController;

@RepositoryRestController
public interface CredentialRepository extends CrudRepository<Credential, Long> {
}
