package com.iso.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.iso.Model.Principal;
import com.iso.Model.PrincipalEducation;
import com.iso.Model.SchoolUserEditAuditLog;
import com.iso.Repository.SchoolUserEditAuditLogRepository;


@Service
public class SchoolUserEditAuditLogService {

    private final SchoolUserEditAuditLogRepository auditLogRepository;

    public SchoolUserEditAuditLogService(SchoolUserEditAuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    public void log(String performedByEmail,
            String performedForEmail,
            String entityType,
            String action,
            String details,
            String ipAddress) {

SchoolUserEditAuditLog log = new SchoolUserEditAuditLog();
log.setPerformedBy(performedByEmail);
log.setPerformedFor(performedForEmail);
log.setEntityType(entityType);
log.setAction(action);
log.setDetails(details);
log.setIpAddress(ipAddress);
log.setPerformedAt(LocalDateTime.now());

auditLogRepository.save(log);
}
    
    public String generateSection1ChangeDetails(Principal oldPrincipal, Principal newPrincipal) {
        StringBuilder details = new StringBuilder();

        // Personal Info
        var oldPI = oldPrincipal.getPersonalInfo();
        var newPI = newPrincipal.getPersonalInfo();

        if (!Objects.equals(oldPI.getTitle(), newPI.getTitle()))
            details.append("Title: ").append(oldPI.getTitle()).append(" → ").append(newPI.getTitle()).append("; ");
        if (!Objects.equals(oldPI.getFirstName(), newPI.getFirstName()))
            details.append("First Name: ").append(oldPI.getFirstName()).append(" → ").append(newPI.getFirstName()).append("; ");
        if (!Objects.equals(oldPI.getSurName(), newPI.getSurName()))
            details.append("Surname: ").append(oldPI.getSurName()).append(" → ").append(newPI.getSurName()).append("; ");
        if (!Objects.equals(oldPI.getMaidenName(), newPI.getMaidenName()))
            details.append("Maiden Name: ").append(oldPI.getMaidenName()).append(" → ").append(newPI.getMaidenName()).append("; ");
        if (!Objects.equals(oldPI.getDateOfBirth(), newPI.getDateOfBirth()))
            details.append("DOB(AD): ").append(oldPI.getDateOfBirth()).append(" → ").append(newPI.getDateOfBirth()).append("; ");
        if (!Objects.equals(oldPI.getDateOfBirthNepali(), newPI.getDateOfBirthNepali()))
            details.append("DOB(BS): ").append(oldPI.getDateOfBirthNepali()).append(" → ").append(newPI.getDateOfBirthNepali()).append("; ");
        if (!Objects.equals(oldPI.getGender(), newPI.getGender()))
            details.append("Gender: ").append(oldPI.getGender()).append(" → ").append(newPI.getGender()).append("; ");
        if (!Objects.equals(oldPI.getPhoneNumber(), newPI.getPhoneNumber()))
            details.append("Phone Number: ").append(oldPI.getPhoneNumber()).append(" → ").append(newPI.getPhoneNumber()).append("; ");
        if (!Objects.equals(oldPI.getEmergencyContactNumber(), newPI.getEmergencyContactNumber()))
            details.append("Emergency Contact: ").append(oldPI.getEmergencyContactNumber()).append(" → ").append(newPI.getEmergencyContactNumber()).append("; ");
        if (!Objects.equals(oldPI.getPreferredCommunication(), newPI.getPreferredCommunication()))
            details.append("Preferred Communication: ").append(oldPI.getPreferredCommunication()).append(" → ").append(newPI.getPreferredCommunication()).append("; ");

        // Family Info
        var oldFI = oldPrincipal.getFamilyInfo();
        var newFI = newPrincipal.getFamilyInfo();

        if (!Objects.equals(oldFI.getMaritalStatus(), newFI.getMaritalStatus()))
            details.append("Marital Status: ").append(oldFI.getMaritalStatus()).append(" → ").append(newFI.getMaritalStatus()).append("; ");
        if (!Objects.equals(oldFI.getBloodGroup(), newFI.getBloodGroup()))
            details.append("Blood Group: ").append(oldFI.getBloodGroup()).append(" → ").append(newFI.getBloodGroup()).append("; ");
        if (!Objects.equals(oldFI.getSpouseName(), newFI.getSpouseName()))
            details.append("Spouse Name: ").append(oldFI.getSpouseName()).append(" → ").append(newFI.getSpouseName()).append("; ");
        if (!Objects.equals(oldFI.getChildrenNumber(), newFI.getChildrenNumber()))
            details.append("Children Number: ").append(oldFI.getChildrenNumber()).append(" → ").append(newFI.getChildrenNumber()).append("; ");
        if (!Objects.equals(oldFI.getDependentNumber(), newFI.getDependentNumber()))
            details.append("Dependent Number: ").append(oldFI.getDependentNumber()).append(" → ").append(newFI.getDependentNumber()).append("; ");
        if (!Objects.equals(oldFI.getActiveEarners(), newFI.getActiveEarners()))
            details.append("Active Earners: ").append(oldFI.getActiveEarners()).append(" → ").append(newFI.getActiveEarners()).append("; ");

        return details.toString();
    }
    
    public String generateSection2ChangeDetails(List<PrincipalEducation> oldList,
            List<PrincipalEducation> newList) {

StringBuilder details = new StringBuilder();

Map<Long, PrincipalEducation> oldMap = oldList.stream()
.collect(Collectors.toMap(PrincipalEducation::getId, e -> e));

Map<Long, PrincipalEducation> newMap = newList.stream()
.filter(e -> e.getId() != null)
.collect(Collectors.toMap(PrincipalEducation::getId, e -> e));

// Detect Updated
for (PrincipalEducation newEdu : newList) {

if (newEdu.getId() != null && oldMap.containsKey(newEdu.getId())) {

PrincipalEducation oldEdu = oldMap.get(newEdu.getId());

if (!Objects.equals(oldEdu.getHighestQualification(), newEdu.getHighestQualification()))
details.append("Highest Qualification: ")
.append(oldEdu.getHighestQualification())
.append(" → ")
.append(newEdu.getHighestQualification())
.append("; ");

if (!Objects.equals(oldEdu.getSpecializedIn(), newEdu.getSpecializedIn()))
details.append("Specialized In: ")
.append(oldEdu.getSpecializedIn())
.append(" → ")
.append(newEdu.getSpecializedIn())
.append("; ");

if (!Objects.equals(oldEdu.getCompletionYear(), newEdu.getCompletionYear()))
details.append("Completion Year: ")
.append(oldEdu.getCompletionYear())
.append(" → ")
.append(newEdu.getCompletionYear())
.append("; ");

if (!Objects.equals(oldEdu.getBoard(), newEdu.getBoard()))
details.append("Board: ")
.append(oldEdu.getBoard())
.append(" → ")
.append(newEdu.getBoard())
.append("; ");

if (!Objects.equals(oldEdu.getInstitutionName(), newEdu.getInstitutionName()))
details.append("Institution: ")
.append(oldEdu.getInstitutionName())
.append(" → ")
.append(newEdu.getInstitutionName())
.append("; ");
}

// Detect Added
if (newEdu.getId() == null) {
details.append("Education Added: ")
.append(newEdu.getHighestQualification())
.append(" - ")
.append(newEdu.getInstitutionName())
.append("; ");
}
}

// Detect Removed
for (PrincipalEducation oldEdu : oldList) {
if (!newMap.containsKey(oldEdu.getId())) {
details.append("Education Removed: ")
.append(oldEdu.getHighestQualification())
.append(" - ")
.append(oldEdu.getInstitutionName())
.append("; ");
}
}

return details.toString();
}
}