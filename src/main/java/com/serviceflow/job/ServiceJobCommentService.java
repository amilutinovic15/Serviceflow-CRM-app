package com.serviceflow.job;

import com.serviceflow.common.NotFoundException;
import com.serviceflow.user.AppUser;
import com.serviceflow.user.AppUserRepository;
import java.util.List;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ServiceJobCommentService {

    private final ServiceJobRepository serviceJobRepository;
    private final ServiceJobCommentRepository serviceJobCommentRepository;
    private final AppUserRepository appUserRepository;

    public ServiceJobCommentService(
            ServiceJobRepository serviceJobRepository,
            ServiceJobCommentRepository serviceJobCommentRepository,
            AppUserRepository appUserRepository
    ) {
        this.serviceJobRepository = serviceJobRepository;
        this.serviceJobCommentRepository = serviceJobCommentRepository;
        this.appUserRepository = appUserRepository;
    }

    @Transactional(readOnly = true)
    public List<ServiceJobCommentResponse> findByJobId(Long jobId) {
        ensureJobExists(jobId);
        return serviceJobCommentRepository.findByServiceJobIdOrderByCreatedAtDesc(jobId)
                .stream()
                .map(ServiceJobCommentResponse::from)
                .toList();
    }

    public ServiceJobCommentResponse addComment(Long jobId, ServiceJobCommentRequest request) {
        ServiceJob job = serviceJobRepository.findById(jobId)
                .orElseThrow(() -> new NotFoundException("Service job with id %d was not found".formatted(jobId)));
        String email = currentUserEmail();
        AppUser author = email == null ? null : appUserRepository.findByEmail(email).orElse(null);

        ServiceJobComment comment = new ServiceJobComment();
        comment.setServiceJob(job);
        comment.setAuthor(author);
        comment.setAuthorEmail(email);
        comment.setBody(request.body().trim());

        return ServiceJobCommentResponse.from(serviceJobCommentRepository.save(comment));
    }

    public void deleteComment(Long jobId, Long commentId) {
        ensureJobExists(jobId);
        ServiceJobComment comment = serviceJobCommentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Comment with id %d was not found".formatted(commentId)));

        if (!comment.getServiceJob().getId().equals(jobId)) {
            throw new NotFoundException("Comment with id %d was not found for job %d".formatted(commentId, jobId));
        }

        serviceJobCommentRepository.delete(comment);
    }

    private void ensureJobExists(Long jobId) {
        if (!serviceJobRepository.existsById(jobId)) {
            throw new NotFoundException("Service job with id %d was not found".formatted(jobId));
        }
    }

    private String currentUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }

        String name = authentication.getName();
        return name == null || "anonymousUser".equals(name) ? null : name;
    }
}
