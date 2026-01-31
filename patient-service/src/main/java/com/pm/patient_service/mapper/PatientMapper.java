package com.pm.patient_service.mapper;

import com.pm.patient_service.dto.PatientResponseDTO;
import com.pm.patient_service.model.Patient;

public class PatientMapper {
    public static PatientResponseDTO toDTO(Patient patient){
        PatientResponseDTO patientDTO = new PatientResponseDTO();
        patientDTO.setId(patient.getId().toString());
        patientDTO.setId(patient.getEmail());
        patientDTO.setId(patient.getAddress());
        patientDTO.setId(patient.getDateOfBirth().toString());
        patientDTO.setId(patient.getName());

        return patientDTO;
    }
}
