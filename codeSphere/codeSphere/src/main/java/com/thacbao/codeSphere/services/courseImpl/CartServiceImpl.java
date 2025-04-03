package com.thacbao.codeSphere.services.courseImpl;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thacbao.codeSphere.configurations.JwtFilter;
import com.thacbao.codeSphere.constants.CodeSphereConstants;
import com.thacbao.codeSphere.data.repository.course.CartRepository;
import com.thacbao.codeSphere.data.repository.course.CourseReviewRepository;
import com.thacbao.codeSphere.data.repository.course.SectionRepository;
import com.thacbao.codeSphere.data.repository.course.VideoRepository;
import com.thacbao.codeSphere.data.repository.user.UserRepository;
import com.thacbao.codeSphere.dto.request.course.CartRequest;
import com.thacbao.codeSphere.dto.response.ApiResponse;
import com.thacbao.codeSphere.dto.response.course.CartDTO;
import com.thacbao.codeSphere.entities.core.User;
import com.thacbao.codeSphere.entities.reference.Section;
import com.thacbao.codeSphere.entities.reference.ShoppingCart;
import com.thacbao.codeSphere.exceptions.common.AlreadyException;
import com.thacbao.codeSphere.exceptions.common.NotFoundException;
import com.thacbao.codeSphere.services.CartService;
import com.thacbao.codeSphere.services.redis.RedisService;
import com.thacbao.codeSphere.utils.CodeSphereResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final ModelMapper modelMapper;
    private final UserRepository userRepository;
    private final JwtFilter jwtFilter;
    private final AmazonS3 amazonS3;
    private final CourseReviewRepository courseReviewRepository;
    private final RedisService redisService;

    @Value("${cloud.aws.s3.bucketFeature}")
    private String bucketFeature;
    @Override
    public ResponseEntity<ApiResponse> addNewProduct(CartRequest request) {
        User user = userRepository.findByUsername(jwtFilter.getCurrentUsername()).orElseThrow(
                () -> new NotFoundException(CodeSphereConstants.User.USER_NOT_FOUND)
        );
        BigInteger courseCount = cartRepository.countCourse(jwtFilter.getCurrentUsername(), request.getCourseId());
        if (courseCount.compareTo(BigInteger.ZERO) > 0) {
            throw new AlreadyException("This course already exists");
        }
        ShoppingCart cart = modelMapper.map(request, ShoppingCart.class);
        cart.setId(null);
        cart.setUser(user);
        cartRepository.save(cart);
        redisService.delete("Cart:" + jwtFilter.getCurrentUsername());
        return CodeSphereResponses.generateResponse(null, "Insert course on cart success", HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<ApiResponse> getCart() {
        String cacheKey = "Cart:" + jwtFilter.getCurrentUsername();
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            String cachedData = (String) redisService.get(cacheKey);
            if (cachedData != null) {
                log.info("cache all {}", cacheKey);
                // Chuyển đổi dữ liệu từ cache về đối tượng mới
                List<CartDTO> cachedCartDTOS = objectMapper.readValue(cachedData, new TypeReference<List<CartDTO>>() {});
                return CodeSphereResponses.generateResponse(cachedCartDTOS, "All contribute successfully", HttpStatus.OK);
            }
            List<CartDTO> cartDTOS = cartRepository.findByUser(jwtFilter.getCurrentUsername())
                    .stream().map(item -> {
                        CartDTO cartDTO = new CartDTO(item);
                        String fileName = cartDTO.getCourseBriefDTO().getThumbnail();
                        cartDTO.getCourseBriefDTO().setImage(viewImageFromS3(fileName));
                        int videoCount = 0;
                        for (Section sec : item.getCourse().getSections()){
                            videoCount+= sec.getVideos().size();
                        }
                        cartDTO.getCourseBriefDTO().setVideoCount(videoCount);
                        cartDTO.getCourseBriefDTO().setSectionCount(item.getCourse().getSections().size());
                        cartDTO.getCourseBriefDTO().setRating(avgRating(cartDTO.getCourseBriefDTO().getId()));
                        return cartDTO;
                    }).toList();
            String jsonData = objectMapper.writeValueAsString(cartDTOS);
            redisService.set(cacheKey, jsonData, 24, TimeUnit.HOURS);
            return CodeSphereResponses.generateResponse(cartDTOS, "Get cart success", HttpStatus.OK);
        }
        catch (Exception e) {
            log.error(e.getMessage());
            return CodeSphereResponses.generateResponse(null, e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @Override
    public ResponseEntity<ApiResponse> deleteProductFromCart(Integer courseId) {
        try {
            cartRepository.deleteProductFromCart(jwtFilter.getCurrentUsername(), courseId);
            redisService.delete("Cart:" + jwtFilter.getCurrentUsername());
            return CodeSphereResponses.generateResponse(null, "Delete course on cart success", HttpStatus.OK);
        }
        catch (Exception e) {
            log.error("Error delete course on cart {}",e.getMessage(), e.getCause());
            return CodeSphereResponses.generateResponse(null, e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    private URL viewImageFromS3(String fileName){
        try {
            Date expiration = new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24);
            GeneratePresignedUrlRequest preSignedUrlRequest = new GeneratePresignedUrlRequest
                    (bucketFeature, fileName, HttpMethod.GET)
                    .withExpiration(expiration);
            return amazonS3.generatePresignedUrl(preSignedUrlRequest);
        }
        catch (Exception ex){
            throw new NotFoundException("Cannot find image with name " + fileName);
        }
    }

    private double avgRating(Integer courseId){
        return courseReviewRepository.averageRating(courseId);
    }
}
