package com.thacbao.codeSphere.services.blogImpl;

import com.thacbao.codeSphere.data.repository.TagRepository;
import com.thacbao.codeSphere.entity.reference.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class TagService {

    @Autowired
    private TagRepository tagRepository;

    /**
     * Liên kết với blogService, tạo mới hoặc get tag
     * @param tagNames
     * @return
     */
    public Set<Tag> getOrCreateTags(Set<String> tagNames) {
        Set<Tag> tags = new HashSet<>();

        if (tagNames != null && !tagNames.isEmpty()) {
            tagNames.forEach(tagName -> {
                Tag tag = tagRepository.findByName(tagName);
                if (tag != null) {
                    tags.add(tag);
                }
                else {
                    Tag newTag = new Tag();
                    newTag.setName(tagName);
                    tagRepository.save(newTag);
                    tags.add(newTag);
                }
            });
        }
        return tags;
    }
}
