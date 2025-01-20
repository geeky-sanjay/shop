package com.onlineshop.shop.product.services;

import com.onlineshop.shop.product.dtos.ImageDto;
import com.onlineshop.shop.product.models.Image;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface IImageService {
    Image getImageById(Long id);
    void deleteImageById(Long id);
    List<ImageDto> saveImages(Long productId, List<MultipartFile> files);
    void updateImage(MultipartFile file,  Long imageId);
}
