package regwhitton.bjsscvtest.service.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import regwhitton.bjsscvtest.model.Employment;

public interface EmploymentRepository extends JpaRepository<Employment, Long> {
}