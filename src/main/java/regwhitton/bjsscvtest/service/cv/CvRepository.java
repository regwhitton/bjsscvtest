package regwhitton.bjsscvtest.service.cv;

import org.springframework.data.jpa.repository.JpaRepository;
import regwhitton.bjsscvtest.model.Cv;

interface CvRepository extends JpaRepository<Cv, Long> {
}