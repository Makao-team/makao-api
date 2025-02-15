package kr.co.makao.repository;

import kr.co.makao.entity.Image;
import kr.co.makao.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ImageRepository extends JpaRepository<Image, Integer> {
    List<Image> findAllByProduct(Product product);
}
