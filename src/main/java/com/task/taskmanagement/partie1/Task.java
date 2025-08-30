package com.task.taskmanagement.partie1;

import java.util.ArrayList;
import java.util.List;

public class Task {
    private String id;
    private String description;
    private int estimatedDuration;
    private TaskStatus status;
    private TaskType type;
    private String comment;
    private Member assignedMember;
    private List<Tool> usedTools;
    private boolean isProfessional;
    private List<Task> subTasks;
    private Task parentTask;

    public Task(String id, String description, TaskType type, boolean isProfessional, int estimatedDuration,
            TaskStatus status) {
        this.id = id;
        this.description = description;
        this.isProfessional = isProfessional;
        this.estimatedDuration = estimatedDuration;
        this.status = status;
        this.type = type;
        this.usedTools = new ArrayList<>();
        this.subTasks = new ArrayList<>();
    }

    // implementation des getters et setters
    public String getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getEstimatedDuration() {
        return estimatedDuration;
    }

    public void setEstimatedDuration(int estimatedDuration) {
        this.estimatedDuration = estimatedDuration;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;

        // Si le statut de la tâche est terminé, toutes les sous-tâches sont également
        // marquées comme terminées
        if (status == TaskStatus.DONE) {
            for (Tool tool : usedTools) {
                tool.setAvailable(true);
            }
        }

        // Si c'est une sous-tâche, vérifier si toutes les sous-tâches du parent sont
        // terminées
        if (parentTask != null) {
            boolean allDone = true;
            for (Task siblingTask : parentTask.getSubTasks()) {
                if (siblingTask.getStatus() != TaskStatus.DONE) {
                    allDone = false;
                    break;
                }
            }
            if (allDone) {
                parentTask.setStatus(TaskStatus.DONE);
            }
        }
    }

    public TaskType getType() {
        return type;
    }

    public void setType(TaskType type) {
        this.type = type;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Member getMember() {
        return assignedMember;
    }

    public void setMember(Member member) {
        this.assignedMember = member;
    }

    public boolean isProfessional() {
        return isProfessional;
    }

    public Task getParentTask() {
        return parentTask;
    }

    public void setParentTask(Task parentTask) {
        this.parentTask = parentTask;
    }

    public List<Task> getSubTasks() {
        return new ArrayList<>(subTasks);
    }

    public void addSubTask(Task Task) {
        subTasks.add(Task);
        Task.setParentTask(this);
    }

    // Methode de manipulation de la tache
    public void assignTo(Member member) {
        this.assignedMember = member;

    }

    public void addTool(Tool tool) {
        // verifier si l'outil n'est pas deja utilisé
        if (!usedTools.contains(tool)) {
            usedTools.add(tool);
            // changer la disponibilité de l'outil
            tool.setAvailable(false);

        }
    }

    // recuperer la liste des outils utilisés
    public List<Tool> getUsedTools() {
        return new ArrayList<>(usedTools);
    }

    // ajouter un commentaire a la tache
    public void addComment(String newComment) {
        if (this.comment == null || this.comment.isEmpty()) {
            this.comment = newComment;
        } else {
            this.comment = this.comment + "\n" + newComment;
        }
    }

    public int getPoints() {
        return type.getPoints(isProfessional);
    }

    public int calculateTotalPoints() {
        if (subTasks.isEmpty()) {
            return getPoints();
        } else {
            int totalPoints = 0;
            for (Task subTask : subTasks) {
                totalPoints += subTask.calculateTotalPoints();
            }
            return totalPoints;
        }
    }

    public double calculateProgress() {
        if (status == TaskStatus.DONE) {
            return 100.0;
        }
        if (subTasks.isEmpty()) {
            return status == TaskStatus.IN_PROGRESS ? 50.0 : 0.0;
        } else {
            double totalProgress = 0.0;
            for (Task subTask : subTasks) {
                totalProgress += subTask.calculateProgress();
            }
            return totalProgress / subTasks.size();
        }
    }

    // Met à jour le statut en fonction de l'état des sous-tâches
    public void updateStatusFromSubTasks() {
        if (subTasks.isEmpty())
            return;

        boolean allDone = true;

        for (Task subTask : subTasks) {
            subTask.updateStatusFromSubTasks(); // Appel récursif
            if (subTask.getStatus() != TaskStatus.DONE) {
                allDone = false;
            }
        }

        if (allDone) {
            this.status = TaskStatus.DONE;
        } else {
            this.status = TaskStatus.IN_PROGRESS;
        }

        if (parentTask != null) {
            parentTask.updateStatusFromSubTasks();
        }
    }

    // afficher les infos d'une tache
    public void displayInfo() {

        this.updateStatusFromSubTasks(); // Synchronise le statut avant affichage

        System.out.println("ID: " + id);
        System.out.println("Description: " + description);
        System.out.println("Type: " + type + " (" + (isProfessional ? "Professionnel" : "Basic") + ")");
        System.out.println("Points: " + getPoints());
        System.out.println("Durée estimée: " + estimatedDuration + " heures");
        System.out.println("Statut: " + status);
        System.out.println("Progrès: " + String.format("%.2f", calculateProgress()) + "%");
        System.out.println("Commentaire: " + comment);
        System.out.println("Membre assigné: " + (assignedMember != null ? assignedMember.getName() : "Non assigné"));
        System.out.println("Nombre d'outils utilisés: " + usedTools.size());
        System.out.println("Sous-tâches: " + subTasks.size());
    }

}
