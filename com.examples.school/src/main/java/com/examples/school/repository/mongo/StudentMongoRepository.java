package com.examples.school.repository.mongo;

import java.util.Collections;
import java.util.List;

import com.examples.school.model.Student;
import com.examples.school.repository.StudentRepository;
import com.mongodb.MongoClient;

public class StudentMongoRepository implements StudentRepository {

	public StudentMongoRepository(MongoClient mongoClient,
			String schoolDbName, String studentCollectionName) {

	}

	@Override
	public List<Student> findAll() {
		return Collections.emptyList();
	}

	@Override
	public Student findById(String id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void save(Student student) {
		// TODO Auto-generated method stub
		
	}

}
