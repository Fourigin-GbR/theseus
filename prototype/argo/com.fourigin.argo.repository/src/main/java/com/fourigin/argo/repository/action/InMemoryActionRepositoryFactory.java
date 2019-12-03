package com.fourigin.argo.repository.action;

import java.util.HashMap;
import java.util.Map;

public class InMemoryActionRepositoryFactory implements ActionRepositoryFactory {

    private Map<String, ActionRepository> projectsActionRepositories = new HashMap<>();

    @Override
    public ActionRepository getInstance(String project) {
        ActionRepository repository = projectsActionRepositories.get(project);
        if (repository == null) {
            repository = new InMemoryActionRepository();
            projectsActionRepositories.put(project, repository);
        }

        return repository;
    }
}
