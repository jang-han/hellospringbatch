package com.example.demo.repository.postgres;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.model.postgres.MedicalOpinionPostgres;

@Repository("postgresMedicalOpinionRepository")
public interface MedicalOpinionPostgresRepository extends JpaRepository<MedicalOpinionPostgres, Long> {
	MedicalOpinionPostgres findByName(String name);

	MedicalOpinionPostgres findByNameAndEmail(String name, String email);

}
