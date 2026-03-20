package com.iso.Model.dto;


public interface LearnerEnrollmentView {

	Long getId();

    LearnerView getLearner();

    SectionView getSection();
    
 // Add this!
    Boolean getActive();   // or boolean isActive()

}
