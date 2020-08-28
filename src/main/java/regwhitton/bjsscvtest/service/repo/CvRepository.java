package regwhitton.bjsscvtest.service.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import regwhitton.bjsscvtest.model.Cv;

public interface CvRepository extends JpaRepository<Cv, Long> {
}