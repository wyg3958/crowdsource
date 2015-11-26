package de.asideas.crowdsource.controller;

import de.asideas.crowdsource.domain.exception.ResourceNotFoundException;
import de.asideas.crowdsource.domain.model.CommentEntity;
import de.asideas.crowdsource.domain.model.ProjectEntity;
import de.asideas.crowdsource.domain.model.UserEntity;
import de.asideas.crowdsource.domain.presentation.Comment;
import de.asideas.crowdsource.repository.CommentRepository;
import de.asideas.crowdsource.repository.ProjectRepository;
import de.asideas.crowdsource.security.Roles;
import de.asideas.crowdsource.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@Secured(Roles.ROLE_USER)
@RequestMapping("/project/{projectId}")
public class CommentController {

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private UserService userService;

    @RequestMapping(value = "/comments", method = RequestMethod.GET)
    public List<Comment> comments(@PathVariable String projectId) {

        final ProjectEntity projectEntity = project(projectId);
        return commentRepository.findByProject(projectEntity).stream().map(Comment::new).collect(Collectors.toList());
    }

    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(value = "/comment", method = RequestMethod.POST)
    public void storeComment(@PathVariable String projectId, Principal principal, @Valid @RequestBody Comment comment) {

        final ProjectEntity project = project(projectId);
        final UserEntity user = userService.getUserByEmail(principal.getName());
        CommentEntity commentEntity = new CommentEntity();
        commentEntity.setComment(comment.getComment());
        commentEntity.setProject(project);
        commentEntity.setUser(user);
        commentRepository.save(commentEntity);
    }

    private ProjectEntity project(String projectId) {

        final ProjectEntity projectEntity = projectRepository.findOne(projectId);
        if (projectEntity == null) {
            throw new ResourceNotFoundException();
        }
        return projectEntity;
    }
}
