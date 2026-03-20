package com.iso.Model;

import jakarta.persistence.*;

@Entity
public class PrincipalCapabilityRating {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int voiceGesture;
    private int phonetics;
    private int fluencyNepali;
    private int fluencyEnglish;
    private int clarity;
    private int modulation;
    private int idiolectUse;
    private int hearingCapacity;
    private int handwriting;
    private int grammar;
    private int sentenceStructure;
    private int wiritngSpeed;
    private int vocabularyUsage;
    private int sentenceFormation;
    private int visibility;
    private int listening;
    private int temperament;
    private int leadershipSkills;
    private int teamwork;
    private int presentationSkills;
    private int communicationSkills;
    private int negotiationSkills;
    private int problemSolvingSkills;
    private int motivationalSkills;
    private int punctuality;
    private int classroomTimemanagement;
    private int openingclosingofClass;
    private int learnersagCapacity;
    private int usingFormattiveassessment;
    private int useworksheetHw;
    private int usesocialmediaLearners;
    private int useLearningaids;
    private int scoreCw;
    private int scoreHw;
    private int scoreUnitassessment;
    private int useTeachingaids;
    private int knowledge;
    private int understanding;
    private int application;
    private int higherAbility;
    private int socialmediaHandling;
    private int recordKeeping;
    private int implementationCorporatepolicies;
    private int consumingRestrictedelements;
    private int personalHygiene;

    @OneToOne
    @JoinColumn(name = "principal_id")
    private Principal principal;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public int getVoiceGesture() {
		return voiceGesture;
	}

	public void setVoiceGesture(int voiceGesture) {
		this.voiceGesture = voiceGesture;
	}

	public int getPhonetics() {
		return phonetics;
	}

	public void setPhonetics(int phonetics) {
		this.phonetics = phonetics;
	}

	public int getFluencyNepali() {
		return fluencyNepali;
	}

	public void setFluencyNepali(int fluencyNepali) {
		this.fluencyNepali = fluencyNepali;
	}

	public int getFluencyEnglish() {
		return fluencyEnglish;
	}

	public void setFluencyEnglish(int fluencyEnglish) {
		this.fluencyEnglish = fluencyEnglish;
	}

	public int getClarity() {
		return clarity;
	}

	public void setClarity(int clarity) {
		this.clarity = clarity;
	}

	public int getModulation() {
		return modulation;
	}

	public void setModulation(int modulation) {
		this.modulation = modulation;
	}

	public int getIdiolectUse() {
		return idiolectUse;
	}

	public void setIdiolectUse(int idiolectUse) {
		this.idiolectUse = idiolectUse;
	}

	public int getHearingCapacity() {
		return hearingCapacity;
	}

	public void setHearingCapacity(int hearingCapacity) {
		this.hearingCapacity = hearingCapacity;
	}
	
	

	public int getHandwriting() {
		return handwriting;
	}

	public void setHandwriting(int handwriting) {
		this.handwriting = handwriting;
	}

	public int getGrammar() {
		return grammar;
	}

	public void setGrammar(int grammar) {
		this.grammar = grammar;
	}

	public int getSentenceStructure() {
		return sentenceStructure;
	}

	public void setSentenceStructure(int sentenceStructure) {
		this.sentenceStructure = sentenceStructure;
	}

	public int getWiritngSpeed() {
		return wiritngSpeed;
	}

	public void setWiritngSpeed(int wiritngSpeed) {
		this.wiritngSpeed = wiritngSpeed;
	}

	public int getVocabularyUsage() {
		return vocabularyUsage;
	}

	public void setVocabularyUsage(int vocabularyUsage) {
		this.vocabularyUsage = vocabularyUsage;
	}

	public int getSentenceFormation() {
		return sentenceFormation;
	}

	public void setSentenceFormation(int sentenceFormation) {
		this.sentenceFormation = sentenceFormation;
	}

	public int getVisibility() {
		return visibility;
	}

	public void setVisibility(int visibility) {
		this.visibility = visibility;
	}

	public int getListening() {
		return listening;
	}

	public void setListening(int listening) {
		this.listening = listening;
	}

	public int getTemperament() {
		return temperament;
	}

	public void setTemperament(int temperament) {
		this.temperament = temperament;
	}

	public int getLeadershipSkills() {
		return leadershipSkills;
	}

	public void setLeadershipSkills(int leadershipSkills) {
		this.leadershipSkills = leadershipSkills;
	}

	public int getTeamwork() {
		return teamwork;
	}

	public void setTeamwork(int teamwork) {
		this.teamwork = teamwork;
	}

	public int getPresentationSkills() {
		return presentationSkills;
	}

	public void setPresentationSkills(int presentationSkills) {
		this.presentationSkills = presentationSkills;
	}

	public int getCommunicationSkills() {
		return communicationSkills;
	}

