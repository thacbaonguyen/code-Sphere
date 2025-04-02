package com.thacbao.codeSphere.controllers.course;

import com.thacbao.codeSphere.dto.request.course.CartRequest;
import com.thacbao.codeSphere.dto.response.ApiResponse;
import com.thacbao.codeSphere.services.CartService;
import com.thacbao.codeSphere.utils.CodeSphereResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;
    @PostMapping("/insert")
    public ResponseEntity<ApiResponse> insert(@Valid @RequestBody CartRequest request, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            bindingResult.getFieldErrors().forEach(fieldError -> {
                errors.put(fieldError.getField(), fieldError.getDefaultMessage());
            });
            return CodeSphereResponses.generateResponse(errors, "Validation failed", HttpStatus.BAD_REQUEST);
        }
        return cartService.addNewProduct(request);
    }

    @GetMapping("/all")
    public ResponseEntity<ApiResponse> getAll() {
        return cartService.getCart();
    }

    @DeleteMapping("/delete/{courseId}")
    public ResponseEntity<ApiResponse> delete(@PathVariable("courseId") Integer courseid) {
        return cartService.deleteProductFromCart(courseid);
    }
}
