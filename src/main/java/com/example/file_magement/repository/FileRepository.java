package com.example.file_magement.repository;

import com.example.file_magement.entity.FileMetaData;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FileRepository extends MongoRepository<FileMetaData, String> {
}