	public void setCommunicationSkills(int communicationSkills) {
		this.communicationSkills = communicationSkills;
	}

	public int getNegotiationSkills() {
		return negotiationSkills;
	}

	public void setNegotiationSkills(int negotiationSkills) {
		this.negotiationSkills = negotiationSkills;
	}

	public int getProblemSolvingSkills() {
		return problemSolvingSkills;
	}

	public void setProblemSolvingSkills(int problemSolvingSkills) {
		this.problemSolvingSkills = problemSolvingSkills;
	}

	public int getMotivationalSkills() {
		return motivationalSkills;
	}

	public void setMotivationalSkills(int motivationalSkills) {
		this.motivationalSkills = motivationalSkills;
	}

	public int getPunctuality() {
		return punctuality;
	}

	public void setPunctuality(int punctuality) {
		this.punctuality = punctuality;
	}

	public int getClassroomTimemanagement() {
		return classroomTimemanagement;
	}

	public void setClassroomTimemanagement(int classroomTimemanagement) {
		this.classroomTimemanagement = classroomTimemanagement;
	}

	public int getOpeningclosingofClass() {
		return openingclosingofClass;
	}

	public void setOpeningclosingofClass(int openingclosingofClass) {
		this.openingclosingofClass = openingclosingofClass;
	}

	public int getLearnersagCapacity() {
		return learnersagCapacity;
	}

	public void setLearnersagCapacity(int learnersagCapacity) {
		this.learnersagCapacity = learnersagCapacity;
	}

	public int getUsingFormattiveassessment() {
		return usingFormattiveassessment;
	}

	public void setUsingFormattiveassessment(int usingFormattiveassessment) {
		this.usingFormattiveassessment = usingFormattiveassessment;
	}

	public int getUseworksheetHw() {
		return useworksheetHw;
	}

	public void setUseworksheetHw(int useworksheetHw) {
		this.useworksheetHw = useworksheetHw;
	}

	public int getUsesocialmediaLearners() {
		return usesocialmediaLearners;
	}

	public void setUsesocialmediaLearners(int usesocialmediaLearners) {
		this.usesocialmediaLearners = usesocialmediaLearners;
	}

	public int getUseLearningaids() {
		return useLearningaids;
	}

	public void setUseLearningaids(int useLearningaids) {
		this.useLearningaids = useLearningaids;
	}

	public int getScoreCw() {
		return scoreCw;
	}

	public void setScoreCw(int scoreCw) {
		this.scoreCw = scoreCw;
	}

	public int getScoreHw() {
		return scoreHw;
	}

	public void setScoreHw(int scoreHw) {
		this.scoreHw = scoreHw;
	}

	
	
	public int getScoreUnitassessment() {
		return scoreUnitassessment;
	}

	public void setScoreUnitassessment(int scoreUnitassessment) {
		this.scoreUnitassessment = scoreUnitassessment;
	}

	public int getUseTeachingaids() {
		return useTeachingaids;
	}

	public void setUseTeachingaids(int useTeachingaids) {
		this.useTeachingaids = useTeachingaids;
	}

	public int getKnowledge() {
		return knowledge;
	}

	public void setKnowledge(int knowledge) {
		this.knowledge = knowledge;
	}

	public int getUnderstanding() {
		return understanding;
	}

	public void setUnderstanding(int understanding) {
		this.understanding = understanding;
	}

	public int getApplication() {
		return application;
	}

	public void setApplication(int application) {
		this.application = application;
	}

	public int getHigherAbility() {
		return higherAbility;
	}

	public void setHigherAbility(int higherAbility) {
		this.higherAbility = higherAbility;
	}

	public int getSocialmediaHandling() {
		return socialmediaHandling;
	}

	public void setSocialmediaHandling(int socialmediaHandling) {
		this.socialmediaHandling = socialmediaHandling;
	}

	public int getRecordKeeping() {
		return recordKeeping;
	}

	public void setRecordKeeping(int recordKeeping) {
		this.recordKeeping = recordKeeping;
	}

	public int getImplementationCorporatepolicies() {
		return implementationCorporatepolicies;
	}

	public void setImplementationCorporatepolicies(int implementationCorporatepolicies) {
		this.implementationCorporatepolicies = implementationCorporatepolicies;
	}

	public int getConsumingRestrictedelements() {
		return consumingRestrictedelements;
	}

	public void setConsumingRestrictedelements(int consumingRestrictedelements) {
		this.consumingRestrictedelements = consumingRestrictedelements;
	}

	public int getPersonalHygiene() {
		return personalHygiene;
	}

	public void setPersonalHygiene(int personalHygiene) {
		this.personalHygiene = personalHygiene;
	}

	public Principal getPrincipal() {
		return principal;
	}

	public void setPrincipal(Principal principal) {
		this.principal = principal;
	}
    
    
}
