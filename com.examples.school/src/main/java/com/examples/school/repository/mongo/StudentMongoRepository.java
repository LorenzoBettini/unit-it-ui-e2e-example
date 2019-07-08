package com.examples.school.repository.mongo;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.bson.Document;

import com.examples.school.model.Student;
import com.examples.school.repository.StudentRepository;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;

public class StudentMongoRepository implements StudentRepository {

	private MongoCollection<Document> studentCollection;

	public StudentMongoRepository(MongoClient mongoClient,
			String schoolDbName, String studentCollectionName) {
		studentCollection = mongoClient
			.getDatabase(schoolDbName)
			.getCollection(studentCollectionName);
	}

	@Override
	public List<Student> findAll() {
		return StreamSupport.stream(studentCollection.find().spliterator(), false)
			.map(d -> new Student(d.get("id")+"", d.get("name")+""))
			.collect(Collectors.toList());
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
