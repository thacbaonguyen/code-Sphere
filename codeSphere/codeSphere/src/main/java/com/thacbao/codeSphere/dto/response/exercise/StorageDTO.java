package com.thacbao.codeSphere.dto.response.exercise;

import com.thacbao.codeSphere.entities.reference.SolutionStorage;
import lombok.Data;

@Data
public class StorageDTO {
    private Integer id;

    private String fileName;

    private String fileType;

    private Integer fileSize;

    public StorageDTO(SolutionStorage solutionStorage) {
        this.id = solutionStorage.getId();
        this.fileName = solutionStorage.getFilename();
        this.fileType = solutionStorage.getFileType();
        this.fileSize = solutionStorage.getFileSize();
    }
}
