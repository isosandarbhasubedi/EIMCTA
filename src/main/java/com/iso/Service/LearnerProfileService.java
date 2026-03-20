package com.iso.Service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.iso.Model.Learner;
import com.iso.Model.School;
import com.iso.Repository.LearnerRepository;

@Service
public class LearnerProfileService {

	 @Autowired
	    private LearnerRepository learnerRepository;

	    public List<Learner> getLearnersBySchool(School school) {
	        return learnerRepository.findBySchool(school);
	    }
}
