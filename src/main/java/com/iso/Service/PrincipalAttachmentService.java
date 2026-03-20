package com.iso.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.iso.Model.Principal;
import com.iso.Model.PrincipalAttachment;
import com.iso.Model.AttachmentType;

@Service
public class PrincipalAttachmentService {

    /**
     * Save a single file and add it to principal's attachment list
     */
    public void saveSingleFile(MultipartFile file,
                               Principal principal,
                               AttachmentType type,
                               String uploadDir) throws IOException {

        if (file != null && !file.isEmpty()) {

            String originalFilename = file.getOriginalFilename();
            String uniqueFileName = UUID.randomUUID() + "_" + originalFilename;

            Path filePath = Paths.get(uploadDir + uniqueFileName);
            Files.createDirectories(filePath.getParent());
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            PrincipalAttachment attachment = new PrincipalAttachment();
            attachment.setPrincipal(principal);
            attachment.setAttachmentType(type);
            attachment.setFilePath(filePath.toString());

            principal.getAttachments().add(attachment);
        }
    }

    /**
     * Save multiple files and add each to principal's attachment list
     */
    public void saveMultipleFiles(MultipartFile[] files,
                                  Principal principal,
                                  AttachmentType type,
                                  String uploadDir) throws IOException {

        if (files != null) {
            for (MultipartFile file : files) {
                saveSingleFile(file, principal, type, uploadDir);
            }
        }
    }
}