package repository;

import jakarta.persistence.EntityManager;
import model.Report;

public class ReportRepository extends AbstractRepository<Report> {

    public ReportRepository(EntityManager em) {
        super(em, Report.class);
    }

}
