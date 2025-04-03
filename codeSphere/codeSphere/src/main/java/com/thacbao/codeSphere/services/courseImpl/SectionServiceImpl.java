package com.thacbao.codeSphere.services.courseImpl;

import com.thacbao.codeSphere.configurations.JwtFilter;
import com.thacbao.codeSphere.constants.CodeSphereConstants;
import com.thacbao.codeSphere.data.repository.course.SectionRepository;
import com.thacbao.codeSphere.dto.request.course.SectionRequest;
import com.thacbao.codeSphere.dto.response.ApiResponse;
import com.thacbao.codeSphere.dto.response.course.SectionDTO;
import com.thacbao.codeSphere.dto.response.course.VideoDTO;
import com.thacbao.codeSphere.entities.reference.Section;
import com.thacbao.codeSphere.exceptions.common.NotFoundException;
import com.thacbao.codeSphere.exceptions.user.PermissionException;
import com.thacbao.codeSphere.services.SectionService;
import com.thacbao.codeSphere.services.VideoService;
import com.thacbao.codeSphere.services.redis.RedisService;
import com.thacbao.codeSphere.utils.CodeSphereResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class SectionServiceImpl implements SectionService {

    private final SectionRepository sectionRepository;
    private final ModelMapper modelMapper;
    private final VideoService videoService;
    private final JwtFilter jwtFilter;
    private final RedisService redisService;

    @Override
    public ResponseEntity<ApiResponse> createSection(SectionRequest request) {
        if (jwtFilter.isAdmin() || jwtFilter.isManager()){
            Section section = modelMapper.map(request, Section.class);
            section.setId(null);
            sectionRepository.save(section);
            redisService.delete("courseDetails:");
            return CodeSphereResponses.generateResponse(null, "Create section success", HttpStatus.CREATED);
        }
       throw new PermissionException(CodeSphereConstants.PERMISSION_DENIED);
    }

    @Override
    public List<SectionDTO> getAllSection(Integer courseId) {
        List<Section> sections = sectionRepository.findByCourseId(courseId);
        return sections.stream().map(section -> {
            List<VideoDTO> videoDTOs = videoService.getAllVideo(section.getId());
                    return new SectionDTO(section, videoDTOs);
        }).collect(Collectors.toList());
    }

    @Override
    public ResponseEntity<ApiResponse> viewSectionDetails(Integer id) {
        if (jwtFilter.isAdmin() || jwtFilter.isManager()){
            Section section = sectionRepository.findById(id).orElseThrow(
                    () -> new NotFoundException("Section not found")
            );
            SectionDTO result = new SectionDTO(section);
            return CodeSphereResponses.generateResponse(result, "View Section success", HttpStatus.OK);
        }
        throw new PermissionException(CodeSphereConstants.PERMISSION_DENIED);
    }

    @Override
    public ResponseEntity<ApiResponse> updateSection(Integer sectionId, SectionRequest request) {
        Section section = sectionRepository.findById(sectionId).orElseThrow(
                () -> new NotFoundException("Section not found")
        );
        if (jwtFilter.isAdmin() || jwtFilter.isManager()){
            section.setTitle(request.getTitle());
            section.setDescription(request.getDescription());
            section.setOrderIndex(request.getOrderIndex());
            sectionRepository.save(section);
            redisService.delete("courseDetails:");
            return CodeSphereResponses.generateResponse(null, "Update section success", HttpStatus.OK);
        }
        throw new PermissionException(CodeSphereConstants.PERMISSION_DENIED);
    }

    @Override
    public ResponseEntity<ApiResponse> deleteSection(Integer sectionId) {
        if (jwtFilter.isAdmin() || jwtFilter.isManager()){
            Section section = sectionRepository.findById(sectionId).orElseThrow(
                    () -> new NotFoundException("Section not found")
            );
            sectionRepository.delete(section);
            redisService.delete("courseDetails:");
            return CodeSphereResponses.generateResponse(null, "Delete section success", HttpStatus.OK);
        }
        throw new PermissionException(CodeSphereConstants.PERMISSION_DENIED);
    }
}
